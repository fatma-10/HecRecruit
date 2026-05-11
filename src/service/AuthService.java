package service;

import java.util.*;
import models.*;

/**
 * Service gérant l'authentification et l'inscription des utilisateurs.
 * Gère la connexion/déconnexion des entreprises et candidats.
 */
public class AuthService {
    private List<Entreprise> entreprises;
    private List<Candidat> candidats;
    private Entreprise entrepriseConnectee;
    private Candidat candidatConnecte;

    public AuthService(List<Entreprise> entreprises, List<Candidat> candidats) {
        this.entreprises = entreprises;
        this.candidats = candidats;
        this.entrepriseConnectee = null;
        this.candidatConnecte = null;
    }

    // ========== CONNEXION ==========

    /**
     * Connexion d'une entreprise avec email et mot de passe.
     * Vérifie les identifiants et établit la session.
     */
    public boolean loginEntreprise(String email, String mdp) {
        // Parcourir toutes les entreprises
        for (Entreprise e : entreprises) {
            // Vérifier email (insensible à la casse) et mot de passe
            if (e.getEmail().equalsIgnoreCase(email) && 
                e.getMdp() != null && e.getMdp().equals(mdp)) {
                // Connexion réussie
                entrepriseConnectee = e;
                return true;
            }
        }
        // Aucune correspondance trouvée
        return false;
    }

    /**
     * Connexion d'un candidat avec email et mot de passe.
     * Vérifie les identifiants et établit la session.
     */
    public boolean loginCandidat(String email, String mdp) {
        // Parcourir tous les candidats
        for (Candidat c : candidats) {
            // Vérifier email (insensible à la casse) et mot de passe
            if (c.getEmail().equalsIgnoreCase(email) && 
                c.getMdp() != null && c.getMdp().equals(mdp)) {
                // Connexion réussie
                candidatConnecte = c;
                return true;
            }
        }
        // Aucune correspondance trouvée
        return false;
    }

    // ========== INSCRIPTION ==========

    /**
     * Inscription d'une nouvelle entreprise.
     * Vérifie que l'email n'existe pas déjà.
     */
    public boolean registerEntreprise(String nom, String secteur, String adresse, String email, 
                                     String telephone, String mdp) {
        // Vérifier si l'email est déjà utilisé
        for (Entreprise e : entreprises) {
            if (e.getEmail().equalsIgnoreCase(email)) {
                return false; // Email déjà existant
            }
        }

        try {
            // Créer la nouvelle entreprise
            Entreprise nouvelle = new Entreprise(nom, secteur, adresse, email, telephone, mdp);
            entreprises.add(nouvelle);
            
            // Connexion automatique après inscription
            entrepriseConnectee = nouvelle;
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur d'inscription: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inscription d'un nouveau candidat (Etudiant, Alumni ou Candidat simple).
     * VALIDATION EMAIL : Doit se terminer par @ihec.ucar.tn pour les candidats.
     */
    public boolean registerCandidat(String nom, String prenom, String email, String telephone, 
                                   String mdp, String typeCandidat, Map<String, String> infosSuppl) {
        // ✅ VALIDATION EMAIL : Doit finir par @ihec.ucar.tn
        if (!email.toLowerCase().endsWith("@ihec.ucar.tn")) {
            System.out.println("Erreur: L'email doit se terminer par @ihec.ucar.tn");
            return false;
        }

        // Vérifier si l'email est déjà utilisé
        for (Candidat c : candidats) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return false; // Email déjà existant
            }
        }

        try {
            Candidat nouveau;
            
            // Créer le candidat selon son type
            if (typeCandidat.equals("etudiant")) {
                // Récupérer l'ID depuis les infos supplémentaires
                int id = 0;
                if (infosSuppl.containsKey("id")) {
                    id = Integer.parseInt(infosSuppl.get("id"));
                }
                
                // Créer un étudiant
                nouveau = new Etudiant(
                    id, nom, prenom, email, telephone, mdp,
                    infosSuppl.get("niveau"),
                    infosSuppl.get("filiere"),
                    infosSuppl.get("etablissement"),
                    candidats
                );
                
            } else if (typeCandidat.equals("alumni")) {
                // Récupérer l'ID depuis les infos supplémentaires
                int id = 0;
                if (infosSuppl.containsKey("id")) {
                    id = Integer.parseInt(infosSuppl.get("id"));
                }
                
                // Créer un alumni
                nouveau = new Alumni(
                    id, nom, prenom, email, telephone, mdp,
                    Integer.parseInt(infosSuppl.get("anneeDiplome")),
                    infosSuppl.get("posteActuel"),
                    infosSuppl.get("entrepriseActuelle"),
                    candidats
                );
                
            } else {
                // Créer un candidat simple
                int id = 0;
                if (infosSuppl.containsKey("id")) {
                    id = Integer.parseInt(infosSuppl.get("id"));
                }
                nouveau = new Candidat(id, nom, prenom, email, telephone, mdp, candidats);
            }

            // Ajouter à la liste et connecter automatiquement
            candidats.add(nouveau);
            candidatConnecte = nouveau;
            return true;
            
        } catch (Exception e) {
            System.out.println("Erreur d'inscription: " + e.getMessage());
            return false;
        }
    }

    /**
     * Déconnexion de l'utilisateur actuel.
     * Réinitialise la session.
     */
    public void logout() {
        entrepriseConnectee = null;
        candidatConnecte = null;
    }

    // ========== GETTERS ==========

    public Entreprise getEntrepriseConnectee() {
        return entrepriseConnectee;
    }

    public Candidat getCandidatConnecte() {
        return candidatConnecte;
    }

    public boolean estEntrepriseConnectee() {
        return entrepriseConnectee != null;
    }

    public boolean estCandidatConnecte() {
        return candidatConnecte != null;
    }
}