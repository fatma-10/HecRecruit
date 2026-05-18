package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.model.Alternance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlternanceRepository extends JpaRepository<Alternance, Long> {

    List<Alternance> findByRythmeContainingIgnoreCase(String rythme);

    List<Alternance> findByDureeEnMois(int dureeEnMois);

    // Recherche par rythme avec filtre actif
    List<Alternance> findByRythmeContainingIgnoreCaseAndDateExpirationIsNullOrRythmeContainingIgnoreCaseAndDateExpirationAfter(
            String rythme1, String rythme2, LocalDate date);
}