package com.IHEC.Recruit.repository;


import com.IHEC.Recruit.model.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Integer> {
    // Note: ID est int (CIN 8 chiffres), pas Long

    // Trouver par email (pour login)
    Optional<Candidat> findByEmailIgnoreCase(String email);

    // Vérifier existence email (pour inscription)
    boolean existsByEmailIgnoreCase(String email);

    // Recherche par nom
    List<Candidat> findByNomContainingIgnoreCase(String nom);

    // Recherche globale nom ou prénom ou email
    List<Candidat> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nom, String prenom, String email
    );
}
