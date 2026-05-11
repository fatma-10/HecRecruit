package com.hecrecruit.service;

import com.hecrecruit.model.Candidat;
import com.hecrecruit.repository.CandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatService {

    @Autowired
    private CandidatRepository candidatRepository;

    public List<Candidat> getTousCandidats() {
        return candidatRepository.findAll();
    }

    public Optional<Candidat> getCandidatById(Integer id) {
        return candidatRepository.findById(id);
    }

    public Optional<Candidat> getCandidatByEmail(String email) {
        return candidatRepository.findByEmail(email);
    }

    public Candidat saveCandidат(Candidat candidat) {
        return candidatRepository.save(candidat);
    }

    public void deleteCandidatById(Integer id) {
        candidatRepository.deleteById(id);
    }

    public boolean emailExists(String email) {
        return candidatRepository.existsByEmail(email);
    }

    public boolean cinExists(Integer cin) {
        return candidatRepository.existsById(cin);
    }
}
