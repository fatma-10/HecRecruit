package com.hecrecruit.repository;

import com.hecrecruit.model.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Integer> {
    Optional<Candidat> findByEmail(String email);
    boolean existsByEmail(String email);
}
