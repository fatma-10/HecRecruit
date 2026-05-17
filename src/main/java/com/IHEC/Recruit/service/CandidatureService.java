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
     * Enregistre la candidature d'un candidat pour une offre donnée.
     *
     * <p>La relation ManyToMany est gérée via l'entité {@link Offre}
     * (table {@code candidature} en base). Les deux côtés de la relation
     * bidirectionnelle sont mis à jour avant la persistance.</p>
     *
     * @param idCandidat le CIN du candidat
     * @param idOffre    l'identifiant de l'offre
     * @throws IllegalArgumentException si le candidat ou l'offre n'existe pas
     * @throws IllegalStateException    si l'offre est expirée ou si le candidat
     *                                  a déjà postulé
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

        offre.getCandidatures().add(candidat);
        candidat.getCandidaturesEnCours().add(offre);
        offreRepository.save(offre);
    }

    /**
     * Retire la candidature d'un candidat pour une offre donnée.
     *
     * @param idCandidat le CIN du candidat
     * @param idOffre    l'identifiant de l'offre
     * @throws IllegalArgumentException si le candidat ou l'offre n'existe pas
     * @throws IllegalStateException    si le candidat n'avait pas postulé
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
     * Retourne toutes les offres auxquelles un candidat a postulé.
     *
     * @param idCandidat le CIN du candidat
     * @return la liste des offres en cours pour ce candidat
     * @throws IllegalArgumentException si le candidat n'existe pas
     */
    public List<Offre> getCandidaturesCandidat(int idCandidat) {
        Candidat candidat = candidatRepository.findById(idCandidat)
            .orElseThrow(() -> new IllegalArgumentException("Candidat non trouvé"));
        return candidat.getCandidaturesEnCours();
    }

    public int getNombreCandidaturesCandidat(int idCandidat) {
        return getCandidaturesCandidat(idCandidat).size();
    }

    /**
     * Retourne tous les candidats ayant postulé à une offre (vue entreprise).
     *
     * @param idOffre l'identifiant de l'offre
     * @return la liste des candidats pour cette offre
     * @throws IllegalArgumentException si l'offre n'existe pas
     */
    public List<Candidat> getCandidatsOffre(Long idOffre) {
        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));
        return offre.getCandidatures();
    }

    public List<Candidat> getCandidatsOffrePourEntreprise(Long idOffre, Entreprise entreprise) {
        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));

        if (!offre.getEntreprise().equals(entreprise)) {
            throw new SecurityException("Accès refusé");
        }

        return offre.getCandidatures();
    }

    /**
     * Retourne le nombre total de candidatures reçues par une entreprise,
     * toutes offres confondues.
     *
     * <p>Cette méthode centralise le calcul qui était auparavant effectué
     * via un {@code stream} directement dans {@code EntrepriseController},
     * conformément au principe de séparation des responsabilités.</p>
     *
     * @param idEntreprise l'identifiant de l'entreprise
     * @return le total des candidatures reçues sur toutes les offres de l'entreprise
     * @throws IllegalArgumentException si l'entreprise n'existe pas
     */
    public int getTotalCandidaturesParEntreprise(Long idEntreprise) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
            .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvée"));
        return entreprise.getOffresPubliees().stream()
                .mapToInt(o -> o.getCandidatures().size())
                .sum();
    }

    /**
     * L'entreprise retire la candidature d'un candidat de l'une de ses offres.
     *
     * @param idOffre    l'identifiant de l'offre
     * @param idCandidat le CIN du candidat à retirer
     * @param entreprise l'entreprise qui effectue l'action
     * @throws SecurityException        si l'offre n'appartient pas à cette entreprise
     * @throws IllegalArgumentException si l'offre ou le candidat n'existe pas
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
     * Ajoute un candidat à la wishlist d'une entreprise.
     *
     * @param idEntreprise l'identifiant de l'entreprise
     * @param idCandidat   le CIN du candidat à ajouter
     * @throws IllegalStateException    si le candidat est déjà dans la wishlist
     * @throws IllegalArgumentException si l'entreprise ou le candidat n'existe pas
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
     * Retire un candidat de la wishlist d'une entreprise.
     *
     * @param idEntreprise l'identifiant de l'entreprise
     * @param idCandidat   le CIN du candidat à retirer
     * @throws IllegalArgumentException si l'entreprise ou le candidat n'existe pas
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
     *
     * @param idEntreprise l'identifiant de l'entreprise
     * @return la liste des candidats en wishlist
     * @throws IllegalArgumentException si l'entreprise n'existe pas
     */
    public List<Candidat> getWishlist(Long idEntreprise) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
            .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvée"));
        return entreprise.getWishlist();
    }

    public int getNombreWishlist(Long idEntreprise) {
        return getWishlist(idEntreprise).size();
    }
}
