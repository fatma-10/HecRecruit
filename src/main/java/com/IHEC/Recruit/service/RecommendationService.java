package com.IHEC.Recruit.service;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.repository.OffreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RecommendationService {

    // Poids des critères (total = 100%)
    private static final double POIDS_FILIERE    = 0.40;
    private static final double POIDS_NIVEAU     = 0.20;
    private static final double POIDS_NOUVEAUTE  = 0.15;
    private static final double POIDS_POPULARITE = 0.15;
    private static final double POIDS_SECTEUR    = 0.10;

    private final OffreRepository offreRepository;

    public RecommendationService(OffreRepository offreRepository) {
        this.offreRepository = offreRepository;
    }

    /**
     * Recommandations pour un étudiant.
     * Charge les offres actives depuis la DB, calcule les scores en mémoire.
     */
    public List<OffreRecommandee> getRecommandationsEtudiant(Etudiant etudiant, int nbMax) {
        // Charger uniquement les offres actives depuis la DB
        List<Offre> offresActives = offreRepository
                .findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());

        return offresActives.stream()
                .filter(o -> !etudiant.getCandidaturesEnCours().contains(o))
                .map(o -> new OffreRecommandee(o, calculerScore(etudiant, o)))
                .sorted(Comparator.comparingDouble(OffreRecommandee::getScore).reversed())
                .limit(nbMax)
                .collect(Collectors.toList());
    }

    /**
     * Recommandations pour un alumni.
     */
    public List<OffreRecommandee> getRecommandationsAlumni(Alumni alumni, int nbMax) {
        List<Offre> offresActives = offreRepository
                .findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());

        return offresActives.stream()
                .filter(o -> !alumni.getCandidaturesEnCours().contains(o))
                .map(o -> {
                    double score = 50.0;
                    String type = o.getTypeOffre().toLowerCase();
                    if (type.contains("alternance")) score += 30.0;
                    if (type.contains("projet"))     score += 20.0;

                    String poste = alumni.getPosteActuel().toLowerCase();
                    String secteur = o.getEntreprise().getSecteur().toLowerCase();
                    if (!poste.isEmpty() && secteur.contains(poste)) score += 20.0;

                    return new OffreRecommandee(o, Math.min(100, score));
                })
                .sorted(Comparator.comparingDouble(OffreRecommandee::getScore).reversed())
                .limit(nbMax)
                .collect(Collectors.toList());
    }

    // ========== CALCUL DU SCORE ==========

    private double calculerScore(Etudiant etudiant, Offre offre) {
        double score = 0.0;
        score += calculerScoreFiliere(etudiant, offre)    * POIDS_FILIERE;
        score += calculerScoreNiveau(etudiant, offre)     * POIDS_NIVEAU;
        score += calculerScoreNouveaute(offre)            * POIDS_NOUVEAUTE;
        score += calculerScorePopularite(offre)           * POIDS_POPULARITE;
        score += calculerScoreSecteur(etudiant, offre)    * POIDS_SECTEUR;
        return score * 100;
    }

    private double calculerScoreFiliere(Etudiant etudiant, Offre offre) {
        String filiere = etudiant.getFiliere().toLowerCase();
        Map<String, List<String>> mots = new HashMap<>();

        mots.put("informatique", Arrays.asList("informatique", "dev", "développement",
                "java", "python", "web", "mobile", "data", "ia", "réseau", "cloud", "logiciel"));
        mots.put("gestion",      Arrays.asList("gestion", "management", "administration", "projet"));
        mots.put("marketing",    Arrays.asList("marketing", "commercial", "digital", "communication"));
        mots.put("finance",      Arrays.asList("finance", "banque", "trading", "investissement", "bourse"));
        mots.put("comptabilité", Arrays.asList("comptabilité", "audit", "contrôle", "fiscal", "bilan"));
        mots.put("ressources humaines", Arrays.asList("rh", "recrutement", "formation", "paie", "talent"));

        List<String> motsClés = mots.entrySet().stream()
                .filter(e -> filiere.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(List.of(filiere));

        String contenu = (offre.getTitre() + " " + offre.getDescription()).toLowerCase();
        if (offre instanceof Stage)
            contenu += " " + ((Stage) offre).getDomaine().toLowerCase();
        if (offre instanceof ProjetFinEtudes) {
            contenu += " " + ((ProjetFinEtudes) offre).getSujet().toLowerCase();
            contenu += " " + ((ProjetFinEtudes) offre).getTechnologies().toLowerCase();
        }

        final String contenuFinal = contenu;
        long correspondances = motsClés.stream().filter(contenuFinal::contains).count();
        return Math.min(1.0, correspondances / 3.0);
    }

    private double calculerScoreNiveau(Etudiant etudiant, Offre offre) {
        String niveau = etudiant.getNiveau().toLowerCase();
        String type   = offre.getTypeOffre().toLowerCase();

        if (niveau.contains("licence") && type.contains("stage"))        return 1.0;
        if (niveau.contains("master")  && type.contains("projet fin"))   return 1.0;
        if (niveau.contains("master")  && type.contains("alternance"))   return 0.9;
        if (niveau.contains("licence") && type.contains("alternance"))   return 0.7;
        return 0.5;
    }

    private double calculerScoreNouveaute(Offre offre) {
        long jours = ChronoUnit.DAYS.between(offre.getDatePublication(), LocalDate.now());
        if (jours <= 7)  return 1.0;
        if (jours <= 30) return 0.7;
        if (jours <= 90) return 0.4;
        return 0.2;
    }

    private double calculerScorePopularite(Offre offre) {
        int nb = offre.getCandidatures().size();
        if (nb == 0)       return 0.3;
        if (nb <= 5)       return 0.8;
        if (nb <= 15)      return 0.6;
        return 0.3;
    }

    private double calculerScoreSecteur(Etudiant etudiant, Offre offre) {
        String filiere = etudiant.getFiliere().toLowerCase();
        String secteur = offre.getEntreprise().getSecteur().toLowerCase();

        if (filiere.contains("informatique") &&
                (secteur.contains("tech") || secteur.contains("it") || secteur.contains("logiciel")))
            return 1.0;
        if (filiere.contains("finance") &&
                (secteur.contains("banque") || secteur.contains("finance") || secteur.contains("assurance")))
            return 1.0;
        if (filiere.contains("marketing") &&
                (secteur.contains("marketing") || secteur.contains("communication")))
            return 1.0;
        if (secteur.contains("conseil") || secteur.isEmpty())
            return 0.6;
        return 0.4;
    }

    // ========== CLASSE INTERNE ==========

    public static class OffreRecommandee {
        private final Offre offre;
        private final double score;

        public OffreRecommandee(Offre offre, double score) {
            this.offre = offre;
            this.score = score;
        }

        public Offre getOffre()  { return offre; }
        public double getScore() { return score; }

        public String getScoreFormate() {
            return String.format("%.0f%%", score);
        }
    }
}
