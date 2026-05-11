package com.hecrecruit.repository;

import com.hecrecruit.model.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OffreRepository extends JpaRepository<Offre, UUID> {
    List<Offre> findByTypeOffre(String typeOffre);
    List<Offre> findByEntrepriseId(UUID entrepriseId);
    
    @Query("SELECT o FROM Offre o WHERE o.dateExpiration IS NULL OR o.dateExpiration >= CURRENT_DATE")
    List<Offre> findActiveOffers();
    
    @Query("SELECT o FROM Offre o WHERE o.dateExpiration IS NOT NULL AND o.dateExpiration < CURRENT_DATE")
    List<Offre> findExpiredOffers();
}
