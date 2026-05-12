package com.IHEC.Recruit.repositories;

import com.IHEC.Recruit.models.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, String> {

    Optional<Candidat> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Candidat> findByNomContainingIgnoreCase(String nom);

    List<Candidat> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nom, String prenom, String email);
}
