package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.model.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    List<Stage> findByDomaineContainingIgnoreCase(String domaine);

    List<Stage> findByDureeEnMois(int dureeEnMois);

    // Recherche par domaine avec filtre actif
    List<Stage> findByDomaineContainingIgnoreCaseAndDateExpirationIsNullOrDomaineContainingIgnoreCaseAndDateExpirationAfter(
            String domaine1, String domaine2, LocalDate date);
}