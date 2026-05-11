package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.model.Offre;
import com.IHEC.Recruit.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {

    // Toutes les offres d'une entreprise
    List<Offre> findByEntreprise(Entreprise entreprise);

    // Toutes les offres d'une entreprise par ID
    List<Offre> findByEntrepriseId(Long entrepriseId);

    // Offres par type (Stage, Alternance, etc.)
    List<Offre> findByTypeOffre(String typeOffre);

    // Recherche par titre (contient, insensible à la casse)
    List<Offre> findByTitreContainingIgnoreCase(String titre);

    // Offres dont la date d'expiration est nulle OU après aujourd'hui
    List<Offre> findByDateExpirationIsNullOrDateExpirationAfter(java.time.LocalDate date);
}
