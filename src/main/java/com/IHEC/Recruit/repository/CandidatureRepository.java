package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.model.Candidature;
import com.IHEC.Recruit.model.CandidatureId;
import com.IHEC.Recruit.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, CandidatureId> {

    List<Candidature> findByCandidat_Id(int candidatId);

    long countByCandidat_Id(int candidatId);

    List<Candidature> findByOffre_Id(Long offreId);

    List<Candidature> findByOffre_Entreprise(Entreprise entreprise);

    long countByOffre_Entreprise(Entreprise entreprise);
}
