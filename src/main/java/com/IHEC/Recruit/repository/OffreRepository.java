package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.model.Offre;
import com.IHEC.Recruit.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {

    List<Offre> findByEntreprise(Entreprise entreprise);

    long countByEntreprise(Entreprise entreprise);

    List<Offre> findByEntrepriseId(Long entrepriseId);

    List<Offre> findByTypeOffre(String typeOffre);

    List<Offre> findByTitreContainingIgnoreCase(String titre);

    // Recherche par titre avec filtre actif
    List<Offre> findByTitreContainingIgnoreCaseAndDateExpirationIsNullOrTitreContainingIgnoreCaseAndDateExpirationAfter(
            String titre1, String titre2, LocalDate date);

    // Recherche par type avec filtre actif
    List<Offre> findByTypeOffreAndDateExpirationIsNullOrTypeOffreAndDateExpirationAfter(
            String type1, String type2, LocalDate date);

    List<Offre> findByDateExpirationIsNullOrDateExpirationAfter(LocalDate date);

    long countByDateExpirationIsNullOrDateExpirationAfter(LocalDate date);

    List<Offre> findByDateExpirationIsNullOrDateExpirationAfterOrderByDatePublicationDesc(LocalDate date);
}