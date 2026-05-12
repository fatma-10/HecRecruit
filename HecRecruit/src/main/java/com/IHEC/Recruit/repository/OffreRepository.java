package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.entity.Entreprise;
import com.IHEC.Recruit.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {

    List<Offre> findByEntreprise(Entreprise entreprise);

    List<Offre> findByEntrepriseId(Long entrepriseId);

    List<Offre> findByTypeOffre(String typeOffre);

    List<Offre> findByTitreContainingIgnoreCase(String titre);

    List<Offre> findByDateExpirationIsNullOrDateExpirationAfter(LocalDate date);
}
