package service;

import java.util.*;
import models.*;

/* Service gérant le forum de discussion.
 * Permet aux étudiants et entreprises d'échanger des messages.*/
public class ForumService {
    private List<Forum> commentaires;

    public ForumService(List<Forum> commentaires) {
        this.commentaires = commentaires;
    }

    /* Ajoute un nouveau commentaire au forum.
     * Vérifie que le message n'est pas vide.*/
    public boolean ajouterCommentaire(String auteur, String email, 
                                     String message, boolean estEtudiant) {
        // Validation du message
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Le message ne peut pas être vide!");
            return false;
        }
        
        // Créer et ajouter le commentaire
        Forum nouveau = new Forum(auteur, email, message, estEtudiant);
        commentaires.add(nouveau);
        return true;
    }

    /* Retourne tous les commentaires triés par date (plus récents en premier).*/
    public List<Forum> getAllCommentaires() {
        // Trier par date décroissante (plus récents d'abord)
        commentaires.sort((f1, f2) -> 
            f2.getDatePublication().compareTo(f1.getDatePublication()));
        return commentaires;
    }

    /* Recherche des commentaires selon différents critères.
     * Critères supportés : auteur, message, etudiant, entreprise.*/
    public List<Forum> rechercherCommentaires(String critere, String valeur) {
        List<Forum> resultats = new ArrayList<>();
        
        // Parcourir tous les commentaires
        for (Forum f : commentaires) {
            boolean match = false;
            
            // Vérifier selon le critère
            switch (critere.toLowerCase()) {
                case "auteur":
                    // Recherche par nom d'auteur
                    match = f.getAuteur().toLowerCase().contains(valeur.toLowerCase());
                    break;
                    
                case "message":
                    // Recherche dans le contenu du message
                    match = f.getMessage().toLowerCase().contains(valeur.toLowerCase());
                    break;
                    
                case "etudiant":
                    // Recherche uniquement dans les messages d'étudiants
                    match = f.isEstEtudiant() && 
                           (f.getAuteur().toLowerCase().contains(valeur.toLowerCase()) ||
                            f.getMessage().toLowerCase().contains(valeur.toLowerCase()));
                    break;
                    
                case "entreprise":
                    // Recherche uniquement dans les messages d'entreprises
                    match = !f.isEstEtudiant() && 
                           (f.getAuteur().toLowerCase().contains(valeur.toLowerCase()) ||
                            f.getMessage().toLowerCase().contains(valeur.toLowerCase()));
                    break;
            }
            
            // Ajouter aux résultats si correspond
            if (match) {
                resultats.add(f);
            }
        }
        
        // Trier les résultats par date décroissante
        resultats.sort((f1, f2) -> 
            f2.getDatePublication().compareTo(f1.getDatePublication()));
        
        return resultats;
    }

    /**
     * Retourne les 10 derniers commentaires.
     * Utilisé pour l'affichage de la page d'accueil du forum.
     */
    public List<Forum> getDerniersCommentaires() {
        // Récupérer tous les commentaires triés
        List<Forum> tous = getAllCommentaires();
        
        // Retourner les 10 premiers (ou moins si pas assez)
        int limit = Math.min(10, tous.size());
        return tous.subList(0, limit);
    }

    /**
     * Calcule des statistiques sur le forum.
     * Retourne le nombre total de commentaires et la répartition étudiants/entreprises.
     */
    public Map<String, Integer> getStatistiquesForum() {
        Map<String, Integer> stats = new HashMap<>();
        
        int total = commentaires.size();
        int etudiants = 0;
        int entreprises = 0;
        
        // Compter les commentaires par type d'auteur
        for (Forum f : commentaires) {
            if (f.isEstEtudiant()) {
                etudiants++;
            } else {
                entreprises++;
            }
        }
        
        // Remplir le map de statistiques
        stats.put("total", total);
        stats.put("etudiants", etudiants);
        stats.put("entreprises", entreprises);
        
        return stats;
    }
}