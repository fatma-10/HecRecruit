package service;

import java.util.*;
import models.*;

/**
 * Service gérant les opérations sur les entreprises.
 * Permet la recherche et la modification des profils entreprises.
 */
public class EntrepriseService {
    private List<Entreprise> entreprises;

    public EntrepriseService(List<Entreprise> entreprises) {
        this.entreprises = entreprises;
    }

    /**
     * Retourne toutes les entreprises du système.
     */
    public List<Entreprise> getAllEntreprises() {
        return entreprises;
    }

    /**
     * Recherche des entreprises selon différents critères.
     * Critères supportés : nom, secteur, email, toutes (recherche globale).
     */
    public List<Entreprise> rechercherEntreprises(String critere, String valeur) {
        List<Entreprise> resultats = new ArrayList<>();
        
        // Parcourir toutes les entreprises
        for (Entreprise e : entreprises) {
            boolean match = false;
            
            // Vérifier selon le critère de recherche
            switch (critere.toLowerCase()) {
                case "nom":
                    // Recherche par nom (insensible à la casse)
                    match = e.getNom().toLowerCase().contains(valeur.toLowerCase());
                    break;
                    
                case "secteur":
                    // Recherche par secteur d'activité
                    match = e.getSecteur().toLowerCase().contains(valeur.toLowerCase());
                    break;
                    
                case "email":
                    // Recherche par email (exact, insensible à la casse)
                    match = e.getEmail().equalsIgnoreCase(valeur);
                    break;
                    
                case "toutes":
                    // Recherche globale dans nom, secteur et adresse
                    match = e.getNom().toLowerCase().contains(valeur.toLowerCase()) ||
                           e.getSecteur().toLowerCase().contains(valeur.toLowerCase()) ||
                           e.getAdresse().toLowerCase().contains(valeur.toLowerCase());
                    break;
            }
            
            // Ajouter aux résultats si correspond
            if (match) {
                resultats.add(e);
            }
        }
        
        return resultats;
    }

    /**
     * Modifie le profil d'une entreprise.
     * Permet de mettre à jour secteur, adresse et téléphone.
     */
    public boolean modifierProfil(Entreprise entreprise, Map<String, String> nouvellesInfos) {
        try {
            // Modification du secteur
            if (nouvellesInfos.containsKey("secteur")) {
                entreprise.setSecteur(nouvellesInfos.get("secteur"));
            }
            
            // Modification de l'adresse
            if (nouvellesInfos.containsKey("adresse")) {
                entreprise.setAdresse(nouvellesInfos.get("adresse"));
            }
            
            // Modification du téléphone
            if (nouvellesInfos.containsKey("telephone")) {
                entreprise.setTelephone(nouvellesInfos.get("telephone"));
            }
            
            return true;
        } catch (Exception e) {
            System.out.println("Erreur modification profil: " + e.getMessage());
            return false;
        }
    }
}