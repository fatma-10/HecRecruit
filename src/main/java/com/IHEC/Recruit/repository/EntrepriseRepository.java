package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {

    // Trouver par email (pour login)
    Optional<Entreprise> findByEmailIgnoreCase(String email);

    // Vérifier existence email (pour inscription)
    boolean existsByEmailIgnoreCase(String email);

    // Recherche par nom
    List<Entreprise> findByNomContainingIgnoreCase(String nom);

    // Recherche par secteur
    List<Entreprise> findBySecteurContainingIgnoreCase(String secteur);

    // Recherche globale nom ou secteur ou adresse
    List<Entreprise> findByNomContainingIgnoreCaseOrSecteurContainingIgnoreCaseOrAdresseContainingIgnoreCase(
            String nom, String secteur, String adresse
    );
}