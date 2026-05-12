package com.IHEC.Recruit.repositories;

import com.IHEC.Recruit.models.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {

    Optional<Entreprise> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Entreprise> findByNomContainingIgnoreCase(String nom);

    List<Entreprise> findBySecteurContainingIgnoreCase(String secteur);

    List<Entreprise> findByNomContainingIgnoreCaseOrSecteurContainingIgnoreCaseOrAdresseContainingIgnoreCase(
            String nom, String secteur, String adresse);
}
