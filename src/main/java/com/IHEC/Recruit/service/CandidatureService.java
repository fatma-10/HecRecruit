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
     *
     * <p>La relation ManyToMany est gérée via l'entité {@link Offre}
     * (table {@code candidature} en base). La relation est synchronisée
     * des deux côtés avant la persistance.</p>
     *
     * @param idCandidat le CIN du candidat
     * @param idOffre    l'identifiant de l'offre visée
     * @throws IllegalArgumentException si le candidat ou l'offre est introuvable
     * @throws IllegalStateException    si l'offre est expirée ou si le candidat a déjà postulé
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

        // Synchronisation des deux côtés de la relation bidirectionnelle
        offre.getCandidatures().add(candidat);
        candidat.getCandidaturesEnCours().add(offre);

        // Un seul save suffit grâce au CascadeType et à la relation mappedBy
        offreRepository.save(offre);
    }

    /**
     * Candidat retire sa candidature d'une offre.
     *
     * @param idCandidat le CIN du candidat
     * @param idOffre    l'identifiant de l'offre concernée
     * @throws IllegalArgumentException si le candidat ou l'offre est introuvable
     * @throws IllegalStateException    si le candidat n'a pas postulé à cette offre
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
     * @throws IllegalArgumentException si le candidat est introuvable
     */
    public List<Offre> getCandidaturesCandidat(int idCandidat) {
        Candidat candidat = candidatRepository.findById(idCandidat)
            .orElseThrow(() -> new IllegalArgumentException("Candidat non trouvé"));
        return candidat.getCandidaturesEnCours();
    }

    /**
     * Retourne tous les candidats ayant postulé à une offre (vue entreprise).
     *
     * @param idOffre l'identifiant de l'offre
     * @return la liste des candidats ayant postulé
     * @throws IllegalArgumentException si l'offre est introuvable
     */
    public List<Candidat> getCandidatsOffre(Long idOffre) {
        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));
        return offre.getCandidatures();
    }

    /**
     * Entreprise supprime la candidature d'un candidat de son offre.
     *
     * @param idOffre    l'identifiant de l'offre
     * @param idCandidat le CIN du candidat à retirer
     * @param entreprise l'entreprise effectuant l'action (contrôle d'accès)
     * @throws IllegalArgumentException si l'offre ou le candidat est introuvable
     * @throws SecurityException        si l'entreprise n'est pas propriétaire de l'offre
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
     *
     * @param idEntreprise l'identifiant de l'entreprise
     * @param idCandidat   le CIN du candidat à ajouter
     * @throws IllegalArgumentException si l'entreprise ou le candidat est introuvable
     * @throws IllegalStateException    si le candidat est déjà dans la wishlist
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
     *
     * @param idEntreprise l'identifiant de l'entreprise
     * @param idCandidat   le CIN du candidat à retirer
     * @throws IllegalArgumentException si l'entreprise ou le candidat est introuvable
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
     * @throws IllegalArgumentException si l'entreprise est introuvable
     */
    public List<Candidat> getWishlist(Long idEntreprise) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
            .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvée"));
        return entreprise.getWishlist();
    }

    // ========== STATISTIQUES ==========

    /**
     * Calcule le nombre total de candidatures reçues par toutes les offres
     * d'une entreprise donnée.
     *
     * <p>Extrait du controller {@code EntrepriseController.dashboard()} pour
     * supprimer le {@code stream().mapToInt().sum()} qui s'effectuait dans la
     * couche présentation. La logique métier de comptage appartient au service.</p>
     *
     * @param entrepriseId l'identifiant de l'entreprise
     * @return le nombre total de candidatures sur l'ensemble des offres de l'entreprise
     * @throws IllegalArgumentException si l'entreprise est introuvable
     */
    public int getTotalCandidaturesParEntreprise(Long entrepriseId) {
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
            .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvée"));

        return entreprise.getOffresPubliees().stream()
                .mapToInt(o -> o.getCandidatures().size())
                .sum();
    }
}