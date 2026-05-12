package com.IHEC.Recruit.services;

import com.IHEC.Recruit.models.*;
import com.IHEC.Recruit.repositories.OffreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RecommendationService {

    // Poids des criteres (somme = 1.0)
    private static final double POIDS_FILIERE = 0.40;
    private static final double POIDS_NIVEAU = 0.20;
    private static final double POIDS_NOUVEAUTE = 0.15;
    private static final double POIDS_POPULARITE = 0.15;
    private static final double POIDS_SECTEUR = 0.10;

    private final OffreRepository offreRepository;

    public RecommendationService(OffreRepository offreRepository) {
        this.offreRepository = offreRepository;
    }

    public List<OffreRecommandee> getRecommandationsEtudiant(Etudiant etudiant, int nbMax) {
        List<Offre> offresActives = offreRepository
                .findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());

        return offresActives.stream()
                .filter(o -> !etudiant.getCandidaturesEnCours().contains(o))
                .map(o -> new OffreRecommandee(o, calculerScoreEtudiant(etudiant, o)))
                .sorted(Comparator.comparingDouble(OffreRecommandee::getScore).reversed())
                .limit(nbMax)
                .collect(Collectors.toList());
    }

    public List<OffreRecommandee> getRecommandationsAlumni(Alumni alumni, int nbMax) {
        List<Offre> offresActives = offreRepository
                .findByDateExpirationIsNullOrDateExpirationAfter(LocalDate.now());

        return offresActives.stream()
                .filter(o -> !alumni.getCandidaturesEnCours().contains(o))
                .map(o -> new OffreRecommandee(o, calculerScoreAlumni(alumni, o)))
                .sorted(Comparator.comparingDouble(OffreRecommandee::getScore).reversed())
                .limit(nbMax)
                .collect(Collectors.toList());
    }

    // ========== SCORE ETUDIANT ==========

    private double calculerScoreEtudiant(Etudiant etudiant, Offre offre) {
        double score = 0.0;
        score += scoreFiliere(etudiant, offre) * POIDS_FILIERE;
        score += scoreNiveau(etudiant, offre) * POIDS_NIVEAU;
        score += scoreNouveaute(offre) * POIDS_NOUVEAUTE;
        score += scorePopularite(offre) * POIDS_POPULARITE;
        score += scoreSecteur(etudiant.getFiliere(), offre) * POIDS_SECTEUR;
        return Math.min(100.0, score * 100);
    }

    // ========== SCORE ALUMNI (rescaled to be comparable to etudiant) ==========

    private double calculerScoreAlumni(Alumni alumni, Offre offre) {
        double score = 0.5;
        String type = nullSafe(offre.getTypeOffre()).toLowerCase();
        if (type.contains("alternance")) score += 0.30;
        else if (type.contains("projet")) score += 0.20;

        String poste = nullSafe(alumni.getPosteActuel()).toLowerCase();
        String secteur = offre.getEntreprise() != null
                ? nullSafe(offre.getEntreprise().getSecteur()).toLowerCase() : "";
        if (!poste.isEmpty() && !secteur.isEmpty() && secteur.contains(poste)) score += 0.20;

        return Math.min(100.0, score * 100);
    }

    // ========== COMPOSANTS DU SCORE ==========

    private double scoreFiliere(Etudiant etudiant, Offre offre) {
        String filiere = nullSafe(etudiant.getFiliere()).toLowerCase();

        Map<String, List<String>> motsParFiliere = new HashMap<>();
        motsParFiliere.put("informatique", Arrays.asList("informatique", "dev", "développement",
                "java", "python", "web", "mobile", "data", "ia", "réseau", "cloud", "logiciel"));
        motsParFiliere.put("gestion", Arrays.asList("gestion", "management", "administration", "projet"));
        motsParFiliere.put("marketing", Arrays.asList("marketing", "commercial", "digital", "communication"));
        motsParFiliere.put("finance", Arrays.asList("finance", "banque", "trading", "investissement", "bourse"));
        motsParFiliere.put("comptabilité", Arrays.asList("comptabilité", "audit", "contrôle", "fiscal", "bilan"));
        motsParFiliere.put("ressources humaines", Arrays.asList("rh", "recrutement", "formation", "paie", "talent"));

        List<String> motsCles = motsParFiliere.entrySet().stream()
                .filter(e -> filiere.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(List.of(filiere));

        // Construire le contenu avec garde NPE
        StringBuilder contenu = new StringBuilder();
        contenu.append(nullSafe(offre.getTitre()).toLowerCase()).append(' ');
        contenu.append(nullSafe(offre.getDescription()).toLowerCase());
        if (offre instanceof Stage s)
            contenu.append(' ').append(nullSafe(s.getDomaine()).toLowerCase());
        if (offre instanceof ProjetFinEtudes p) {
            contenu.append(' ').append(nullSafe(p.getSujet()).toLowerCase());
            contenu.append(' ').append(nullSafe(p.getTechnologies()).toLowerCase());
        }

        String contenuStr = contenu.toString();
        long correspondances = motsCles.stream().filter(contenuStr::contains).count();
        return Math.min(1.0, correspondances / 3.0);
    }

    private double scoreNiveau(Etudiant etudiant, Offre offre) {
        String niveau = nullSafe(etudiant.getNiveau()).toLowerCase();
        String type = nullSafe(offre.getTypeOffre()).toLowerCase();

        if (niveau.contains("licence") && type.contains("stage")) return 1.0;
        if (niveau.contains("master") && type.contains("projet fin")) return 1.0;
        if (niveau.contains("master") && type.contains("alternance")) return 0.9;
        if (niveau.contains("licence") && type.contains("alternance")) return 0.7;
        return 0.5;
    }

    private double scoreNouveaute(Offre offre) {
        if (offre.getDatePublication() == null) return 0.5;
        long jours = ChronoUnit.DAYS.between(offre.getDatePublication(), LocalDate.now());
        if (jours <= 7) return 1.0;
        if (jours <= 30) return 0.7;
        if (jours <= 90) return 0.4;
        return 0.2;
    }

    private double scorePopularite(Offre offre) {
        int nb = offre.getNombreCandidatures();
        if (nb == 0) return 0.3;
        if (nb <= 5) return 0.8;
        if (nb <= 15) return 0.6;
        return 0.3;
    }

    private double scoreSecteur(String filiere, Offre offre) {
        String f = nullSafe(filiere).toLowerCase();
        String s = offre.getEntreprise() != null
                ? nullSafe(offre.getEntreprise().getSecteur()).toLowerCase() : "";

        if (f.contains("informatique") &&
                (s.contains("tech") || s.contains("it") || s.contains("logiciel"))) return 1.0;
        if (f.contains("finance") &&
                (s.contains("banque") || s.contains("finance") || s.contains("assurance"))) return 1.0;
        if (f.contains("marketing") &&
                (s.contains("marketing") || s.contains("communication"))) return 1.0;
        if (s.contains("conseil") || s.isEmpty()) return 0.6;
        return 0.4;
    }

    private static String nullSafe(String s) { return s == null ? "" : s; }

    // ========== DTO INTERNE ==========

    public static class OffreRecommandee {
        private final Offre offre;
        private final double score;

        public OffreRecommandee(Offre offre, double score) {
            this.offre = offre;
            this.score = score;
        }

        public Offre getOffre() { return offre; }
        public double getScore() { return score; }
        public String getScoreFormate() { return String.format("%.0f%%", score); }
    }
}
