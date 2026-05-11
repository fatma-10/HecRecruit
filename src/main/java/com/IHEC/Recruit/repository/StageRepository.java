package com.IHEC.Recruit.repository;


import com.IHEC.Recruit.model.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    // Stages par domaine
    List<Stage> findByDomaineContainingIgnoreCase(String domaine);

    // Stages par durée
    List<Stage> findByDureeEnMois(int dureeEnMois);
}
