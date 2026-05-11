package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe représentant une entreprise dans le système de gestion des offres.
 * 
 * Une entreprise peut publier différents types d'offres (stages, alternances, projets)
 * et maintenir une wishlist de candidats qu'elle souhaite suivre.
 * 
 * Chaque entreprise est identifiée de manière unique par un UUID généré automatiquement.
 */
public class Entreprise {
    
    // ----------------------------- Attributs privés -----------------------------
    
    /** Identifiant unique de l'entreprise (généré automatiquement) */
    private UUID id;
    
    /** Nom de l'entreprise */
    private String nom;
    
    /** Secteur d'activité de l'entreprise (ex: Technologie, Finance, etc.) */
    private String secteur;
    
    /** Adresse physique du siège ou bureau de l'entreprise */
    private String adresse;
    
    /** Adresse email de contact de l'entreprise (doit être unique) */
    private String email;
    
    /** Numéro de téléphone de l'entreprise */
    private String telephone;
    
    /** Mot de passe de l'entreprise pour l'authentification */
    private String mdp;
    
    /** Liste de toutes les offres publiées par l'entreprise */
    private List<Offre> offresPubliees;
    
    /** Liste des candidats favoris de l'entreprise (wishlist) */
    private List<Candidat> wishlist;
    
    // ----------------------------- Constructeur paramétré -----------------------------
    
    /**
     * Construit une nouvelle entreprise avec validation complète des données.
     * 
     * Ce constructeur effectue les validations suivantes :
     * - Vérification de la présence des champs obligatoires (nom, email, téléphone, mot de passe)
     * - Validation du format de l'email
     * - Génération automatique d'un UUID unique pour l'entreprise
     * 
     * Les champs secteur et adresse sont optionnels et seront initialisés à une chaîne vide
     * s'ils ne sont pas fournis.
     * 
     * @param nom Nom de l'entreprise (obligatoire, ne peut pas être vide)
     * @param secteur Secteur d'activité (optionnel)
     * @param adresse Adresse physique (optionnel)
     * @param email Adresse email (obligatoire, doit avoir un format valide)
     * @param telephone Numéro de téléphone (obligatoire)
     * @param mdp Mot de passe (obligatoire)
     * @throws IllegalArgumentException si une validation échoue
     */
    public Entreprise(String nom, String secteur, String adresse, String email, String telephone, String mdp) {
        // Validation du nom de l'entreprise
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'entreprise est obligatoire");
        }
        
        // Validation de l'email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        // Vérification du format de l'email avec une expression régulière
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
        
        // Génération d'un UUID unique pour identifier l'entreprise
        this.id = UUID.randomUUID();
        
        // Initialisation des attributs obligatoires
        this.nom = nom.trim();
        this.email = email.trim();
        this.telephone = telephone.trim();
        this.mdp = mdp.trim();
        
        // Initialisation des attributs optionnels (chaîne vide si null)
        this.secteur = (secteur != null) ? secteur.trim() : "";
        this.adresse = (adresse != null) ? adresse.trim() : "";
        
        // Initialisation des listes vides
        this.offresPubliees = new ArrayList<>();
        this.wishlist = new ArrayList<>();
    }

    // ----------------------------- Getters & Setters -----------------------------
    
    /**
     * Retourne l'identifiant unique de l'entreprise.
     * 
     * @return L'UUID de l'entreprise
     */
    public UUID getId() {
        return id;
    }

    /**
     * Modifie l'identifiant de l'entreprise.
     * Note : Cette méthode devrait être utilisée avec prudence car l'UUID devrait être immuable.
     * 
     * @param id Le nouvel identifiant UUID
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Retourne le nom de l'entreprise.
     * 
     * @return Le nom de l'entreprise
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom de l'entreprise.
     * 
     * @param nom Le nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne le secteur d'activité de l'entreprise.
     * 
     * @return Le secteur d'activité
     */
    public String getSecteur() {
        return secteur;
    }

    /**
     * Modifie le secteur d'activité de l'entreprise.
     * 
     * @param secteur Le nouveau secteur
     */
    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    /**
     * Retourne l'adresse physique de l'entreprise.
     * 
     * @return L'adresse de l'entreprise
     */
    public String getAdresse() {
        return adresse;
    }

    /**
     * Modifie l'adresse de l'entreprise.
     * 
     * @param adresse La nouvelle adresse
     */
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    /**
     * Retourne l'adresse email de l'entreprise.
     * 
     * @return L'email de contact
     */
    public String getEmail() {
        return email;
    }

    /**
     * Modifie l'email de l'entreprise.
     * 
     * @param email Le nouvel email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retourne le numéro de téléphone de l'entreprise.
     * 
     * @return Le numéro de téléphone
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Modifie le téléphone de l'entreprise.
     * 
     * @param telephone Le nouveau numéro
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Retourne le mot de passe de l'entreprise.
     * 
     * @return Le mot de passe
     */
    public String getMdp() {
        return mdp;
    }

    /**
     * Modifie le mot de passe de l'entreprise avec validation.
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
     * Retourne la liste de toutes les offres publiées par l'entreprise.
     * 
     * @return La liste des offres publiées
     */
    public List<Offre> getOffresPubliees() {
        return offresPubliees;
    }

    /**
     * Remplace la liste des offres publiées.
     * 
     * @param offresPubliees La nouvelle liste d'offres
     */
    public void setOffresPubliees(List<Offre> offresPubliees) {
        this.offresPubliees = offresPubliees;
    }

    /**
     * Retourne la liste des candidats dans la wishlist de l'entreprise.
     * 
     * @return La liste des candidats favoris
     */
    public List<Candidat> getWishlist() {
        return wishlist;
    }

    /**
     * Remplace la liste de la wishlist.
     * 
     * @param wishlist La nouvelle liste de candidats favoris
     */
    public void setWishlist(List<Candidat> wishlist) {
        this.wishlist = wishlist;
    }

    // ----------------------------- Les méthodes -----------------------------
    
    /**
     * Retourne un tableau contenant les informations principales de l'entreprise.
     * Cette méthode est utilisée pour l'affichage dans les interfaces utilisateur.
     * 
     * @return Un tableau de String contenant : [nom, secteur, adresse, email, téléphone,
     *         nombre d'offres publiées, taille de la wishlist]
     */
    public String[] getInfosPrincipales() {
        return new String[] { 
            nom, 
            secteur, 
            adresse, 
            email, 
            telephone,
            String.valueOf(offresPubliees.size()),
            String.valueOf(wishlist.size()) 
        };
    }

    /**
     * Compare cette entreprise avec un autre objet pour déterminer l'égalité.
     * Deux entreprises sont considérées égales si elles ont le même UUID.
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
        
        // Comparaison par UUID
        Entreprise entreprise = (Entreprise) obj;  
        return id.equals(entreprise.id); 
    }

    /**
     * Calcule le code de hachage de l'entreprise basé sur son UUID.
     * 
     * @return Le code de hachage de l'entreprise
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    /**
     * Vérifie si le mot de passe fourni correspond au mot de passe de l'entreprise.
     * Cette méthode est utilisée pour l'authentification.
     * 
     * @param mdp Le mot de passe à vérifier
     * @return true si le mot de passe est correct, false sinon
     */
    public boolean verifierMotDePasse(String mdp) {
        return this.mdp.equals(mdp);
    }
}