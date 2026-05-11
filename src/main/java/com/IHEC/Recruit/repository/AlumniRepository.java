package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.model.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumniRepository extends JpaRepository<Alumni, Integer> {

    // Recherche par entreprise actuelle
    List<Alumni> findByEntrepriseActuelleContainingIgnoreCase(String entrepriseActuelle);

    // Recherche par année de diplôme
    List<Alumni> findByAnneeDiplome(int anneeDiplome);
}
