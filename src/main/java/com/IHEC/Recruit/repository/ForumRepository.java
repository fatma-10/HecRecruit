package com.IHEC.Recruit.repository;
import com.IHEC.Recruit.model.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {

    // Tous les messages triés par date décroissante (plus récents en premier)
    List<Forum> findAllByOrderByDatePublicationDesc();

    // Les N derniers messages
    List<Forum> findTop10ByOrderByDatePublicationDesc();

    // Recherche par auteur
    List<Forum> findByAuteurContainingIgnoreCaseOrderByDatePublicationDesc(String auteur);

    // Recherche dans le contenu du message
    List<Forum> findByMessageContainingIgnoreCaseOrderByDatePublicationDesc(String message);

    // Filtrer par type d'auteur (étudiant ou entreprise)
    List<Forum> findByEstEtudiantOrderByDatePublicationDesc(boolean estEtudiant);

    // Compter par type d'auteur (pour statistiques)
    long countByEstEtudiant(boolean estEtudiant);
}
