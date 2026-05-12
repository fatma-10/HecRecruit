package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.entity.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumniRepository extends JpaRepository<Alumni, Integer> {

    List<Alumni> findByEntrepriseActuelleContainingIgnoreCase(String entrepriseActuelle);

    List<Alumni> findByAnneeDiplome(int anneeDiplome);
}
