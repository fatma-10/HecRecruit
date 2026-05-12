package com.IHEC.Recruit.repositories;

import com.IHEC.Recruit.models.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    List<Stage> findByDomaineContainingIgnoreCase(String domaine);

    List<Stage> findByDureeEnMois(int dureeEnMois);
}
