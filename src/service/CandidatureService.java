package service;

import java.util.*;
import models.*;

/**
 * Service gérant les candidatures et la wishlist des entreprises.
 * Gère le lien bidirectionnel entre candidats et offres.
 */
public class CandidatureService {
    private List<Offre> offres;
    private List<Candidat> candidats;

    public CandidatureService(List<Offre> offres, List<Candidat> candidats) {
        this.offres = offres;
        this.candidats = candidats;
    }

    // ========== GESTION DES CANDIDATURES ==========
    
    /**
     * Permet à un candidat de postuler à une offre.
     * Vérifie que l'offre existe, n'est pas expirée et que le candidat n'a pas déjà postulé.
     */
    public boolean postulerOffre(Candidat candidat, String idOffre) {
        // Rechercher l'offre
        Offre offre = trouverOffre(idOffre);
        
        if (offre == null) {
            System.out.println("Offre non trouvée!");
            return false;
        }
        
        // Vérifier si l'offre est expirée
        if (offre.estExpiree()) {
            System.out.println("Cette offre est expirée!");
            return false;
        }
        
        // Vérifier si le candidat a déjà postulé
        if (candidat.getCandidaturesEnCours().contains(offre)) {
            System.out.println("Vous avez déjà postulé à cette offre!");
            return false;
        }
        
        // Ajouter la candidature des deux côtés (bidirectionnel)
        candidat.getCandidaturesEnCours().add(offre);
        offre.ajouterCandidature(candidat);
        return true;
    }

    /**
     * Permet à un candidat de retirer sa candidature d'une offre.
     */
    public boolean retirerCandidature(Candidat candidat, String idOffre) {
        // Rechercher l'offre
        Offre offre = trouverOffre(idOffre);
        
        if (offre == null) {
            System.out.println("Offre non trouvée!");
            return false;
        }
        
        // Vérifier si le candidat a postulé
        if (!candidat.getCandidaturesEnCours().contains(offre)) {
            System.out.println("Vous n'avez pas postulé à cette offre!");
            return false;
        }
        
        // Retirer la candidature des deux côtés (bidirectionnel)
        candidat.getCandidaturesEnCours().remove(offre);
        offre.getCandidatures().remove(candidat);
        return true;
    }

    /**
     * Retourne toutes les candidatures d'un candidat.
     */
    public List<Offre> getCandidatures(Candidat candidat) {
        return candidat.getCandidaturesEnCours();
    }

    /**
     * Retourne les candidatures d'un candidat sous forme de tableau formaté.
     * Utilisé pour l'affichage dans l'interface.
     */
    public List<String[]> getCandidaturesFormatees(Candidat candidat) {
        List<String[]> result = new ArrayList<>();
        
        // Convertir chaque offre en tableau de strings
        for (Offre offre : candidat.getCandidaturesEnCours()) {
            result.add(offre.getInfosPrincipales());
        }
        
        return result;
    }

    /**
     * Compte le nombre de candidatures actives (non expirées) d'un candidat.
     */
    public int getNombreCandidaturesActives(Candidat candidat) {
        int count = 0;
        
        // Parcourir les candidatures et compter les actives
        for (Offre offre : candidat.getCandidaturesEnCours()) {
            if (!offre.estExpiree()) {
                count++;
            }
        }
        
        return count;
    }

    // ========== GESTION DES CANDIDATS D'UNE OFFRE ==========
    
    /**
     * Retourne la liste des candidats ayant postulé à une offre.
     */
    public List<Candidat> getCandidatsOffre(String idOffre) {
        Offre offre = trouverOffre(idOffre);
        
        if (offre != null) {
            return offre.getCandidatures();
        }
        
        return new ArrayList<>();
    }

    /**
     * Retourne les candidats d'une offre sous forme de tableau formaté.
     * Utilisé pour l'affichage dans l'interface entreprise.
     */
    public List<String[]> getCandidaturesFormateesOffre(String idOffre) {
        List<String[]> result = new ArrayList<>();
        Offre offre = trouverOffre(idOffre);
        
        if (offre != null) {
            // Convertir chaque candidat en tableau de strings
            for (Candidat candidat : offre.getCandidatures()) {
                result.add(candidat.getInfosPrincipales());
            }
        }
        
        return result;
    }

    /**
     * Supprime une candidature spécifique d'une offre (action entreprise).
     * Retire le lien bidirectionnel entre candidat et offre.
     */
    public boolean supprimerCandidatureOffre(String idOffre, String idCandidat) {
        Offre offre = trouverOffre(idOffre);
        Candidat candidat = trouverCandidat(idCandidat);
        
        if (offre == null || candidat == null) {
            return false;
        }
        
        // Retirer des deux côtés (bidirectionnel)
        offre.getCandidatures().remove(candidat);
        candidat.getCandidaturesEnCours().remove(offre);
        return true;
    }

    /**
     * Recherche un candidat spécifique dans les candidatures d'une offre.
     */
    public Candidat rechercherCandidatureOffre(String idOffre, String idCandidat) {
        Offre offre = trouverOffre(idOffre);
        
        if (offre == null) {
            return null;
        }
        
        // Vérifier si le candidat a postulé
        Candidat candidat = trouverCandidat(idCandidat);
        if (candidat != null && offre.getCandidatures().contains(candidat)) {
            return candidat;
        }
        
        return null;
    }

    // ========== GESTION DE LA WISHLIST ==========
    
    /**
     * Ajoute un candidat à la wishlist d'une entreprise.
     * La wishlist permet aux entreprises de sauvegarder des profils intéressants.
     */
    public boolean ajouterWishlist(Entreprise entreprise, String idCandidat) {
        Candidat candidat = trouverCandidat(idCandidat);
        
        // Vérifier que le candidat existe et n'est pas déjà dans la wishlist
        if (candidat != null && !entreprise.getWishlist().contains(candidat)) {
            entreprise.getWishlist().add(candidat);
            return true;
        }
        
        return false;
    }

    /**
     * Retire un candidat de la wishlist d'une entreprise.
     */
    public boolean retirerWishlist(Entreprise entreprise, String idCandidat) {
        try {
            // Convertir l'ID string en int (Candidat utilise int comme ID)
            int id = Integer.parseInt(idCandidat);
            
            // Parcourir la wishlist pour trouver le candidat
            for (int i = 0; i < entreprise.getWishlist().size(); i++) {
                if (entreprise.getWishlist().get(i).getId() == id) {
                    entreprise.getWishlist().remove(i);
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("ID candidat invalide: " + idCandidat);
        }
        
        return false;
    }

    /**
     * Retourne la wishlist d'une entreprise sous forme de tableau formaté.
     */
    public List<String[]> getWishlistFormatee(Entreprise entreprise) {
        List<String[]> result = new ArrayList<>();
        
        // Convertir chaque candidat en tableau de strings
        for (Candidat candidat : entreprise.getWishlist()) {
            result.add(candidat.getInfosPrincipales());
        }
        
        return result;
    }

    /**
     * Recherche un candidat dans la wishlist d'une entreprise.
     */
    public Candidat rechercherCandidatWishlist(String idCandidat, Entreprise entreprise) {
        try {
            // Convertir l'ID string en int
            int id = Integer.parseInt(idCandidat);
            
            // Parcourir la wishlist
            for (Candidat candidat : entreprise.getWishlist()) {
                if (candidat.getId() == id) {
                    return candidat;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("ID candidat invalide: " + idCandidat);
        }
        
        return null;
    }

    // ========== MÉTHODES UTILITAIRES PRIVÉES ==========
    
    /**
     * Trouve une offre par son ID (UUID).
     */
    private Offre trouverOffre(String idOffre) {
        for (Offre offre : offres) {
            // Offre utilise UUID, donc on compare les strings
            if (offre.getId().toString().equals(idOffre)) {
                return offre;
            }
        }
        return null;
    }

    /**
     * Trouve un candidat par son ID (int).
     */
    private Candidat trouverCandidat(String idCandidat) {
        try {
            // Convertir string en int (Candidat utilise int comme ID)
            int id = Integer.parseInt(idCandidat);
            
            for (Candidat candidat : candidats) {
                if (candidat.getId() == id) {
                    return candidat;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("ID candidat invalide: " + idCandidat);
        }
        
        return null;
    }
}