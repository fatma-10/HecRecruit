package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.model.Alternance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlternanceRepository extends JpaRepository<Alternance, Long> {

    // Alternances par rythme
    List<Alternance> findByRythmeContainingIgnoreCase(String rythme);

    // Alternances par durée
    List<Alternance> findByDureeEnMois(int dureeEnMois);
}
