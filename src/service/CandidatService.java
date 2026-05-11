package service;

import java.util.*;
import models.*;

/**
 * Service gérant les opérations sur les candidats.
 * Permet la recherche et la modification des profils candidats.
 */
public class CandidatService {
    private List<Candidat> candidats;

    public CandidatService(List<Candidat> candidats) {
        this.candidats = candidats;
    }

    /**
     * Retourne tous les candidats du système.
     */
    public List<Candidat> getAllCandidats() {
        return candidats;
    }

    /**
     * Recherche des candidats selon différents critères.
     * Critères supportés : nom, email, etudiant (filière/établissement), 
     * alumni (entreprise actuelle), toutes (recherche globale).
     */
    public List<Candidat> rechercherCandidats(String critere, String valeur) {
        List<Candidat> resultats = new ArrayList<>();
        
        // Parcourir tous les candidats
        for (Candidat c : candidats) {
            boolean match = false;
            
            // Vérifier selon le critère de recherche
            switch (critere.toLowerCase()) {
                case "nom":
                    // Recherche par nom (insensible à la casse)
                    match = c.getNom().toLowerCase().contains(valeur.toLowerCase());
                    break;
                    
                case "email":
                    // Recherche par email (exact, insensible à la casse)
                    match = c.getEmail().equalsIgnoreCase(valeur);
                    break;
                    
                case "etudiant":
                    // Recherche dans filière ou établissement (uniquement pour étudiants)
                    if (c instanceof Etudiant) {
                        Etudiant etud = (Etudiant) c;
                        match = etud.getFiliere().toLowerCase().contains(valeur.toLowerCase()) ||
                               etud.getEtablissement().toLowerCase().contains(valeur.toLowerCase());
                    }
                    break;
                    
                case "alumni":
                    // Recherche dans entreprise actuelle (uniquement pour alumni)
                    if (c instanceof Alumni) {
                        Alumni alumni = (Alumni) c;
                        match = alumni.getEntrepriseActuelle().toLowerCase().contains(valeur.toLowerCase());
                    }
                    break;
                    
                case "toutes":
                    // Recherche globale dans nom, prénom et email
                    match = c.getNom().toLowerCase().contains(valeur.toLowerCase()) ||
                           c.getPrenom().toLowerCase().contains(valeur.toLowerCase()) ||
                           c.getEmail().toLowerCase().contains(valeur.toLowerCase());
                    break;
            }
            
            // Ajouter aux résultats si correspond
            if (match) {
                resultats.add(c);
            }
        }
        
        return resultats;
    }

    /**
     * Modifie le profil d'un candidat.
     * Permet de mettre à jour les informations selon le type de candidat.
     */
    public boolean modifierProfil(Candidat candidat, Map<String, String> nouvellesInfos) {
        try {
            // Modification du téléphone (commun à tous)
            if (nouvellesInfos.containsKey("telephone")) {
                candidat.setTelephone(nouvellesInfos.get("telephone"));
            }
            
            // Modifications spécifiques aux étudiants
            if (candidat instanceof Etudiant) {
                Etudiant etud = (Etudiant) candidat;
                
                if (nouvellesInfos.containsKey("niveau")) {
                    etud.setNiveau(nouvellesInfos.get("niveau"));
                }
                if (nouvellesInfos.containsKey("filiere")) {
                    etud.setFiliere(nouvellesInfos.get("filiere"));
                }
                if (nouvellesInfos.containsKey("etablissement")) {
                    etud.setEtablissement(nouvellesInfos.get("etablissement"));
                }
            } 
            // Modifications spécifiques aux alumni
            else if (candidat instanceof Alumni) {
                Alumni alumni = (Alumni) candidat;
                
                if (nouvellesInfos.containsKey("posteActuel")) {
                    alumni.setPosteActuel(nouvellesInfos.get("posteActuel"));
                }
                if (nouvellesInfos.containsKey("entrepriseActuelle")) {
                    alumni.setEntrepriseActuelle(nouvellesInfos.get("entrepriseActuelle"));
                }
            }
            
            return true;
        } catch (Exception e) {
            System.out.println("Erreur modification profil: " + e.getMessage());
            return false;
        }
    }
}