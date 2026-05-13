package com.IHEC.Recruit.service;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OffreService {

    private final OffreRepository offreRepository;
    private final StageRepository stageRepository;
    private final AlternanceRepository alternanceRepository;
    private final ProjetFinEtudesRepository projetRepository;
    private final EntrepriseRepository entrepriseRepository;

    public OffreService(OffreRepository offreRepository,
                        StageRepository stageRepository,
                        AlternanceRepository alternanceRepository,
                        ProjetFinEtudesRepository projetRepository,
                        EntrepriseRepository entrepriseRepository) {
        this.offreRepository = offreRepository;
        this.stageRepository = stageRepository;
        this.alternanceRepository = alternanceRepository;
        this.projetRepository = projetRepository;
        this.entrepriseRepository = entrepriseRepository;
    }

    // ========== CRÉATION ==========

    /**
     * Crée et sauvegarde une offre spécialisée directement en DB.
     */
    public Offre creerOffre(String titre, String description, String type,
                             Entreprise entreprise, Map<String, String> infos) {

        OffreSpecialisee nouvelleOffre;

        switch (type.toLowerCase()) {
            case "stage":
                nouvelleOffre = new Stage(
                    titre, description, entreprise,
                    Integer.parseInt(infos.get("duree")),
                    infos.get("domaine")
                );
                break;

            case "alternance":
                nouvelleOffre = new Alternance(
                    titre, description, entreprise,
                    infos.get("rythme"),
                    Integer.parseInt(infos.get("duree"))
                );
                break;

            case "projet fin d'etudes":
                nouvelleOffre = new ProjetFinEtudes(
                    titre, description, entreprise,
                    infos.get("sujet"),
                    infos.get("technologies")
                );
                break;

            default:
                throw new IllegalArgumentException("Type d'offre non reconnu : " + type);
        }

        return offreRepository.save(nouvelleOffre);
    }

    // ========== SUPPRESSION ==========

    /**
     * Supprime une offre si elle appartient à l'entreprise.
     */
    public void supprimerOffre(Long idOffre, Entreprise entreprise) {
        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));

        if (!offre.getEntreprise().equals(entreprise)) {
            throw new SecurityException("Vous n'avez pas les droits pour supprimer cette offre");
        }

        offreRepository.delete(offre);
    }

    // ========== RECHERCHE ==========

    public List<Offre> getAllOffres() {
        return offreRepository.findAll();
    }

    public Offre getOffreById(Long id) {
        return offreRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));
    }

    public List<Offre> getOffresEntreprise(Entreprise entreprise) {
        return offreRepository.findByEntreprise(entreprise);
    }

    /**
     * Offres actives = date expiration nulle OU dans le futur.
     */
    public List<Offre> getOffresDisponibles() {
        return offreRepository.findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());
    }

    public List<Offre> rechercherParTitre(String titre) {
        return offreRepository.findByTitreContainingIgnoreCase(titre);
    }

    public List<Offre> rechercherParType(String type) {
        return offreRepository.findByTypeOffre(type);
    }

    public List<Stage> rechercherStagesParDomaine(String domaine) {
        return stageRepository.findByDomaineContainingIgnoreCase(domaine);
    }

    public List<Alternance> rechercherAlternancesParRythme(String rythme) {
        return alternanceRepository.findByRythmeContainingIgnoreCase(rythme);
    }

    public List<ProjetFinEtudes> rechercherPFEParTechnologie(String tech) {
        return projetRepository.findByTechnologiesContainingIgnoreCase(tech);
    }

    // ========== DATE EXPIRATION ==========

    /**
     * Modifie la date d'expiration d'une offre (doit être dans le futur).
     */
    public Offre setDateExpiration(Long idOffre, LocalDate date, Entreprise entreprise) {
        Offre offre = offreRepository.findById(idOffre)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));

        if (!offre.getEntreprise().equals(entreprise)) {
            throw new SecurityException("Accès refusé");
        }

        if (!date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date d'expiration doit être dans le futur");
        }

        offre.setDateExpiration(date);
        return offreRepository.save(offre);
    }
    public List<Offre> rechercherOffres(String critere, String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return getOffresDisponibles();
        }

        // On utilise un switch moderne qui retourne la liste
        return switch (critere.toLowerCase()) {
            case "titre" ->
                    offreRepository.findByTitreContainingIgnoreCase(valeur);

            case "type" ->
                    offreRepository.findByTypeOffre(valeur);

            case "domaine" ->
                // On convertit List<Stage> en List<Offre> proprement
                    new java.util.ArrayList<>(stageRepository.findByDomaineContainingIgnoreCase(valeur));

            case "rhythm" ->
                    new java.util.ArrayList<>(alternanceRepository.findByRythmeContainingIgnoreCase(valeur));

            case "technologies" ->
                    new java.util.ArrayList<>(projetRepository.findByTechnologiesContainingIgnoreCase(valeur));

            default ->
                    getOffresDisponibles();
        };
    }
    // ========== STATISTIQUES ==========

    public Map<String, Long> getStatistiques() {
        return Map.of(
            "total",       offreRepository.count(),
            "stages",      stageRepository.count(),
            "alternances", alternanceRepository.count(),
            "pfe",         projetRepository.count()
        );
    }
}
