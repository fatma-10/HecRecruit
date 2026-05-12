package com.IHEC.Recruit.repository;

import com.IHEC.Recruit.entity.Alternance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlternanceRepository extends JpaRepository<Alternance, Long> {

    List<Alternance> findByRythmeContainingIgnoreCase(String rythme);

    List<Alternance> findByDureeEnMois(int dureeEnMois);
}
