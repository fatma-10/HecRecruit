package com.IHEC.Recruit.service;

import com.IHEC.Recruit.model.Forum;
import com.IHEC.Recruit.repository.ForumRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ForumService {

    private final ForumRepository forumRepository;

    public ForumService(ForumRepository forumRepository) {
        this.forumRepository = forumRepository;
    }

    /**
     * Crée et sauvegarde un message directement en DB.
     */
    public Forum ajouterCommentaire(String auteur, String email,
                                    String message, boolean estEtudiant) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Le message ne peut pas être vide");
        }

        Forum nouveau = new Forum(auteur, email, message, estEtudiant);
        return forumRepository.save(nouveau);
    }

    /**
     * Tous les messages triés par date décroissante — requête DB directe.
     */
    public List<Forum> getAllCommentaires() {
        return forumRepository.findAllByOrderByDatePublicationDesc();
    }

    /**
     * Les 10 derniers messages — requête DB directe.
     */
    public List<Forum> getDerniersCommentaires() {
        return forumRepository.findTop10ByOrderByDatePublicationDesc();
    }

    /**
     * Recherche dans les messages — toutes les requêtes vont en DB.
     */
    public List<Forum> rechercherCommentaires(String critere, String valeur) {
        switch (critere.toLowerCase()) {

            case "auteur":
                return forumRepository
                        .findByAuteurContainingIgnoreCaseOrderByDatePublicationDesc(valeur);

            case "message":
                return forumRepository
                        .findByMessageContainingIgnoreCaseOrderByDatePublicationDesc(valeur);

            case "etudiant":
                return forumRepository.findByEstEtudiantOrderByDatePublicationDesc(true);

            case "entreprise":
                return forumRepository.findByEstEtudiantOrderByDatePublicationDesc(false);

            default:
                return List.of();
        }
    }

    /**
     * Statistiques calculées directement en DB — pas de boucle en mémoire.
     */
    public Map<String, Long> getStatistiquesForum() {
        long total = forumRepository.count();
        long etudiants = forumRepository.countByEstEtudiant(true);
        long entreprises = forumRepository.countByEstEtudiant(false);

        return Map.of(
                "total", total,
                "etudiants", etudiants,
                "entreprises", entreprises
        );
    }
}
