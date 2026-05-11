package com.hecrecruit.service;

import com.hecrecruit.model.Entreprise;
import com.hecrecruit.repository.EntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EntrepriseService {

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    public List<Entreprise> getToutesEntreprises() {
        return entrepriseRepository.findAll();
    }

    public Optional<Entreprise> getEntrepriseById(UUID id) {
        return entrepriseRepository.findById(id);
    }

    public Optional<Entreprise> getEntrepriseByEmail(String email) {
        return entrepriseRepository.findByEmail(email);
    }

    public Entreprise saveEntreprise(Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    public void deleteEntrepriseById(UUID id) {
        entrepriseRepository.deleteById(id);
    }

    public boolean emailExists(String email) {
        return entrepriseRepository.existsByEmail(email);
    }

    public boolean verifyPassword(UUID id, String mdp) {
        Optional<Entreprise> entreprise = entrepriseRepository.findById(id);
        return entreprise.isPresent() && entreprise.get().verifierMotDePasse(mdp);
    }
}
