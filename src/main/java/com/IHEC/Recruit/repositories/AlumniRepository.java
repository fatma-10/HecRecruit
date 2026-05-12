package com.IHEC.Recruit.repositories;

import com.IHEC.Recruit.models.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumniRepository extends JpaRepository<Alumni, String> {

    List<Alumni> findByEntrepriseActuelleContainingIgnoreCase(String entrepriseActuelle);

    List<Alumni> findByAnneeDiplome(int anneeDiplome);
}
