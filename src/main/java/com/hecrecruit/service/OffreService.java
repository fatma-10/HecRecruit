package com.hecrecruit.service;

import com.hecrecruit.model.Offre;
import com.hecrecruit.repository.OffreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OffreService {

    @Autowired
    private OffreRepository offreRepository;

    public List<Offre> getToutesOffres() {
        return offreRepository.findAll();
    }

    public Optional<Offre> getOffreById(UUID id) {
        return offreRepository.findById(id);
    }

    public List<Offre> getOffresByType(String typeOffre) {
        return offreRepository.findByTypeOffre(typeOffre);
    }

    public List<Offre> getOffresByEntreprise(UUID entrepriseId) {
        return offreRepository.findByEntrepriseId(entrepriseId);
    }

    public List<Offre> getActiveOffers() {
        return offreRepository.findActiveOffers();
    }

    public List<Offre> getExpiredOffers() {
        return offreRepository.findExpiredOffers();
    }

    public Offre saveOffre(Offre offre) {
        return offreRepository.save(offre);
    }

    public void deleteOffreById(UUID id) {
        offreRepository.deleteById(id);
    }

    public List<Offre> filterOffresByType(String type) {
        return getToutesOffres().stream()
                .filter(o -> !o.estExpiree() && o.getTypeOffre().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }
}
