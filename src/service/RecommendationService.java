package service;

import java.util.*;
import models.*;

/* Service de recommandation d'offres personnalisées pour les candidats.
 * Utilise un système de scoring basé sur plusieurs critères pondérés.*/
public class RecommendationService {
    
    private List<Offre> offres;
    
    // Poids des critères de recommandation (total = 100%)
    private static final double POIDS_FILIERE = 0.40;      // 40% - Correspondance filière
    private static final double POIDS_NIVEAU = 0.20;       // 20% - Niveau adapté au type d'offre
    private static final double POIDS_NOUVEAUTE = 0.15;    // 15% - Offres récentes
    private static final double POIDS_POPULARITE = 0.15;   // 15% - Offres populaires
    private static final double POIDS_SECTEUR = 0.10;      // 10% - Correspondance secteur
    
    public RecommendationService(List<Offre> offres) {
        this.offres = offres;
    }
    
    /* Génère des recommandations personnalisées pour un étudiant.
     * Retourne les offres triées par score de correspondance.*/
    public List<OffreRecommandee> getRecommandationsEtudiant(Etudiant etudiant, int nbRecommandations) {
        List<OffreRecommandee> recommendations = new ArrayList<>();
        
        // Parcourir toutes les offres disponibles
        for (Offre offre : offres) {
            // Ignorer les offres expirées
            if (offre.estExpiree()) {
                continue;
            }
            
            // Ignorer les offres auxquelles l'étudiant a déjà postulé
            if (etudiant.getCandidaturesEnCours().contains(offre)) {
                continue;
            }
            
            // Calculer le score de correspondance (0-100)
            double score = calculerScore(etudiant, offre);
            
            // Ajouter à la liste avec son score
            recommendations.add(new OffreRecommandee(offre, score));
        }
        
        // Trier par score décroissant (meilleurs scores en premier)
        recommendations.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()));
        
        // Retourner les N meilleures recommandations
        int limit = Math.min(nbRecommandations, recommendations.size());
        return recommendations.subList(0, limit);
    }
    
    /* Calcule le score de correspondance entre un étudiant et une offre.
     * Score final entre 0 et 100.*/
    private double calculerScore(Etudiant etudiant, Offre offre) {
        double scoreTotal = 0.0;
        
        // 1. Score filière (40%) - Le plus important
        scoreTotal += calculerScoreFiliere(etudiant, offre) * POIDS_FILIERE;
        
        // 2. Score niveau (20%) - Type d'offre adapté
        scoreTotal += calculerScoreNiveau(etudiant, offre) * POIDS_NIVEAU;
        
        // 3. Score nouveauté (15%) - Privilégier les offres récentes
        scoreTotal += calculerScoreNouveaute(offre) * POIDS_NOUVEAUTE;
        
        // 4. Score popularité (15%) - Offres avec candidatures modérées
        scoreTotal += calculerScorePopularite(offre) * POIDS_POPULARITE;
        
        // 5. Score secteur (10%) - Correspondance avec le secteur
        scoreTotal += calculerScoreSecteur(etudiant, offre) * POIDS_SECTEUR;
        
        // Convertir en pourcentage (0-100)
        return scoreTotal * 100;
    }
    
    /* Calcule le score de correspondance filière/domaine (0 à 1).
     * Cherche des mots-clés de la filière dans l'offre.*/
    private double calculerScoreFiliere(Etudiant etudiant, Offre offre) {
        String filiere = etudiant.getFiliere().toLowerCase();
        
        // Mots-clés par filière 
        Map<String, List<String>> motsClésFiliere = new HashMap<>();
        
        // Informatique
        motsClésFiliere.put("informatique", Arrays.asList("informatique", "dev", "développement", 
            "java", "python", "web", "mobile", "data", "ia", "intelligence artificielle", 
            "machine learning", "réseau", "cybersécurité", "cloud", "software", "logiciel"));
        
        // Gestion
        motsClésFiliere.put("gestion", Arrays.asList("gestion", "management", "administration", 
            "business", "organisation", "stratégie", "projet"));
        
        // Marketing
        motsClésFiliere.put("marketing", Arrays.asList("marketing", "commercial", "vente", 
            "communication", "digital", "réseaux sociaux", "publicité", "marque", "seo"));
        
        // Finance 
        motsClésFiliere.put("finance", Arrays.asList("finance", "financier", "banque", "trading", 
            "investissement", "assurance", "analyse financière", "bourse", "trésorerie", 
            "crédit", "risque", "portfolio", "hedge fund"));
        
        // Comptabilité
        motsClésFiliere.put("comptabilité", Arrays.asList("comptabilité", "comptable", "audit", 
            "contrôle", "fiscalité", "bilan", "contrôle de gestion", "consolidation", 
            "reporting", "expert-comptable", "normes comptables", "ifrs", "gaap"));
        
        // Ressources Humaines
        motsClésFiliere.put("ressources humaines", Arrays.asList("rh", "ressources humaines", 
            "recrutement", "formation", "paie", "talent", "carrière"));
        
        // Chercher les mots-clés correspondants à la filière
        List<String> motsClés = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : motsClésFiliere.entrySet()) {
            if (filiere.contains(entry.getKey())) {
                motsClés = entry.getValue();
                break;
            }
        }
        // Si pas de correspondance directe, utiliser la filière comme mot-clé
        if (motsClés.isEmpty()) {
            motsClés.add(filiere);
        }
        // Construire le contenu à analyser de l'offre
        String contenuOffre = (offre.getTitre() + " " + offre.getDescription()).toLowerCase();
        
        // Ajouter les champs spécifiques selon le type d'offre
        if (offre instanceof Stage) {
            contenuOffre += " " + ((Stage) offre).getDomaine().toLowerCase();
        } else if (offre instanceof ProjetFinEtudes) {
            contenuOffre += " " + ((ProjetFinEtudes) offre).getSujet().toLowerCase();
            contenuOffre += " " + ((ProjetFinEtudes) offre).getTechnologies().toLowerCase();
        }
        // Compter les correspondances de mots-clés
        int correspondances = 0;
        for (String motCle : motsClés) {
            if (contenuOffre.contains(motCle)) {
                correspondances++;
            }
        }
        // Score proportionnel au nombre de correspondances (max 1.0)
        // 3 correspondances ou plus = score parfait
        return Math.min(1.0, correspondances / 3.0);
    }
    
    /* Calcule le score de correspondance niveau/type d'offre (0 à 1).
     * Certaines combinaisons niveau-type sont plus adaptées.*/
    
    private double calculerScoreNiveau(Etudiant etudiant, Offre offre) {
        String niveau = etudiant.getNiveau().toLowerCase();
        String typeOffre = offre.getTypeOffre().toLowerCase();
        
        // Correspondances idéales niveau-type
        if (niveau.contains("licence") && typeOffre.contains("stage")) {
            return 1.0; // Parfait : Licence → Stage
        }
        if (niveau.contains("master") && typeOffre.contains("projet fin")) {
            return 1.0; // Parfait : Master → PFE
        }
        if (niveau.contains("master") && typeOffre.contains("alternance")) {
            return 0.9; // Très bien : Master → Alternance
        }
        if (niveau.contains("licence") && typeOffre.contains("alternance")) {
            return 0.7; // Bien : Licence → Alternance
        }        
        // Par défaut, correspondance moyenne
        return 0.5;
    }
    
    /* Calcule le score de nouveauté de l'offre (0 à 1).
     * Plus l'offre est récente, plus le score est élevé.*/
    
    private double calculerScoreNouveaute(Offre offre) {
        // Calculer le nombre de jours depuis la publication
        long joursDepuisPublication = java.time.temporal.ChronoUnit.DAYS.between(
            offre.getDatePublication(), 
            java.time.LocalDate.now()
        );
        
        // Score décroissant avec le temps
        if (joursDepuisPublication <= 7) {
            return 1.0; // Moins d'une semaine - Excellent
        } else if (joursDepuisPublication <= 30) {
            return 0.7; // Moins d'un mois - Bien
        } else if (joursDepuisPublication <= 90) {
            return 0.4; // Moins de 3 mois - Moyen
        } else {
            return 0.2; // Plus ancien - Faible
        }
    }
    
    /* Calcule le score de popularité de l'offre (0 à 1).
     * Basé sur le nombre de candidatures (éviter trop peu ou trop).*/
    
    private double calculerScorePopularite(Offre offre) {
        int nbCandidatures = offre.getCandidatures().size();
        
        // Score optimal pour popularité modérée
        if (nbCandidatures == 0) {
            return 0.3; // Nouvelle offre - Faible
        } else if (nbCandidatures <= 5) {
            return 0.8; // Popularité modérée - Meilleur score (moins de compétition)
        } else if (nbCandidatures <= 15) {
            return 0.6; // Assez populaire - Bien
        } else {
            return 0.3; // Très compétitive - Faible
        }
    }
    
    /* Calcule le score de correspondance avec le secteur (0 à 1).
     * Certains secteurs correspondent mieux à certaines filières.*/
    private double calculerScoreSecteur(Etudiant etudiant, Offre offre) {
        String filiere = etudiant.getFiliere().toLowerCase();
        String secteur = offre.getEntreprise().getSecteur().toLowerCase();
        
        // Correspondances filière-secteur
        
        // Informatique
        if (filiere.contains("informatique") && 
            (secteur.contains("informatique") || secteur.contains("tech") || 
             secteur.contains("digital") || secteur.contains("it") || 
             secteur.contains("logiciel"))) {
            return 1.0;
        }
        
        // Finance
        if (filiere.contains("finance") && 
            (secteur.contains("finance") || secteur.contains("banque") || 
             secteur.contains("assurance") || secteur.contains("investissement") ||
             secteur.contains("trading") || secteur.contains("bourse"))) {
            return 1.0;
        }
        
        // Comptabilité
        if (filiere.contains("comptabilité") && 
            (secteur.contains("comptabilité") || secteur.contains("audit") || 
             secteur.contains("expertise comptable") || secteur.contains("conseil") ||
             secteur.contains("fiduciaire"))) {
            return 1.0;
        }
        
        // Marketing
        if (filiere.contains("marketing") && 
            (secteur.contains("marketing") || secteur.contains("communication") || 
             secteur.contains("publicité") || secteur.contains("médias"))) {
            return 1.0;
        }
        
        // Gestion
        if (filiere.contains("gestion") && 
            (secteur.contains("gestion") || secteur.contains("management") || 
             secteur.contains("business") || secteur.contains("administration"))) {
            return 1.0;
        }
        
        // Secteurs polyvalents (acceptent toutes les filières)
        if (secteur.contains("conseil") || secteur.isEmpty()) {
            return 0.6;
        }
        
        // Pas de correspondance évidente
        return 0.4;
    }
    
    /*Génère des recommandations pour les Alumni.
      Logique différente car ils cherchent des postes plus avancés.*/
    
    public List<OffreRecommandee> getRecommandationsAlumni(Alumni alumni, int nbRecommandations) {
        List<OffreRecommandee> recommendations = new ArrayList<>();
        
        // Parcourir les offres disponibles
        for (Offre offre : offres) {
            // Ignorer si expirée ou déjà postulé
            if (offre.estExpiree() || alumni.getCandidaturesEnCours().contains(offre)) {
                continue;
            }
            
            // Score de base pour alumni
            double score = 50.0;
            
            // Privilégier alternances et PFE pour alumni
            if (offre.getTypeOffre().toLowerCase().contains("alternance")) {
                score += 30.0;
            }
            if (offre.getTypeOffre().toLowerCase().contains("projet")) {
                score += 20.0;
            }
            
            // Bonus si le secteur correspond au poste actuel
            String poste = alumni.getPosteActuel().toLowerCase();
            String secteur = offre.getEntreprise().getSecteur().toLowerCase();
            
            if (!poste.isEmpty() && secteur.contains(poste)) {
                score += 20.0;
            }
            
            // Limiter le score max à 100
            recommendations.add(new OffreRecommandee(offre, Math.min(100, score)));
        }
        
        // Trier par score décroissant
        recommendations.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()));
        
        // Retourner les N meilleures
        int limit = Math.min(nbRecommandations, recommendations.size());
        return recommendations.subList(0, limit);
    }
    
    /*Classe interne représentant une offre avec son score de recommandation.*/
    public static class OffreRecommandee {
        private Offre offre;
        private double score;
        
        public OffreRecommandee(Offre offre, double score) {
            this.offre = offre;
            this.score = score;
        }
        
        public Offre getOffre() {
            return offre;
        }
        
        public double getScore() {
            return score;
        }
        
        /* Retourne le score formaté en pourcentage.*/
        public String getScoreFormate() {
            return String.format("%.0f%%", score);
        }
    }
}