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

    public Offre creerOffre(String titre, String description, String type,
                             Entreprise entreprise, Map<String, String> infos) {

        Offre nouvelleOffre;
        String typeNormalise = normaliserTypeOffre(type);

        switch (typeNormalise) {
            case "stage":
                nouvelleOffre = new Stage(
                    titre, description, entreprise,
                    parseDuree(infos.get("duree")),
                    infos.get("domaine")
                );
                break;

            case "alternance":
                nouvelleOffre = new Alternance(
                    titre, description, entreprise,
                    infos.get("rythme"),
                    parseDuree(infos.get("duree"))
                );
                break;

            case "projet_fin_etudes":
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

    public long getNombreOffresEntreprise(Entreprise entreprise) {
        return offreRepository.countByEntreprise(entreprise);
    }

    public List<Offre> getOffresDisponibles() {
        return offreRepository.findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());
    }

    public long getOffresDisponiblesCount() {
        return offreRepository.countByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());
    }

    public List<Offre> getDernieresOffres(int n) {
        if (n <= 0) {
            return List.of();
        }
        List<Offre> offres = offreRepository
                .findByDateExpirationIsNullOrDateExpirationAfterOrderByDatePublicationDesc(LocalDate.now());
        return offres.subList(0, Math.min(n, offres.size()));
    }

    /**
     * Recherche des offres selon un critère et une valeur,
     * en excluant les offres expirées.
     */
    public List<Offre> rechercherOffres(String critere, String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return getOffresDisponibles();
        }

        LocalDate today = LocalDate.now();

        return switch (critere.toLowerCase()) {
            case "titre" -> offreRepository
                    .findByTitreContainingIgnoreCaseAndDateExpirationIsNullOrTitreContainingIgnoreCaseAndDateExpirationAfter(
                            valeur, valeur, today);
            case "type" -> offreRepository
                    .findByTypeOffreAndDateExpirationIsNullOrTypeOffreAndDateExpirationAfter(
                            normaliserTypeOffre(valeur), normaliserTypeOffre(valeur), today);
            case "domaine" -> new java.util.ArrayList<>(
                    stageRepository.findByDomaineContainingIgnoreCaseAndDateExpirationIsNullOrDomaineContainingIgnoreCaseAndDateExpirationAfter(
                            valeur, valeur, today));
            case "rythme" -> new java.util.ArrayList<>(
                    alternanceRepository.findByRythmeContainingIgnoreCaseAndDateExpirationIsNullOrRythmeContainingIgnoreCaseAndDateExpirationAfter(
                            valeur, valeur, today));
            case "technologies" -> new java.util.ArrayList<>(
                    projetRepository.findByTechnologiesContainingIgnoreCaseAndDateExpirationIsNullOrTechnologiesContainingIgnoreCaseAndDateExpirationAfter(
                            valeur, valeur, today));
            default -> getOffresDisponibles();
        };
    }

    public List<Offre> rechercherParTitre(String titre) {
        return offreRepository.findByTitreContainingIgnoreCase(titre);
    }

    public List<Offre> rechercherParType(String type) {
        return offreRepository.findByTypeOffre(normaliserTypeOffre(type));
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

    // ========== STATISTIQUES ==========

    public Map<String, Long> getStatistiques() {
        return Map.of(
            "total",       offreRepository.count(),
            "stages",      stageRepository.count(),
            "alternances", alternanceRepository.count(),
            "pfe",         projetRepository.count()
        );
    }

    private String normaliserTypeOffre(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Le type d'offre est obligatoire");
        }

        String normalized = type.trim().toLowerCase()
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("'", "")
                .replace("-", " ")
                .replace("_", " ");

        return switch (normalized) {
            case "stage" -> "stage";
            case "alternance" -> "alternance";
            case "projet fin detudes", "projet fin d etudes", "pfe" -> "projet_fin_etudes";
            default -> throw new IllegalArgumentException("Type d'offre non reconnu : " + type);
        };
    }

    private int parseDuree(String duree) {
        if (duree == null || duree.isBlank()) {
            throw new IllegalArgumentException("La durée est obligatoire");
        }

        try {
            int valeur = Integer.parseInt(duree.trim());
            if (valeur <= 0) {
                throw new IllegalArgumentException("La durée doit être supérieure à 0");
            }
            return valeur;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La durée doit être un nombre valide");
        }
    }
}