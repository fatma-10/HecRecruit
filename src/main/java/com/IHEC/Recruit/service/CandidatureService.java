package com.IHEC.Recruit.service;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CandidatureService {

    private final OffreRepository offreRepository;
    private final CandidatRepository candidatRepository;
    private final EntrepriseRepository entrepriseRepository;

    public CandidatureService(OffreRepository offreRepository,
                               CandidatRepository candidatRepository,
                               EntrepriseRepository entrepriseRepository) {
        this.offreRepository = offreRepository;
        this.candidatRepository = candidatRepository;
        this.entrepriseRepository = entrepriseRepository;
    }

    // ========== POSTULER / RETIRER ==========

    /**
     * Candidat postule à une offre.
     * La relation ManyToMany est gérée via l'entité Offre (table candidature en DB).
     */
    public void postulerOffre(int idCandidat, Long idOffre) {
        Candidat candidat = candidatRepository.findById(idCandidat)
            .orElseThrow(() -> new IllegalArgumentException("Candidat non trouvé"));

        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));

        if (offre.estExpiree()) {
            throw new IllegalStateException("Cette offre est expirée");
        }

        if (offre.getCandidatures().contains(candidat)) {
            throw new IllegalStateException("Vous avez déjà postulé à cette offre");
        }

        // Ajouter des deux côtés (relation bidirectionnelle)
        offre.getCandidatures().add(candidat);
        candidat.getCandidaturesEnCours().add(offre);

        // Un seul save suffit grâce au CascadeType et à la relation mappedBy
        offreRepository.save(offre);
    }

    /**
     * Candidat retire sa candidature.
     */
    public void retirerCandidature(int idCandidat, Long idOffre) {
        Candidat candidat = candidatRepository.findById(idCandidat)
            .orElseThrow(() -> new IllegalArgumentException("Candidat non trouvé"));

        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));

        if (!offre.getCandidatures().contains(candidat)) {
            throw new IllegalStateException("Vous n'avez pas postulé à cette offre");
        }

        offre.getCandidatures().remove(candidat);
        candidat.getCandidaturesEnCours().remove(offre);
        offreRepository.save(offre);
    }

    // ========== CONSULTATION ==========

    /**
     * Toutes les candidatures d'un candidat (offres auxquelles il a postulé).
     */
    public List<Offre> getCandidaturesCandidat(int idCandidat) {
        Candidat candidat = candidatRepository.findById(idCandidat)
            .orElseThrow(() -> new IllegalArgumentException("Candidat non trouvé"));
        return candidat.getCandidaturesEnCours();
    }

    /**
     * Tous les candidats ayant postulé à une offre (vue entreprise).
     */
    public List<Candidat> getCandidatsOffre(Long idOffre) {
        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));
        return offre.getCandidatures();
    }

    /**
     * Entreprise supprime la candidature d'un candidat de son offre.
     */
    public void supprimerCandidatureOffre(Long idOffre, int idCandidat, Entreprise entreprise) {
        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));

        if (!offre.getEntreprise().equals(entreprise)) {
            throw new SecurityException("Accès refusé");
        }

        Candidat candidat = candidatRepository.findById(idCandidat)
            .orElseThrow(() -> new IllegalArgumentException("Candidat non trouvé"));

        offre.getCandidatures().remove(candidat);
        candidat.getCandidaturesEnCours().remove(offre);
        offreRepository.save(offre);
    }

    // ========== WISHLIST ==========

    /**
     * Entreprise ajoute un candidat à sa wishlist.
     */
    public void ajouterWishlist(Long idEntreprise, int idCandidat) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
            .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvée"));

        Candidat candidat = candidatRepository.findById(idCandidat)
            .orElseThrow(() -> new IllegalArgumentException("Candidat non trouvé"));

        if (entreprise.getWishlist().contains(candidat)) {
            throw new IllegalStateException("Ce candidat est déjà dans la wishlist");
        }

        entreprise.getWishlist().add(candidat);
        entrepriseRepository.save(entreprise);
    }

    /**
     * Entreprise retire un candidat de sa wishlist.
     */
    public void retirerWishlist(Long idEntreprise, int idCandidat) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
            .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvée"));

        Candidat candidat = candidatRepository.findById(idCandidat)
            .orElseThrow(() -> new IllegalArgumentException("Candidat non trouvé"));

        entreprise.getWishlist().remove(candidat);
        entrepriseRepository.save(entreprise);
    }

    /**
     * Retourne la wishlist complète d'une entreprise.
     */
    public List<Candidat> getWishlist(Long idEntreprise) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
            .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvée"));
        return entreprise.getWishlist();
    }
}
