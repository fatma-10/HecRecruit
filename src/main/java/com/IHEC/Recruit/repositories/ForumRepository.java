package com.IHEC.Recruit.repositories;

import com.IHEC.Recruit.models.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {

    List<Forum> findAllByOrderByDatePublicationDesc();

    List<Forum> findTop10ByOrderByDatePublicationDesc();

    List<Forum> findByAuteurContainingIgnoreCaseOrderByDatePublicationDesc(String auteur);

    List<Forum> findByMessageContainingIgnoreCaseOrderByDatePublicationDesc(String message);

    List<Forum> findByEstEtudiantOrderByDatePublicationDesc(boolean estEtudiant);

    long countByEstEtudiant(boolean estEtudiant);
}
