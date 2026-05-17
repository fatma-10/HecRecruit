package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.model.Offre;
import com.IHEC.Recruit.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {

    // Toutes les offres d'une entreprise
    List<Offre> findByEntreprise(Entreprise entreprise);

    long countByEntreprise(Entreprise entreprise);

    // Toutes les offres d'une entreprise par ID
    List<Offre> findByEntrepriseId(Long entrepriseId);

    // Offres par type (Stage, Alternance, etc.)
    List<Offre> findByTypeOffre(String typeOffre);

    // Recherche par titre (contient, insensible à la casse)
    List<Offre> findByTitreContainingIgnoreCase(String titre);

    // Offres dont la date d'expiration est nulle OU après aujourd'hui
    List<Offre> findByDateExpirationIsNullOrDateExpirationAfter(LocalDate date);

    long countByDateExpirationIsNullOrDateExpirationAfter(LocalDate date);

    List<Offre> findByDateExpirationIsNullOrDateExpirationAfterOrderByDatePublicationDesc(LocalDate date);
}
