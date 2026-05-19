package com.IHEC.Recruit.service;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CandidatureService {

    private final OffreRepository offreRepository;
    private final CandidatRepository candidatRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final CandidatureRepository candidatureRepository;

    public CandidatureService(OffreRepository offreRepository,
                              CandidatRepository candidatRepository,
                              EntrepriseRepository entrepriseRepository,
                              CandidatureRepository candidatureRepository) {
        this.offreRepository = offreRepository;
        this.candidatRepository = candidatRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.candidatureRepository = candidatureRepository;
    }

    // ========== POSTULER / RETIRER ==========

    public void postulerOffre(int idCandidat, Long idOffre) {
        Candidat candidat = candidatRepository.findById(idCandidat)
                .orElseThrow(() -> new IllegalArgumentException("Candidat non trouve"));

        Offre offre = offreRepository.findById(idOffre)
                .orElseThrow(() -> new IllegalArgumentException("Offre non trouvee"));

        if (offre.estExpiree()) {
            throw new IllegalStateException("Cette offre est expiree");
        }

        CandidatureId candidatureId = new CandidatureId(idOffre, idCandidat);
        if (candidatureRepository.existsById(candidatureId)) {
            throw new IllegalStateException("Vous avez deja postule a cette offre");
        }

        candidatureRepository.save(new Candidature(offre, candidat));
    }

    public void retirerCandidature(int idCandidat, Long idOffre) {
        Candidature candidature = candidatureRepository
                .findById(new CandidatureId(idOffre, idCandidat))
                .orElseThrow(() -> new IllegalStateException("Vous n'avez pas postule a cette offre"));

        candidatureRepository.delete(candidature);
    }

    // ========== CONSULTATION CANDIDAT ==========

    public List<Offre> getCandidaturesCandidat(int idCandidat) {
        verifierCandidatExiste(idCandidat);
        return candidatureRepository.findByCandidat_Id(idCandidat).stream()
                .map(Candidature::getOffre)
                .toList();
    }

    public List<Candidature> getCandidaturesDetailsCandidat(int idCandidat) {
        verifierCandidatExiste(idCandidat);
        return candidatureRepository.findByCandidat_Id(idCandidat);
    }

    public int getNombreCandidaturesCandidat(int idCandidat) {
        verifierCandidatExiste(idCandidat);
        return Math.toIntExact(candidatureRepository.countByCandidat_Id(idCandidat));
    }

    // ========== CONSULTATION ENTREPRISE ==========

    public List<Candidat> getCandidatsOffre(Long idOffre) {
        verifierOffreExiste(idOffre);
        return candidatureRepository.findByOffre_Id(idOffre).stream()
                .map(Candidature::getCandidat)
                .toList();
    }

    public List<Candidat> getCandidatsOffrePourEntreprise(Long idOffre, Entreprise entreprise) {
        verifierOffreAppartientEntreprise(idOffre, entreprise);
        return candidatureRepository.findByOffre_Id(idOffre).stream()
                .map(Candidature::getCandidat)
                .toList();
    }

    public List<Candidature> getCandidaturesOffrePourEntreprise(Long idOffre, Entreprise entreprise) {
        verifierOffreAppartientEntreprise(idOffre, entreprise);
        return candidatureRepository.findByOffre_Id(idOffre);
    }

    public int getTotalCandidaturesParEntreprise(Long idEntreprise) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
                .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvee"));
        return Math.toIntExact(candidatureRepository.countByOffre_Entreprise(entreprise));
    }

    // ========== STATUTS ==========

    public void marquerCommeContacte(Long idOffre, int idCandidat, Entreprise entreprise) {
        changerStatutCandidature(idOffre, idCandidat, entreprise, CandidatureStatus.CONTACTED);
    }

    public void marquerCommeRefuse(Long idOffre, int idCandidat, Entreprise entreprise) {
        changerStatutCandidature(idOffre, idCandidat, entreprise, CandidatureStatus.REFUSED);
    }

    public void remettreEnAttente(Long idOffre, int idCandidat, Entreprise entreprise) {
        changerStatutCandidature(idOffre, idCandidat, entreprise, CandidatureStatus.PENDING);
    }

    private void changerStatutCandidature(Long idOffre, int idCandidat,
                                          Entreprise entreprise,
                                          CandidatureStatus nouveauStatut) {
        verifierOffreAppartientEntreprise(idOffre, entreprise);

        Candidature candidature = candidatureRepository
                .findById(new CandidatureId(idOffre, idCandidat))
                .orElseThrow(() -> new IllegalArgumentException("Candidature non trouvee"));

        candidature.setStatus(nouveauStatut);
        candidatureRepository.save(candidature);
    }

    // ========== WISHLIST ==========

    public void ajouterWishlist(Long idEntreprise, int idCandidat) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
                .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvee"));

        Candidat candidat = candidatRepository.findById(idCandidat)
                .orElseThrow(() -> new IllegalArgumentException("Candidat non trouve"));

        if (entreprise.getWishlist().contains(candidat)) {
            throw new IllegalStateException("Ce candidat est deja dans la wishlist");
        }

        entreprise.getWishlist().add(candidat);
        entrepriseRepository.save(entreprise);
    }

    public void retirerWishlist(Long idEntreprise, int idCandidat) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
                .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvee"));

        Candidat candidat = candidatRepository.findById(idCandidat)
                .orElseThrow(() -> new IllegalArgumentException("Candidat non trouve"));

        entreprise.getWishlist().remove(candidat);
        entrepriseRepository.save(entreprise);
    }

    public List<Candidat> getWishlist(Long idEntreprise) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
                .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvee"));
        return entreprise.getWishlist();
    }

    public int getNombreWishlist(Long idEntreprise) {
        return getWishlist(idEntreprise).size();
    }

    public Map<Integer, List<Offre>> getOffresParCandidatWishlist(Long idEntreprise,
                                                                  List<Candidat> wishlist) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
                .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvee"));

        List<Offre> offresEntreprise = entreprise.getOffresPubliees();
        Map<Integer, List<Offre>> result = new HashMap<>();

        for (Candidat candidat : wishlist) {
            List<Offre> offresCandidat = candidatureRepository.findByCandidat_Id(candidat.getId()).stream()
                    .map(Candidature::getOffre)
                    .filter(offresEntreprise::contains)
                    .toList();

            if (!offresCandidat.isEmpty()) {
                result.put(candidat.getId(), offresCandidat);
            }
        }

        return result;
    }

    // ========== VALIDATION INTERNE ==========

    private void verifierCandidatExiste(int idCandidat) {
        if (!candidatRepository.existsById(idCandidat)) {
            throw new IllegalArgumentException("Candidat non trouve");
        }
    }

    private void verifierOffreExiste(Long idOffre) {
        if (!offreRepository.existsById(idOffre)) {
            throw new IllegalArgumentException("Offre non trouvee");
        }
    }

    private Offre verifierOffreAppartientEntreprise(Long idOffre, Entreprise entreprise) {
        Offre offre = offreRepository.findById(idOffre)
                .orElseThrow(() -> new IllegalArgumentException("Offre non trouvee"));

        if (!offre.getEntreprise().equals(entreprise)) {
            throw new SecurityException("Acces refuse");
        }

        return offre;
    }
}
