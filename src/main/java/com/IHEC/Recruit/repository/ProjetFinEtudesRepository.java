package com.IHEC.Recruit.repository;


import com.IHEC.Recruit.model.ProjetFinEtudes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetFinEtudesRepository extends JpaRepository<ProjetFinEtudes, Long> {

    // PFE par sujet
    List<ProjetFinEtudes> findBySujetContainingIgnoreCase(String sujet);

    // PFE par technologies
    List<ProjetFinEtudes> findByTechnologiesContainingIgnoreCase(String technologies);}