package service;

import java.util.*;
import java.time.LocalDate;
import models.*;

/*Service de gestion des offres (stages, alternances, projets fin d'études).
 * 
 * Ce service permet de :
 * - Créer différents types d'offres spécialisées
 * - Rechercher des offres selon divers critères
 * - Gérer les dates d'expiration
 * - Obtenir des statistiques sur les offres
 * - Supprimer des offres
 * 
 * Le service utilise la hiérarchie : Offre → OffreSpecialisee → Stage/Alternance/ProjetFinEtudes*/
public class OffreService {
    
    // ----------------------------- Attributs -----------------------------
    
    /** Liste de toutes les offres du système */
    private List<Offre> offres;

    // ----------------------------- Constructeur -----------------------------
    
    /**
     * Construit un nouveau service de gestion des offres.
     * 
     * @param offres Liste des offres du système
     */
    public OffreService(List<Offre> offres) {
        this.offres = offres;
    }

    // ========== CRÉATION ET GESTION DES OFFRES ==========
    
    /*Crée une nouvelle offre spécialisée selon le type demandé.
     * 
     * Cette méthode utilise le polymorphisme pour créer le bon type d'offre :
     * - Stage : nécessite durée et domaine
     * - Alternance : nécessite rythme et durée
     * - Projet Fin d'Etudes : nécessite sujet et technologies
     * 
     * Les informations spécifiques à chaque type sont passées via infosSuppl.
     * L'offre créée est automatiquement ajoutée à la liste des offres de l'entreprise.
     * 
     * @param titre Titre de l'offre
     * @param description Description détaillée
     * @param type Type d'offre ("stage", "alternance", ou "projet fin d'etudes")
     * @param entreprise Entreprise qui publie l'offre
     * @param infosSuppl Map contenant les informations spécifiques au type d'offre
     * @return true si la création a réussi, false en cas d'erreur*/
    public boolean creerOffre(String titre, String description, String type, 
                             Entreprise entreprise, Map<String, String> infosSuppl) {
        try {
            // Variable pour stocker l'offre créée
            // On utilise OffreSpecialisee pour bénéficier du polymorphisme
            OffreSpecialisee nouvelleOffre = null;
            
            // Créer le type approprié d'offre selon le paramètre type
            switch (type.toLowerCase()) {
                case "stage":
                    // Création d'un stage avec durée et domaine
                    nouvelleOffre = new Stage(
                        titre, 
                        description, 
                        entreprise,
                        Integer.parseInt(infosSuppl.get("duree")),  // Durée en mois
                        infosSuppl.get("domaine")                    // Domaine d'activité
                    );
                    System.out.println("✅ Stage créé avec succès");
                    break;
                    
                case "alternance":
                    // Création d'une alternance avec rythme et durée
                    nouvelleOffre = new Alternance(
                        titre, 
                        description, 
                        entreprise,
                        infosSuppl.get("rythme"),                    // Rythme (ex: "3j/2j")
                        Integer.parseInt(infosSuppl.get("duree"))   // Durée en mois
                    );
                    System.out.println("✅ Alternance créée avec succès");
                    break;
                    
                case "projet fin d'etudes":
                    // Création d'un PFE avec sujet et technologies
                    nouvelleOffre = new ProjetFinEtudes(
                        titre, 
                        description, 
                        entreprise,
                        infosSuppl.get("sujet"),                     // Sujet du projet
                        infosSuppl.get("technologies")               // Technologies utilisées
                    );
                    System.out.println("✅ Projet de fin d'études créé avec succès");
                    break;
                    
                default:
                    // Type d'offre non reconnu
                    System.out.println("❌ Type d'offre non reconnu : " + type);
                    return false;
            }
            
            // Si l'offre a été créée avec succès
            if (nouvelleOffre != null) {
                // Ajouter à la liste générale des offres
                offres.add(nouvelleOffre);
                
                // Ajouter à la liste des offres de l'entreprise
                entreprise.getOffresPubliees().add(nouvelleOffre);
                
                return true;
            }
            
        } catch (NumberFormatException e) {
            // Erreur de conversion de la durée en nombre
            System.out.println("❌ Erreur : La durée doit être un nombre valide");
        } catch (IllegalArgumentException e) {
            // Erreur de validation (durée négative, champs vides, etc.)
            System.out.println("❌ Erreur de validation : " + e.getMessage());
        } catch (Exception e) {
            // Autres erreurs inattendues
            System.out.println("❌ Erreur lors de la création de l'offre : " + e.getMessage());
        }
        
        return false;
    }

    /* Supprime une offre du système.
     * 
     * Cette méthode effectue un nettoyage complet :
     * 1. Vérifie que l'offre existe et appartient bien à l'entreprise
     * 2. Retire l'offre de toutes les candidatures des candidats
     * 3. Retire l'offre de la liste de l'entreprise
     * 4. Retire l'offre de la liste générale
     * 
     * @param idOffre ID (UUID) de l'offre à supprimer
     * @param entreprise Entreprise qui tente de supprimer l'offre
     * @return true si la suppression a réussi, false sinon*/
    public boolean supprimerOffre(String idOffre, Entreprise entreprise) {
        // Parcourir toutes les offres pour trouver celle à supprimer
        for (int i = 0; i < offres.size(); i++) {
            Offre offre = offres.get(i);
            
            // Vérifier que c'est la bonne offre et qu'elle appartient à l'entreprise
            if (offre.getId().toString().equals(idOffre) && 
                offre.getEntreprise().equals(entreprise)) {
                
                // Étape 1 : Retirer l'offre des candidatures de tous les candidats
                for (Candidat c : offre.getCandidatures()) {
                    c.getCandidaturesEnCours().remove(offre);
                }
                
                // Étape 2 : Retirer de la liste des offres de l'entreprise
                entreprise.getOffresPubliees().remove(offre);
                
                // Étape 3 : Retirer de la liste générale des offres
                offres.remove(i);
                
                System.out.println("✅ Offre supprimée avec succès");
                return true;
            }
        }
        
        // Offre non trouvée ou n'appartient pas à l'entreprise
        System.out.println("❌ Offre non trouvée ou vous n'avez pas les droits pour la supprimer");
        return false;
    }

    // ========== RECHERCHE D'OFFRES ==========
    
    /* Recherche des offres selon un critère et une valeur.
     * 
     * Critères disponibles :
     * - "titre" : recherche dans le titre de l'offre
     * - "type" : recherche par type exact (Stage, Alternance, etc.)
     * - "entreprise" : recherche par nom d'entreprise
     * - "domaine" : recherche le domaine (pour les stages uniquement)
     * - "toutes" : recherche dans tous les champs
     * 
     * Seules les offres non expirées sont retournées.
     * 
     * @param critere Le critère de recherche
     * @param valeur La valeur à rechercher
     * @return Liste des offres correspondantes*/
    public List<Offre> rechercherOffres(String critere, String valeur) {
        List<Offre> resultats = new ArrayList<>();
        
        // Parcourir toutes les offres
        for (Offre offre : offres) {
            // Ignorer les offres expirées
            if (!offre.estExpiree()) {
                boolean match = false;
                
                // Appliquer le critère de recherche
                switch (critere.toLowerCase()) {
                    case "titre":
                        // Recherche dans le titre (insensible à la casse)
                        match = offre.getTitre().toLowerCase().contains(valeur.toLowerCase());
                        break;
                        
                    case "type":
                        // Recherche par type exact
                        match = offre.getTypeOffre().equalsIgnoreCase(valeur);
                        break;
                        
                    case "entreprise":
                        // Recherche dans le nom de l'entreprise
                        match = offre.getEntreprise().getNom().toLowerCase()
                                    .contains(valeur.toLowerCase());
                        break;
                        
                    case "domaine":
                        // Recherche spécifique pour les stages
                        // Utilisation de instanceof pour vérifier le type réel
                        if (offre instanceof Stage) {
                            Stage stage = (Stage) offre;
                            match = stage.getDomaine().toLowerCase()
                                        .contains(valeur.toLowerCase());
                        }
                        break;
                        
                    case "toutes":
                        // Recherche globale dans tous les champs
                        match = offre.getTitre().toLowerCase().contains(valeur.toLowerCase()) ||
                               offre.getTypeOffre().toLowerCase().contains(valeur.toLowerCase()) ||
                               offre.getEntreprise().getNom().toLowerCase()
                                    .contains(valeur.toLowerCase()) ||
                               offre.getDescription().toLowerCase().contains(valeur.toLowerCase());
                        break;
                }
                
                // Si l'offre correspond, l'ajouter aux résultats
                if (match) {
                    resultats.add(offre);
                }
            }
        }
        
        System.out.println("🔍 " + resultats.size() + " offre(s) trouvée(s)");
        return resultats;
    }

    /* Recherche une offre spécifique d'une entreprise par son ID.
     * 
     * @param idOffre ID de l'offre recherchée
     * @param entreprise Entreprise à laquelle l'offre doit appartenir
     * @return L'offre trouvée, ou null si non trouvée*/
    public Offre rechercherOffreEntreprise(String idOffre, Entreprise entreprise) {
        // Parcourir les offres de l'entreprise
        for (Offre offre : entreprise.getOffresPubliees()) {
            if (offre.getId().toString().equals(idOffre)) {
                return offre;
            }
        }
        return null;
    }

    // ========== RÉCUPÉRATION D'OFFRES ==========
    
    /* Retourne toutes les offres du système.
     * 
     * @return Liste complète des offres*/
    public List<Offre> getAllOffres() { 
        return offres; 
    }

    /* Retourne toutes les offres publiées par une entreprise spécifique.
     * 
     * @param entreprise L'entreprise dont on veut les offres
     * @return Liste des offres de l'entreprise*/
    public List<Offre> getOffresEntreprise(Entreprise entreprise) {
        List<Offre> resultats = new ArrayList<>();
        for (Offre offre : offres) {
            if (offre.getEntreprise().equals(entreprise)) {
                resultats.add(offre);
            }
        }
        return resultats;
    }

    /* Retourne uniquement les offres disponibles (non expirées).
     * 
     * @return Liste des offres actives*/
    public List<Offre> getOffresDisponibles() {
        List<Offre> resultats = new ArrayList<>();
        for (Offre offre : offres) {
            if (!offre.estExpiree()) {
                resultats.add(offre);
            }
        }
        return resultats;
    }

    /*Retourne les offres d'une entreprise sous forme de tableau formaté.
     * Utilisé pour l'affichage dans les interfaces utilisateur.
     * 
     * @param entreprise L'entreprise dont on veut les offres
     * @return Liste de tableaux contenant les informations principales*/
    public List<String[]> getOffresFormateesEntreprise(Entreprise entreprise) {
        List<String[]> result = new ArrayList<>();
        for (Offre offre : entreprise.getOffresPubliees()) {
            // Utilise la méthode getInfosPrincipales() polymorphe
            result.add(offre.getInfosPrincipales());
        }
        return result;
    }

    // ========== GESTION DES DATES ==========
    
    /*Définit la date d'expiration d'une offre.
     * 
     * La date d'expiration doit être dans le futur.
     * Seul le propriétaire de l'offre (l'entreprise) peut modifier cette date.
     * 
     * @param idOffre ID de l'offre
     * @param date Nouvelle date d'expiration
     * @param entreprise Entreprise qui tente de modifier la date
     * @return true si la modification a réussi, false sinon*/
    public boolean setDateExpiration(String idOffre, LocalDate date, Entreprise entreprise) {
        for (Offre offre : offres) {
            // Vérifier que c'est la bonne offre et qu'elle appartient à l'entreprise
            if (offre.getId().toString().equals(idOffre) && 
                offre.getEntreprise().equals(entreprise)) {
                
                // Vérifier que la date est dans le futur
                if (date.isAfter(LocalDate.now())) {
                    offre.setDateExpiration(date);
                    System.out.println("✅ Date d'expiration mise à jour : " + date);
                    return true;
                } else {
                    System.out.println("❌ La date d'expiration doit être dans le futur");
                    return false;
                }
            }
        }
        System.out.println("❌ Offre non trouvée ou accès refusé");
        return false;
    }

    // ========== STATISTIQUES ==========
    
    /* Calcule et retourne des statistiques globales sur les offres.
     * 
     * Les statistiques incluent :
     * - Nombre de stages, alternances, PFE et autres offres
     * - Nombre d'offres actives vs expirées
     * - Nombre total d'offres
     * 
     * @return Map contenant les différentes statistiques*/
    public Map<String, Integer> getStatistiques() {
        Map<String, Integer> stats = new HashMap<>();
        
        // Initialisation des compteurs
        int stages = 0, alternances = 0, pfe = 0, autres = 0;
        int actives = 0, expirees = 0;
        
        // Parcourir toutes les offres pour calculer les statistiques
        for (Offre offre : offres) {
            // Compter par type d'offre
            String type = offre.getTypeOffre().toLowerCase();
            if (type.contains("stage")) {
                stages++;
            } else if (type.contains("alternance")) {
                alternances++;
            } else if (type.contains("projet fin")) {
                pfe++;
            } else {
                autres++;
            }
            
            // Compter par état (active ou expirée)
            if (offre.estExpiree()) {
                expirees++;
            } else {
                actives++;
            }
        }
        
        // Remplir le Map avec les résultats
        stats.put("stages", stages);
        stats.put("alternances", alternances);
        stats.put("pfe", pfe);
        stats.put("autres", autres);
        stats.put("actives", actives);
        stats.put("expirees", expirees);
        stats.put("total", offres.size());
        
        return stats;
    }

    /* Compte le nombre d'offres actives (non expirées) d'une entreprise.
     * 
     * @param entreprise L'entreprise dont on veut compter les offres actives
     * @return Le nombre d'offres actives*/
    public int getNombreOffresActives(Entreprise entreprise) {
        int count = 0;
        for (Offre offre : entreprise.getOffresPubliees()) {
            if (!offre.estExpiree()) {
                count++;
            }
        }
        return count;
    }
}