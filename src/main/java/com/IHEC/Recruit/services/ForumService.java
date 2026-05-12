package com.IHEC.Recruit.services;

import com.IHEC.Recruit.models.Forum;
import com.IHEC.Recruit.exception.BusinessException;
import com.IHEC.Recruit.repositories.ForumRepository;
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

    public Forum ajouterCommentaire(String auteur, String email,
                                    String message, boolean estEtudiant) {
        if (message == null || message.trim().isEmpty())
            throw new BusinessException("Le message ne peut pas être vide");
        return forumRepository.save(new Forum(auteur, email, message, estEtudiant));
    }

    public List<Forum> getAllCommentaires() {
        return forumRepository.findAllByOrderByDatePublicationDesc();
    }

    public List<Forum> getDerniersCommentaires() {
        return forumRepository.findTop10ByOrderByDatePublicationDesc();
    }

    public List<Forum> rechercherCommentaires(String critere, String valeur) {
        if (valeur == null) valeur = "";
        switch (critere == null ? "" : critere.toLowerCase()) {
            case "auteur":
                return forumRepository.findByAuteurContainingIgnoreCaseOrderByDatePublicationDesc(valeur);
            case "message":
                return forumRepository.findByMessageContainingIgnoreCaseOrderByDatePublicationDesc(valeur);
            case "etudiant":
                return forumRepository.findByEstEtudiantOrderByDatePublicationDesc(true);
            case "entreprise":
                return forumRepository.findByEstEtudiantOrderByDatePublicationDesc(false);
            default:
                return getAllCommentaires();
        }
    }

    public Map<String, Long> getStatistiquesForum() {
        return Map.of(
                "total", forumRepository.count(),
                "etudiants", forumRepository.countByEstEtudiant(true),
                "entreprises", forumRepository.countByEstEtudiant(false));
    }
}
