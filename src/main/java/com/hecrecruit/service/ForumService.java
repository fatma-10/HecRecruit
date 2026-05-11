package com.hecrecruit.service;

import com.hecrecruit.model.Forum;
import com.hecrecruit.repository.ForumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForumService {

    @Autowired
    private ForumRepository forumRepository;

    public List<Forum> getTousMessages() {
        return forumRepository.findAll();
    }

    public Optional<Forum> getForumById(UUID id) {
        return forumRepository.findById(id);
    }

    public List<Forum> getMessagesByCandidatId(Integer candidatId) {
        return forumRepository.findByCandidatId(candidatId);
    }

    public Forum saveMessage(Forum forum) {
        return forumRepository.save(forum);
    }

    public void deleteMessageById(UUID id) {
        forumRepository.deleteById(id);
    }
}
