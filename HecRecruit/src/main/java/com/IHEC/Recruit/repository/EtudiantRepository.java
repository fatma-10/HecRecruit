package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Integer> {

    List<Etudiant> findByFiliereContainingIgnoreCase(String filiere);

    List<Etudiant> findByEtablissementContainingIgnoreCase(String etablissement);

    List<Etudiant> findByFiliereContainingIgnoreCaseOrEtablissementContainingIgnoreCase(
            String filiere, String etablissement);
}
