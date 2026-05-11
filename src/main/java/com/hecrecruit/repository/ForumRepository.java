package com.hecrecruit.repository;

import com.hecrecruit.model.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ForumRepository extends JpaRepository<Forum, UUID> {
    List<Forum> findByCandidatId(Integer candidatId);
    List<Forum> orderByDateCreationDesc();
}
