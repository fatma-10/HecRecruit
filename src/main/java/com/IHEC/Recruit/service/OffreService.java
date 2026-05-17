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
     *
     * @param titre       le titre de l'offre
     * @param description la description de l'offre
     * @param type        "stage", "alternance" ou "projet fin d'etudes"
     * @param entreprise  l'entreprise propriétaire
     * @param infos       map des champs spécifiques au type (domaine, duree, rythme, sujet, technologies)
     * @return l'offre persistée
     */
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

    /**
     * Supprime une offre si elle appartient à l'entreprise donnée.
     *
     * @param idOffre    l'identifiant de l'offre
     * @param entreprise l'entreprise qui demande la suppression
     * @throws SecurityException        si l'offre n'appartient pas à cette entreprise
     * @throws IllegalArgumentException si l'offre n'existe pas
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

    /**
     * Retourne toutes les offres sans filtre.
     *
     * @return la liste complète des offres
     */
    public List<Offre> getAllOffres() {
        return offreRepository.findAll();
    }

    /**
     * Retourne une offre par son identifiant.
     *
     * @param id l'identifiant de l'offre
     * @return l'offre correspondante
     * @throws IllegalArgumentException si l'offre n'existe pas
     */
    public Offre getOffreById(Long id) {
        return offreRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));
    }

    /**
     * Retourne toutes les offres publiées par une entreprise donnée.
     *
     * @param entreprise l'entreprise concernée
     * @return la liste des offres de cette entreprise
     */
    public List<Offre> getOffresEntreprise(Entreprise entreprise) {
        return offreRepository.findByEntreprise(entreprise);
    }

    public long getNombreOffresEntreprise(Entreprise entreprise) {
        return offreRepository.countByEntreprise(entreprise);
    }

    /**
     * Retourne toutes les offres actives (date d'expiration nulle ou dans le futur).
     *
     * @return la liste des offres disponibles
     */
    public List<Offre> getOffresDisponibles() {
        return offreRepository.findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());
    }

    /**
     * Retourne le nombre total d'offres actives sans charger les entités en mémoire.
     *
     * <p>Cette méthode est destinée aux controllers qui n'ont besoin que du
     * comptage pour l'affichage du tableau de bord, sans manipuler la liste.</p>
     *
     * @return le nombre d'offres dont la date d'expiration est nulle ou future
     */
    public long getOffresDisponiblesCount() {
        return offreRepository.countByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());
    }

    /**
     * Retourne les {@code n} offres actives les plus récentes, triées par date
     * de publication décroissante.
     *
     * <p>Cette méthode centralise la logique de sous-liste qui était auparavant
     * dispersée dans {@code CandidatController}, conformément au principe de
     * séparation des responsabilités.</p>
     *
     * @param n le nombre maximum d'offres à retourner (doit être &gt; 0)
     * @return la liste des {@code n} dernières offres disponibles
     */
    public List<Offre> getDernieresOffres(int n) {
        if (n <= 0) {
            return List.of();
        }
        List<Offre> offres = offreRepository
                .findByDateExpirationIsNullOrDateExpirationAfterOrderByDatePublicationDesc(LocalDate.now());
        return offres.subList(0, Math.min(n, offres.size()));
    }

    /**
     * Recherche des offres selon un critère et une valeur.
     *
     * @param critere titre, type, domaine, rythme ou technologies
     * @param valeur  la valeur à rechercher
     * @return la liste des offres correspondantes, ou toutes les offres disponibles
     *         si le critère est inconnu ou la valeur vide
     */
    public List<Offre> rechercherOffres(String critere, String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return getOffresDisponibles();
        }

        return switch (critere.toLowerCase()) {
            case "titre"        -> offreRepository.findByTitreContainingIgnoreCase(valeur);
            case "type"         -> offreRepository.findByTypeOffre(normaliserTypeOffre(valeur));
            case "domaine"      -> new java.util.ArrayList<>(
                                        stageRepository.findByDomaineContainingIgnoreCase(valeur));
            case "rythme"       -> new java.util.ArrayList<>(
                                        alternanceRepository.findByRythmeContainingIgnoreCase(valeur));
            case "technologies" -> new java.util.ArrayList<>(
                                        projetRepository.findByTechnologiesContainingIgnoreCase(valeur));
            default             -> getOffresDisponibles();
        };
    }

    /**
     * Recherche des offres par titre.
     *
     * @param titre le titre (ou fragment) à rechercher
     * @return la liste des offres correspondantes
     */
    public List<Offre> rechercherParTitre(String titre) {
        return offreRepository.findByTitreContainingIgnoreCase(titre);
    }

    /**
     * Recherche des offres par type exact.
     *
     * @param type le type d'offre (ex : "Stage", "Alternance")
     * @return la liste des offres de ce type
     */
    public List<Offre> rechercherParType(String type) {
        return offreRepository.findByTypeOffre(normaliserTypeOffre(type));
    }

    /**
     * Recherche des stages dont le domaine contient la valeur donnée.
     *
     * @param domaine le domaine à rechercher
     * @return la liste des stages correspondants
     */
    public List<Stage> rechercherStagesParDomaine(String domaine) {
        return stageRepository.findByDomaineContainingIgnoreCase(domaine);
    }

    /**
     * Recherche des alternances dont le rythme contient la valeur donnée.
     *
     * @param rythme le rythme à rechercher
     * @return la liste des alternances correspondantes
     */
    public List<Alternance> rechercherAlternancesParRythme(String rythme) {
        return alternanceRepository.findByRythmeContainingIgnoreCase(rythme);
    }

    /**
     * Recherche des PFE dont les technologies contiennent la valeur donnée.
     *
     * @param tech la technologie à rechercher
     * @return la liste des PFE correspondants
     */
    public List<ProjetFinEtudes> rechercherPFEParTechnologie(String tech) {
        return projetRepository.findByTechnologiesContainingIgnoreCase(tech);
    }

    // ========== DATE EXPIRATION ==========

    /**
     * Modifie la date d'expiration d'une offre appartenant à l'entreprise donnée.
     *
     * @param idOffre    l'identifiant de l'offre
     * @param date       la nouvelle date d'expiration (doit être strictement future)
     * @param entreprise l'entreprise propriétaire de l'offre
     * @return l'offre mise à jour
     * @throws SecurityException        si l'offre n'appartient pas à cette entreprise
     * @throws IllegalArgumentException si la date n'est pas dans le futur
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

    // ========== STATISTIQUES ==========

    /**
     * Retourne les statistiques globales des offres.
     *
     * @return une map contenant total, stages, alternances et pfe
     */
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
