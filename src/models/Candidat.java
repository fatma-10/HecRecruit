package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un candidat (étudiant ou alumni) dans le système de gestion des offres.
 * Cette classe sert de classe parent pour les sous-classes Etudiant et Alumni.
 * 
 * Un candidat est identifié par son CIN (8 chiffres) et possède des informations personnelles
 * ainsi qu'une liste de candidatures en cours.
 */
public class Candidat {

    // ----------------------------- Attributs -----------------------------
    
    /** Identifiant unique du candidat (CIN - 8 chiffres) */
    private int id;
    
    /** Nom de famille du candidat */
    private String nom;
    
    /** Prénom du candidat */
    private String prenom;
    
    /** Adresse email du candidat (doit être unique dans le système) */
    private String email;
    
    /** Numéro de téléphone du candidat */
    private String telephone;
    
    /** Mot de passe du candidat pour l'authentification */
    private String mdp;
    
    /** Liste des offres pour lesquelles le candidat a postulé */
    private List<Offre> candidaturesEnCours;

    // ----------------------------- Constructeur -----------------------------
    
    /**
     * Construit un nouveau candidat avec validation complète des données.
     * 
     * Ce constructeur effectue les validations suivantes :
     * - Vérification de la présence et du format de tous les champs obligatoires
     * - Validation du format de l'email
     * - Validation du CIN (8 chiffres)
     * - Vérification de l'unicité du CIN et de l'email
     * 
     * @param id Numéro CIN du candidat (doit être entre 10000000 et 99999999)
     * @param nom Nom de famille (ne peut pas être vide)
     * @param prenom Prénom (ne peut pas être vide)
     * @param email Adresse email (format valide requis)
     * @param telephone Numéro de téléphone (ne peut pas être vide)
     * @param mdp Mot de passe (ne peut pas être vide)
     * @param tousLesCandidats Liste de tous les candidats existants pour vérifier l'unicité
     * @throws IllegalArgumentException si une validation échoue
     */
    public Candidat(int id, String nom, String prenom, String email, String telephone, String mdp, List<Candidat> tousLesCandidats) {
        // Validation du nom
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        
        // Validation du prénom
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        
        // Validation de l'email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
        
        // Validation du téléphone
        if (telephone == null || telephone.trim().isEmpty()) {
            throw new IllegalArgumentException("Le téléphone est obligatoire");
        }
        
        // Validation du mot de passe
        if (mdp == null || mdp.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        
        // Validation de l'ID (CIN) - doit être un nombre de 8 chiffres
        if (id < 10000000 || id > 99999999) {
            throw new IllegalArgumentException("Le CIN doit être un nombre de 8 chiffres");
        }
        
        // Vérifier que le CIN n'est pas déjà utilisé
        if (idExisteDeja(id, tousLesCandidats)) {
            throw new IllegalArgumentException("Ce CIN est déjà utilisé par un autre candidat");
        }
        
        // Vérifier que l'email n'est pas déjà utilisé
        if (emailExisteDeja(email, tousLesCandidats)) {
            throw new IllegalArgumentException("Un candidat avec cet email existe déjà");
        }
        
        // Initialisation des attributs après validation réussie
        this.id = id;
        this.nom = nom.trim();
        this.prenom = prenom.trim();
        this.email = email.trim();
        this.telephone = telephone.trim();
        this.mdp = mdp.trim();
        this.candidaturesEnCours = new ArrayList<>();
    }

    // ----------------------------- Méthodes de validation -----------------------------
    
    /**
     * Vérifie si un identifiant (CIN) existe déjà dans la liste des candidats.
     * Cette méthode est utilisée pour garantir l'unicité du CIN lors de la création d'un candidat.
     * 
     * @param id Le CIN à vérifier
     * @param tousLesCandidats La liste de tous les candidats existants
     * @return true si le CIN existe déjà, false sinon
     */
    private boolean idExisteDeja(int id, List<Candidat> tousLesCandidats) {
        // Si la liste est nulle, l'ID n'existe pas
        if (tousLesCandidats == null) {
            return false;
        }
        
        // Parcourir tous les candidats pour vérifier l'existence de l'ID
        for (Candidat c : tousLesCandidats) {
            if (c.getId() == id) {
                return true; // ID trouvé
            }
        }
        return false; // ID non trouvé
    }
    
    /**
     * Vérifie si une adresse email existe déjà dans la liste des candidats.
     * La vérification est insensible à la casse pour éviter les doublons.
     * 
     * @param email L'email à vérifier
     * @param tousLesCandidats La liste de tous les candidats existants
     * @return true si l'email existe déjà, false sinon
     */
    private boolean emailExisteDeja(String email, List<Candidat> tousLesCandidats) {
        // Si la liste est nulle, l'email n'existe pas
        if (tousLesCandidats == null) {
            return false;
        }
        
        // Parcourir tous les candidats pour vérifier l'existence de l'email
        for (Candidat c : tousLesCandidats) {
            // Comparaison insensible à la casse
            if (c.getEmail().equalsIgnoreCase(email.trim())) {
                return true; // Email trouvé
            }
        }
        return false; // Email non trouvé
    }

    // ----------------------------- Getters & Setters -----------------------------
    
    /**
     * @return L'identifiant (CIN) du candidat
     */
    public int getId() {
        return id;
    }
    
    /**
     * Modifie l'identifiant du candidat.
     * Note : L'utilisation de cette méthode doit être limitée car l'ID devrait être immuable.
     * 
     * @param id Le nouvel identifiant
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Le nom de famille du candidat
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom du candidat.
     * 
     * @param nom Le nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return Le prénom du candidat
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Modifie le prénom du candidat.
     * 
     * @param prenom Le nouveau prénom
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return L'adresse email du candidat
     */
    public String getEmail() {
        return email;
    }

    /**
     * Modifie l'email du candidat.
     * 
     * @param email Le nouvel email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Le numéro de téléphone du candidat
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Modifie le téléphone du candidat.
     * 
     * @param telephone Le nouveau numéro de téléphone
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * @return Le mot de passe du candidat
     */
    public String getMdp() {
        return mdp;
    }

    /**
     * Modifie le mot de passe du candidat avec validation.
     * 
     * @param mdp Le nouveau mot de passe (ne peut pas être vide)
     * @throws IllegalArgumentException si le mot de passe est vide
     */
    public void setMdp(String mdp) {
        if (mdp == null || mdp.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        this.mdp = mdp.trim();
    }

    /**
     * @return La liste des offres pour lesquelles le candidat a postulé
     */
    public List<Offre> getCandidaturesEnCours() {
        return candidaturesEnCours;
    }

    /**
     * Remplace la liste des candidatures en cours.
     * 
     * @param candidaturesEnCours La nouvelle liste de candidatures
     */
    public void setCandidaturesEnCours(List<Offre> candidaturesEnCours) {
        this.candidaturesEnCours = candidaturesEnCours;
    }

    // ----------------------------- Méthodes -----------------------------

    /**
     * Retourne un tableau contenant les informations principales du candidat.
     * Cette méthode est utilisée pour l'affichage dans les interfaces utilisateur.
     * 
     * @return Un tableau de String contenant : [ID, nom, prénom, email, téléphone, nombre de candidatures]
     */
    public String[] getInfosPrincipales() {
        return new String[] {
            String.valueOf(id),
            nom,
            prenom,
            email,
            telephone,
            String.valueOf(candidaturesEnCours.size())
        };
    }

    /**
     * Compare ce candidat avec un autre objet pour déterminer l'égalité.
     * Deux candidats sont considérés égaux s'ils ont le même ID (CIN).
     * 
     * @param obj L'objet à comparer
     * @return true si les objets sont égaux, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        // Vérification de référence
        if (this == obj) return true;
        
        // Vérification du type
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // Comparaison par ID
        Candidat candidat = (Candidat) obj;
        return id == candidat.id;
    }

    /**
     * Calcule le code de hachage du candidat basé sur son ID.
     * 
     * @return Le code de hachage du candidat
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}