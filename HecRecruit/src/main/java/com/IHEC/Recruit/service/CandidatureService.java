package com.IHEC.Recruit.service;

import com.IHEC.Recruit.entity.*;
import com.IHEC.Recruit.exception.BusinessException;
import com.IHEC.Recruit.exception.ForbiddenException;
import com.IHEC.Recruit.exception.NotFoundException;
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

    // ========== POSTULER ==========

    public void postulerOffre(int idCandidat, Long idOffre) {
        Candidat candidat = candidatRepository.findById(idCandidat)
                .orElseThrow(() -> new NotFoundException("Candidat non trouvé"));
        Offre offre = offreRepository.findById(idOffre)
                .orElseThrow(() -> new NotFoundException("Offre non trouvée"));

        if (offre.estExpiree())
            throw new BusinessException("Cette offre est expirée");
        if (offre.getCandidatures().contains(candidat))
            throw new BusinessException("Vous avez déjà postulé à cette offre");

        // Offre est le cote proprietaire de la relation ManyToMany
        offre.getCandidatures().add(candidat);
        offreRepository.save(offre);
    }

    public void retirerCandidature(int idCandidat, Long idOffre) {
        Candidat candidat = candidatRepository.findById(idCandidat)
                .orElseThrow(() -> new NotFoundException("Candidat non trouvé"));
        Offre offre = offreRepository.findById(idOffre)
                .orElseThrow(() -> new NotFoundException("Offre non trouvée"));

        if (!offre.getCandidatures().contains(candidat))
            throw new BusinessException("Vous n'avez pas postulé à cette offre");

        offre.getCandidatures().remove(candidat);
        offreRepository.save(offre);
    }

    // ========== CONSULTATION ==========

    public List<Offre> getCandidaturesCandidat(int idCandidat) {
        Candidat candidat = candidatRepository.findById(idCandidat)
                .orElseThrow(() -> new NotFoundException("Candidat non trouvé"));
        return candidat.getCandidaturesEnCours();
    }

    public List<Candidat> getCandidatsOffre(Long idOffre) {
        Offre offre = offreRepository.findById(idOffre)
                .orElseThrow(() -> new NotFoundException("Offre non trouvée"));
        return offre.getCandidatures();
    }

    public void supprimerCandidatureOffre(Long idOffre, int idCandidat, Entreprise entreprise) {
        Offre offre = offreRepository.findById(idOffre)
                .orElseThrow(() -> new NotFoundException("Offre non trouvée"));
        if (!offre.getEntreprise().equals(entreprise))
            throw new ForbiddenException("Accès refusé");
        Candidat candidat = candidatRepository.findById(idCandidat)
                .orElseThrow(() -> new NotFoundException("Candidat non trouvé"));
        offre.getCandidatures().remove(candidat);
        offreRepository.save(offre);
    }

    // ========== WISHLIST ==========

    public void ajouterWishlist(Long idEntreprise, int idCandidat) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
                .orElseThrow(() -> new NotFoundException("Entreprise non trouvée"));
        Candidat candidat = candidatRepository.findById(idCandidat)
                .orElseThrow(() -> new NotFoundException("Candidat non trouvé"));
        if (entreprise.getWishlist().contains(candidat))
            throw new BusinessException("Ce candidat est déjà dans la wishlist");
        entreprise.getWishlist().add(candidat);
        entrepriseRepository.save(entreprise);
    }

    public void retirerWishlist(Long idEntreprise, int idCandidat) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
                .orElseThrow(() -> new NotFoundException("Entreprise non trouvée"));
        Candidat candidat = candidatRepository.findById(idCandidat)
                .orElseThrow(() -> new NotFoundException("Candidat non trouvé"));
        entreprise.getWishlist().remove(candidat);
        entrepriseRepository.save(entreprise);
    }

    public List<Candidat> getWishlist(Long idEntreprise) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
                .orElseThrow(() -> new NotFoundException("Entreprise non trouvée"));
        return entreprise.getWishlist();
    }
}
