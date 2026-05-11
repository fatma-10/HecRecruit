package models;

import java.util.List;

/**
 * Classe représentant un étudiant dans le système.
 * Un étudiant est un type spécifique de candidat qui est actuellement inscrit
 * dans un établissement d'enseignement supérieur.
 * 
 * Cette classe hérite de la classe Candidat et ajoute des informations
 * spécifiques aux étudiants : niveau d'études, filière et établissement.
 */
public class Etudiant extends Candidat {

    // ----------------------------- Attributs -----------------------------
    
    /** Niveau d'études de l'étudiant (ex: Licence 1, Master 2, etc.) */
    private String niveau;
    
    /** Filière ou spécialité de l'étudiant (ex: Informatique, Génie Civil, etc.) */
    private String filiere;
    
    /** Nom de l'établissement d'enseignement où l'étudiant est inscrit */
    private String etablissement;
    
    // ----------------------------- Constructeur -----------------------------
    
    /**
     * Construit un nouvel étudiant avec validation complète des données.
     * 
     * Ce constructeur appelle d'abord le constructeur parent (Candidat) pour initialiser
     * les informations de base, puis valide et initialise les attributs spécifiques
     * à un étudiant.
     * 
     * Validations effectuées :
     * - Toutes les validations de la classe Candidat (CIN, email, etc.)
     * - Vérification que le niveau, la filière et l'établissement ne sont pas vides
     * 
     * @param id Numéro CIN de l'étudiant (8 chiffres)
     * @param nom Nom de famille de l'étudiant
     * @param prenom Prénom de l'étudiant
     * @param email Adresse email de l'étudiant
     * @param telephone Numéro de téléphone de l'étudiant
     * @param mdp Mot de passe de l'étudiant
     * @param niveau Niveau d'études (ne peut pas être vide)
     * @param filiere Filière d'études (ne peut pas être vide)
     * @param etablissement Établissement d'enseignement (ne peut pas être vide)
     * @param tousLesCandidats Liste de tous les candidats pour vérifier l'unicité
     * @throws IllegalArgumentException si une validation échoue
     */
    public Etudiant(int id, String nom, String prenom, String email, String telephone, 
                   String mdp, String niveau, String filiere, String etablissement,
                   List<Candidat> tousLesCandidats) {
        // Appel au constructeur parent pour initialiser les informations de base
        super(id, nom, prenom, email, telephone, mdp, tousLesCandidats);
        
        // Validation du niveau d'études
        if (niveau == null || niveau.trim().isEmpty()) {
            throw new IllegalArgumentException("Le niveau est obligatoire");
        }
        
        // Validation de la filière
        if (filiere == null || filiere.trim().isEmpty()) {
            throw new IllegalArgumentException("La filière est obligatoire");
        }
        
        // Validation de l'établissement
        if (etablissement == null || etablissement.trim().isEmpty()) {
            throw new IllegalArgumentException("L'établissement est obligatoire");
        }

        // Initialisation des attributs spécifiques après validation
        this.niveau = niveau.trim();
        this.filiere = filiere.trim();
        this.etablissement = etablissement.trim();
    }

    // ----------------------------- Getters & Setters -----------------------------
    
    /**
     * Retourne le niveau d'études de l'étudiant.
     * 
     * @return Le niveau d'études (ex: Licence 1, Master 2)
     */
    public String getNiveau() {
        return niveau;
    }

    /**
     * Modifie le niveau d'études de l'étudiant.
     * 
     * @param niveau Le nouveau niveau d'études
     */
    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    /**
     * Retourne la filière de l'étudiant.
     * 
     * @return La filière (ex: Informatique, Génie Civil)
     */
    public String getFiliere() {
        return filiere;
    }

    /**
     * Modifie la filière de l'étudiant.
     * 
     * @param filiere La nouvelle filière
     */
    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    /**
     * Retourne l'établissement où l'étudiant est inscrit.
     * 
     * @return Le nom de l'établissement
     */
    public String getEtablissement() {
        return etablissement;
    }

    /**
     * Modifie l'établissement de l'étudiant.
     * 
     * @param etablissement Le nouvel établissement
     */
    public void setEtablissement(String etablissement) {
        this.etablissement = etablissement;
    }
    
    // ----------------------------- Méthodes -----------------------------

    /**
     * Retourne un tableau contenant les informations principales de l'étudiant.
     * Cette méthode redéfinit celle de la classe parent pour inclure les informations
     * spécifiques à un étudiant.
     * 
     * @return Un tableau de String contenant : [ID, nom, prénom, niveau, filière, 
     *         établissement, email, téléphone]
     */
    @Override
    public String[] getInfosPrincipales() {
        return new String[] {
            String.valueOf(getId()),  // Conversion de l'ID en String
            getNom(),
            getPrenom(),
            niveau,
            filiere,
            etablissement,
            getEmail(),
            getTelephone()
        };
    }
}