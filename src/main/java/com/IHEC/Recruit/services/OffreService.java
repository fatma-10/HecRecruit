package com.IHEC.Recruit.services;

import com.IHEC.Recruit.models.*;
import com.IHEC.Recruit.exception.BusinessException;
import com.IHEC.Recruit.exception.ForbiddenException;
import com.IHEC.Recruit.exception.NotFoundException;
import com.IHEC.Recruit.repositories.*;
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

    public OffreService(OffreRepository offreRepository,
                        StageRepository stageRepository,
                        AlternanceRepository alternanceRepository,
                        ProjetFinEtudesRepository projetRepository) {
        this.offreRepository = offreRepository;
        this.stageRepository = stageRepository;
        this.alternanceRepository = alternanceRepository;
        this.projetRepository = projetRepository;
    }

    // ========== CREATION ==========

    public Offre creerOffre(String titre, String description, String type,
                            Entreprise entreprise, Map<String, String> infos) {
        if (titre == null || titre.isBlank())
            throw new BusinessException("Le titre est obligatoire");
        if (description == null || description.isBlank())
            throw new BusinessException("La description est obligatoire");
        if (entreprise == null)
            throw new BusinessException("L'entreprise est obligatoire");

        OffreSpecialisee nouvelleOffre;
        switch (type == null ? "" : type.toLowerCase()) {
            case "stage":
                nouvelleOffre = new Stage(
                        titre, description, entreprise,
                        parseIntStrict(infos.get("duree"), "La durée"),
                        infos.getOrDefault("domaine", ""));
                break;
            case "alternance":
                nouvelleOffre = new Alternance(
                        titre, description, entreprise,
                        infos.getOrDefault("rythme", ""),
                        parseIntStrict(infos.get("duree"), "La durée"));
                break;
            case "projet fin d'etudes":
            case "pfe":
                nouvelleOffre = new ProjetFinEtudes(
                        titre, description, entreprise,
                        infos.getOrDefault("sujet", ""),
                        infos.getOrDefault("technologies", ""));
                break;
            default:
                throw new BusinessException("Type d'offre non reconnu : " + type);
        }
        return offreRepository.save(nouvelleOffre);
    }

    private int parseIntStrict(String s, String label) {
        if (s == null || s.isBlank())
            throw new BusinessException(label + " est obligatoire");
        try {
            int v = Integer.parseInt(s);
            if (v <= 0) throw new BusinessException(label + " doit être supérieure à 0");
            return v;
        } catch (NumberFormatException e) {
            throw new BusinessException(label + " doit être un nombre");
        }
    }

    // ========== SUPPRESSION ==========

    public void supprimerOffre(Long idOffre, Entreprise entreprise) {
        Offre offre = offreRepository.findById(idOffre)
                .orElseThrow(() -> new NotFoundException("Offre non trouvée"));
        if (!offre.getEntreprise().equals(entreprise))
            throw new ForbiddenException("Vous n'avez pas les droits pour supprimer cette offre");
        offreRepository.delete(offre);
    }

    // ========== LECTURE ==========

    public List<Offre> getAllOffres() {
        return offreRepository.findAll();
    }

    public Offre getOffreById(Long id) {
        return offreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Offre non trouvée"));
    }

    public List<Offre> getOffresEntreprise(Entreprise entreprise) {
        return offreRepository.findByEntreprise(entreprise);
    }

    public List<Offre> getOffresDisponibles() {
        return offreRepository.findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());
    }

    public List<Offre> rechercherParTitre(String titre) {
        return offreRepository.findByTitreContainingIgnoreCase(titre);
    }

    public List<Offre> rechercherParType(String type) {
        return offreRepository.findByTypeOffre(type);
    }

    /**
     * Recherche combinee : titre + type. Si type vide, recherche sur titre seulement.
     */
    public List<Offre> rechercher(String titre, String type) {
        List<Offre> base = (titre == null || titre.isBlank())
                ? offreRepository.findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now())
                : offreRepository.findByTitreContainingIgnoreCase(titre);
        if (type == null || type.isBlank() || "tous".equalsIgnoreCase(type)) return base;
        // Alias "pfe" -> "projet fin d'etudes" (le formulaire utilise pfe pour eviter l'apostrophe)
        final String typeFinal = "pfe".equalsIgnoreCase(type) ? "projet fin d'etudes" : type;
        return base.stream().filter(o -> o.getTypeOffre().equalsIgnoreCase(typeFinal)).toList();
    }

    // ========== DATE EXPIRATION ==========

    public Offre setDateExpiration(Long idOffre, LocalDate date, Entreprise entreprise) {
        Offre offre = offreRepository.findById(idOffre)
                .orElseThrow(() -> new NotFoundException("Offre non trouvée"));
        if (!offre.getEntreprise().equals(entreprise))
            throw new ForbiddenException("Accès refusé");
        if (date != null && !date.isAfter(LocalDate.now()))
            throw new BusinessException("La date d'expiration doit être dans le futur");
        offre.setDateExpiration(date);
        return offreRepository.save(offre);
    }

    // ========== STATISTIQUES ==========

    public Map<String, Long> getStatistiques() {
        return Map.of(
                "total", offreRepository.count(),
                "stages", stageRepository.count(),
                "alternances", alternanceRepository.count(),
                "pfe", projetRepository.count()
        );
    }
}
