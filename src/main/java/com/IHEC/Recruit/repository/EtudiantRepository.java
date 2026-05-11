package com.IHEC.Recruit.repository;
import com.IHEC.Recruit.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Integer> {

    // Recherche par filière
    List<Etudiant> findByFiliereContainingIgnoreCase(String filiere);

    // Recherche par établissement
    List<Etudiant> findByEtablissementContainingIgnoreCase(String etablissement);

    // Recherche par filière OU établissement
    List<Etudiant> findByFiliereContainingIgnoreCaseOrEtablissementContainingIgnoreCase(
            String filiere, String etablissement
    );
}
