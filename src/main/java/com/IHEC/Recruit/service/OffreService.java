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
     * @param type        le type : "stage", "alternance" ou "projet fin d'etudes"
     * @param entreprise  l'entreprise publiant l'offre
     * @param infos       les paramètres spécifiques au type (domaine, duree, rythme, sujet, technologies)
     * @return l'offre persistée
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
     * Supprime une offre si elle appartient à l'entreprise donnée.
     *
     * @param idOffre    l'identifiant de l'offre à supprimer
     * @param entreprise l'entreprise propriétaire (contrôle d'accès)
     * @throws IllegalArgumentException si l'offre est introuvable
     * @throws SecurityException        si l'entreprise n'est pas propriétaire de l'offre
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
     * @throws IllegalArgumentException si aucune offre n'est trouvée
     */
    public Offre getOffreById(Long id) {
        return offreRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée"));
    }

    /**
     * Retourne toutes les offres d'une entreprise donnée.
     *
     * @param entreprise l'entreprise concernée
     * @return la liste des offres publiées par cette entreprise
     */
    public List<Offre> getOffresEntreprise(Entreprise entreprise) {
        return offreRepository.findByEntreprise(entreprise);
    }

    /**
     * Retourne les offres actives : date d'expiration nulle ou dans le futur.
     *
     * @return la liste des offres disponibles
     */
    public List<Offre> getOffresDisponibles() {
        return offreRepository.findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());
    }

    /**
     * Retourne le nombre d'offres actives sans charger toute la liste en mémoire.
     *
     * <p>Extrait du controller {@code CandidatController.dashboard()} pour éviter
     * un double appel à {@link #getOffresDisponibles()} : l'un pour le comptage
     * et l'autre pour la sous-liste des dernières offres.</p>
     *
     * @return le nombre d'offres dont la date d'expiration est nulle ou future
     */
    public long getOffresDisponiblesCount() {
        return offreRepository.findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now()).size();
    }

    /**
     * Retourne les {@code n} dernières offres actives (les plus récemment publiées).
     *
     * <p>Extrait du controller {@code CandidatController.dashboard()} pour éviter
     * de manipuler {@code subList} dans la couche présentation. La sélection des
     * {@code n} derniers éléments est calculée ici, au plus près des données.</p>
     *
     * @param n le nombre maximum d'offres à retourner
     * @return une sous-liste des {@code n} dernières offres disponibles
     */
    public List<Offre> getDernieresOffres(int n) {
        List<Offre> offres = getOffresDisponibles();
        int debut = Math.max(0, offres.size() - n);
        return offres.subList(debut, offres.size());
    }

    /**
     * Recherche des offres par titre (insensible à la casse).
     *
     * @param titre la chaîne à rechercher dans le titre
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
        return offreRepository.findByTypeOffre(type);
    }

    /**
     * Recherche des stages par domaine (insensible à la casse).
     *
     * @param domaine le domaine à rechercher
     * @return la liste des stages correspondants
     */
    public List<Stage> rechercherStagesParDomaine(String domaine) {
        return stageRepository.findByDomaineContainingIgnoreCase(domaine);
    }

    /**
     * Recherche des alternances par rythme (insensible à la casse).
     *
     * @param rythme le rythme à rechercher
     * @return la liste des alternances correspondantes
     */
    public List<Alternance> rechercherAlternancesParRythme(String rythme) {
        return alternanceRepository.findByRythmeContainingIgnoreCase(rythme);
    }

    /**
     * Recherche des projets de fin d'études par technologie (insensible à la casse).
     *
     * @param tech la technologie à rechercher
     * @return la liste des PFE correspondants
     */
    public List<ProjetFinEtudes> rechercherPFEParTechnologie(String tech) {
        return projetRepository.findByTechnologiesContainingIgnoreCase(tech);
    }

    // ========== DATE EXPIRATION ==========

    /**
     * Modifie la date d'expiration d'une offre (doit être dans le futur).
     *
     * @param idOffre    l'identifiant de l'offre
     * @param date       la nouvelle date d'expiration (doit être postérieure à aujourd'hui)
     * @param entreprise l'entreprise propriétaire (contrôle d'accès)
     * @return l'offre mise à jour
     * @throws IllegalArgumentException si la date n'est pas dans le futur
     * @throws SecurityException        si l'entreprise n'est pas propriétaire
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

    /**
     * Recherche unifiée par critère avec fallback sur les offres disponibles.
     *
     * @param critere le critère parmi : titre, type, domaine, rythme, technologies
     * @param valeur  la valeur à rechercher ; si vide, retourne toutes les offres disponibles
     * @return la liste des offres correspondant aux critères
     */
    public List<Offre> rechercherOffres(String critere, String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return getOffresDisponibles();
        }

        return switch (critere.toLowerCase()) {
            case "titre" ->
                    offreRepository.findByTitreContainingIgnoreCase(valeur);

            case "type" ->
                    offreRepository.findByTypeOffre(valeur);

            case "domaine" ->
                    new java.util.ArrayList<>(stageRepository.findByDomaineContainingIgnoreCase(valeur));

            case "rythme" ->
                    new java.util.ArrayList<>(alternanceRepository.findByRythmeContainingIgnoreCase(valeur));

            case "technologies" ->
                    new java.util.ArrayList<>(projetRepository.findByTechnologiesContainingIgnoreCase(valeur));

            default ->
                    getOffresDisponibles();
        };
    }

    // ========== STATISTIQUES ==========

    /**
     * Retourne les statistiques globales des offres (totaux par type).
     *
     * @return une map avec les clés : total, stages, alternances, pfe
     */
    public Map<String, Long> getStatistiques() {
        return Map.of(
            "total",       offreRepository.count(),
            "stages",      stageRepository.count(),
            "alternances", alternanceRepository.count(),
            "pfe",         projetRepository.count()
        );
    }
}