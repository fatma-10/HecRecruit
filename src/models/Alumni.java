package models;

import java.util.List;

/**
 * Classe représentant un alumni (ancien diplômé) dans le système.
 * Un alumni est un type spécifique de candidat qui a déjà obtenu son diplôme
 * et qui peut être actuellement en poste dans une entreprise.
 * 
 * Cette classe hérite de la classe Candidat et ajoute des informations
 * spécifiques aux alumni : année d'obtention du diplôme, poste actuel et entreprise actuelle.
 */
public class Alumni extends Candidat {

    // ----------------------------- Attributs -----------------------------
    
    /** Année d'obtention du diplôme (année de graduation) */
    private int anneeDiplome;
    
    /** Intitulé du poste actuellement occupé par l'alumni (peut être vide) */
    private String posteActuel;
    
    /** Nom de l'entreprise où l'alumni travaille actuellement (peut être vide) */
    private String entrepriseActuelle;
    
    // ----------------------------- Constructeur -----------------------------
    
    /**
     * Construit un nouvel alumni avec validation complète des données.
     * 
     * Ce constructeur appelle d'abord le constructeur parent (Candidat) pour initialiser
     * les informations de base, puis valide et initialise les attributs spécifiques
     * à un alumni.
     * 
     * Validations effectuées :
     * - Toutes les validations de la classe Candidat (CIN, email, etc.)
     * - Vérification que l'année de diplôme est valide (supérieure à 0)
     * 
     * Note : Le poste et l'entreprise actuels sont optionnels et peuvent être vides
     * 
     * @param id Numéro CIN de l'alumni (8 chiffres)
     * @param nom Nom de famille de l'alumni
     * @param prenom Prénom de l'alumni
     * @param email Adresse email de l'alumni
     * @param telephone Numéro de téléphone de l'alumni
     * @param mdp Mot de passe de l'alumni
     * @param anneeDiplome Année d'obtention du diplôme (doit être > 0)
     * @param posteActuel Intitulé du poste actuel (peut être null ou vide)
     * @param entrepriseActuelle Nom de l'entreprise actuelle (peut être null ou vide)
     * @param tousLesCandidats Liste de tous les candidats pour vérifier l'unicité
     * @throws IllegalArgumentException si l'année de diplôme est invalide
     */
    public Alumni(int id, String nom, String prenom, String email, String telephone, 
                 String mdp, int anneeDiplome, String posteActuel, String entrepriseActuelle,
                 List<Candidat> tousLesCandidats) {
        // Appel au constructeur parent pour initialiser les informations de base
        super(id, nom, prenom, email, telephone, mdp, tousLesCandidats);
        
        // Validation de l'année de diplôme
        if (anneeDiplome <= 0) {
            throw new IllegalArgumentException("L'année de diplôme est invalide");
        }

        // Initialisation des attributs spécifiques
        this.anneeDiplome = anneeDiplome;
        
        // Gestion des valeurs null pour le poste (conversion en chaîne vide si null)
        this.posteActuel = (posteActuel == null) ? "" : posteActuel.trim();
        
        // Gestion des valeurs null pour l'entreprise (conversion en chaîne vide si null)
        this.entrepriseActuelle = (entrepriseActuelle == null) ? "" : entrepriseActuelle.trim();
    }

    // ----------------------------- Getters & Setters -----------------------------
    
    /**
     * Retourne l'année d'obtention du diplôme de l'alumni.
     * 
     * @return L'année de graduation
     */
    public int getAnneeDiplome() {
        return anneeDiplome;
    }

    /**
     * Modifie l'année d'obtention du diplôme.
     * 
     * @param anneeDiplome La nouvelle année de diplôme
     */
    public void setAnneeDiplome(int anneeDiplome) {
        this.anneeDiplome = anneeDiplome;
    }

    /**
     * Retourne le poste actuellement occupé par l'alumni.
     * 
     * @return L'intitulé du poste actuel (peut être une chaîne vide)
     */
    public String getPosteActuel() {
        return posteActuel;
    }

    /**
     * Modifie le poste actuel de l'alumni.
     * 
     * @param posteActuel Le nouveau poste
     */
    public void setPosteActuel(String posteActuel) {
        this.posteActuel = posteActuel;
    }

    /**
     * Retourne le nom de l'entreprise où l'alumni travaille actuellement.
     * 
     * @return Le nom de l'entreprise actuelle (peut être une chaîne vide)
     */
    public String getEntrepriseActuelle() {
        return entrepriseActuelle;
    }

    /**
     * Modifie l'entreprise actuelle de l'alumni.
     * 
     * @param entrepriseActuelle La nouvelle entreprise
     */
    public void setEntrepriseActuelle(String entrepriseActuelle) {
        this.entrepriseActuelle = entrepriseActuelle;
    }
    
    // ----------------------------- Méthodes -----------------------------
    
    /**
     * Retourne un tableau contenant les informations principales de l'alumni.
     * Cette méthode redéfinit celle de la classe parent pour inclure les informations
     * spécifiques à un alumni.
     * 
     * Si le poste ou l'entreprise actuelle sont vides, la méthode affiche "Non spécifié(e)"
     * pour améliorer la lisibilité dans les interfaces utilisateur.
     * 
     * @return Un tableau de String contenant : [ID, nom, prénom, email, téléphone,
     *         année de diplôme, poste actuel, entreprise actuelle]
     */
    @Override
    public String[] getInfosPrincipales() {
        return new String[] {
            String.valueOf(getId()),  // Conversion de l'ID en String
            getNom(),
            getPrenom(),
            getEmail(),
            getTelephone(),
            String.valueOf(anneeDiplome),
            // Affichage "Non spécifié" si le poste est vide
            (posteActuel == null || posteActuel.isEmpty()) ? "Non spécifié" : posteActuel,
            // Affichage "Non spécifiée" si l'entreprise est vide
            (entrepriseActuelle == null || entrepriseActuelle.isEmpty()) ? "Non spécifiée" : entrepriseActuelle
        };
    }
}