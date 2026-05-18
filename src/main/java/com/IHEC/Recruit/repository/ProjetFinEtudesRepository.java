package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.model.ProjetFinEtudes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjetFinEtudesRepository extends JpaRepository<ProjetFinEtudes, Long> {

    List<ProjetFinEtudes> findBySujetContainingIgnoreCase(String sujet);

    List<ProjetFinEtudes> findByTechnologiesContainingIgnoreCase(String technologies);

    // Recherche par technologies avec filtre actif
    List<ProjetFinEtudes> findByTechnologiesContainingIgnoreCaseAndDateExpirationIsNullOrTechnologiesContainingIgnoreCaseAndDateExpirationAfter(
            String tech1, String tech2, LocalDate date);
}