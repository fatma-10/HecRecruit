package com.IHEC.Recruit.repositories;

import com.IHEC.Recruit.models.ProjetFinEtudes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetFinEtudesRepository extends JpaRepository<ProjetFinEtudes, Long> {

    List<ProjetFinEtudes> findBySujetContainingIgnoreCase(String sujet);

    List<ProjetFinEtudes> findByTechnologiesContainingIgnoreCase(String technologies);
}
