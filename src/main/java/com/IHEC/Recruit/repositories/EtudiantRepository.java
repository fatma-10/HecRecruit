package com.IHEC.Recruit.repositories;

import com.IHEC.Recruit.models.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, String> {

    List<Etudiant> findByFiliereContainingIgnoreCase(String filiere);

    List<Etudiant> findByEtablissementContainingIgnoreCase(String etablissement);

    List<Etudiant> findByFiliereContainingIgnoreCaseOrEtablissementContainingIgnoreCase(
            String filiere, String etablissement);
}
