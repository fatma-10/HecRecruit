package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe représentant une offre générique dans le système de gestion des opportunités.
 * 
 * Une offre peut être un stage, une alternance ou un projet de fin d'études.
 * Cette classe sert de classe parent pour les classes spécialisées (Stage, Alternance, ProjetFinEtudes).
 * 
 * Chaque offre est identifiée par un UUID unique, possède des dates de publication et d'expiration,
 * est liée à une entreprise et peut recevoir plusieurs candidatures.
 */
public class Offre {

    // ----------------------------- Attributs privés -----------------------------
    
    /** Identifiant unique de l'offre (généré automatiquement) */
    private UUID id;
    
    /** Titre de l'offre */
    private String titre;
    
    /** Description détaillée de l'offre */
    private String description;
    
    /** Type de l'offre (stage, alternance, projet fin d'études) */
    private String typeOffre;
    
    /** Date de publication de l'offre (générée automatiquement à la création) */
    private LocalDate datePublication;
    
    /** Date d'expiration de l'offre (peut être null si non définie) */
    private LocalDate dateExpiration;
    
    /** Entreprise qui a publié l'offre */
    private Entreprise entreprise;
    
    /** Liste des candidats ayant postulé à cette offre */
    private List<Candidat> candidatures;
    
    // ----------------------------- Constructeur -----------------------------
    
    /**
     * Construit une nouvelle offre avec validation complète des données.
     * 
     * Ce constructeur effectue les validations suivantes :
     * - Vérification de la présence de tous les champs obligatoires
     * - Génération automatique d'un UUID unique
     * - Initialisation de la date de publication à la date actuelle
     * - Initialisation d'une liste vide pour les candidatures
     * 
     * @param titre Titre de l'offre (obligatoire, ne peut pas être vide)
     * @param description Description détaillée (obligatoire, ne peut pas être vide)
     * @param typeOffre Type de l'offre (obligatoire, ne peut pas être vide)
     * @param entreprise Entreprise publiant l'offre (obligatoire, ne peut pas être null)
     * @throws IllegalArgumentException si une validation échoue
     */
    public Offre(String titre, String description, String typeOffre, Entreprise entreprise) {
        // Validation du titre
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        
        // Validation de la description
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("La description est obligatoire");
        }
        
        // Validation du type d'offre
        if (typeOffre == null || typeOffre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type d'offre est obligatoire");
        }
        
        // Validation de l'entreprise
        if (entreprise == null) {
            throw new IllegalArgumentException("L'entreprise est obligatoire");
        }
        
        // Génération d'un UUID unique pour l'offre
        this.id = UUID.randomUUID();
        
        // Initialisation des attributs
        this.titre = titre.trim();
        this.description = description.trim();
        this.typeOffre = typeOffre.trim();
        
        // La date de publication est définie à la date actuelle
        this.datePublication = LocalDate.now();
        
        // La date d'expiration est initialement non définie
        this.dateExpiration = null;
        
        // Association avec l'entreprise
        this.entreprise = entreprise;
        
        // Initialisation d'une liste vide pour les candidatures
        this.candidatures = new ArrayList<>();
    }

    // ----------------------------- Getters & Setters -----------------------------
    
    /**
     * Retourne l'identifiant unique de l'offre.
     * 
     * @return L'UUID de l'offre
     */
    public UUID getId() {
        return id;
    }

    /**
     * Modifie l'identifiant de l'offre.
     * Note : Cette méthode devrait être utilisée avec prudence car l'UUID devrait être immuable.
     * 
     * @param id Le nouvel identifiant UUID
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Retourne le titre de l'offre.
     * 
     * @return Le titre
     */
    public String getTitre() {
        return titre;
    }

    /**
     * Modifie le titre de l'offre.
     * 
     * @param titre Le nouveau titre
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     * Retourne la description de l'offre.
     * 
     * @return La description détaillée
     */
    public String getDescription() {
        return description;
    }

    /**
     * Modifie la description de l'offre.
     * 
     * @param description La nouvelle description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retourne le type de l'offre.
     * 
     * @return Le type d'offre (stage, alternance, projet fin d'études)
     */
    public String getTypeOffre() {
        return typeOffre;
    }

    /**
     * Modifie le type de l'offre.
     * 
     * @param typeOffre Le nouveau type
     */
    public void setTypeOffre(String typeOffre) {
        this.typeOffre = typeOffre;
    }
    
    /**
     * Retourne la date de publication de l'offre.
     * 
     * @return La date de publication
     */
    public LocalDate getDatePublication() {
        return datePublication;
    }

    /**
     * Modifie la date de publication de l'offre.
     * 
     * @param datePublication La nouvelle date de publication
     */
    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }

    /**
     * Retourne la date d'expiration de l'offre.
     * 
     * @return La date d'expiration (peut être null si non définie)
     */
    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    /**
     * Modifie la date d'expiration de l'offre.
     * 
     * @param dateExpiration La nouvelle date d'expiration
     */
    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    /**
     * Retourne l'entreprise qui a publié l'offre.
     * 
     * @return L'entreprise associée
     */
    public Entreprise getEntreprise() {
        return entreprise;
    }

    /**
     * Modifie l'entreprise associée à l'offre.
     * 
     * @param entreprise La nouvelle entreprise
     */
    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    /**
     * Retourne la liste des candidats ayant postulé à cette offre.
     * 
     * @return La liste des candidatures
     */
    public List<Candidat> getCandidatures() {
        return candidatures;
    }

    /**
     * Remplace la liste des candidatures.
     * 
     * @param candidatures La nouvelle liste de candidatures
     */
    public void setCandidatures(List<Candidat> candidatures) {
        this.candidatures = candidatures;
    }
    
    // ----------------------------- Méthodes -----------------------------
    
    /**
     * Retourne un tableau contenant les informations principales de l'offre.
     * Cette méthode est utilisée pour l'affichage dans les interfaces utilisateur.
     * 
     * @return Un tableau de String contenant : [ID, titre, type, date publication,
     *         date expiration, nom entreprise, nombre de candidatures]
     */
    public String[] getInfosPrincipales() {
        return new String[] {
            id.toString(),
            titre,
            typeOffre,
            datePublication.toString(),
            (dateExpiration != null) ? dateExpiration.toString() : "Non définie",
            entreprise.getNom(),
            String.valueOf(candidatures.size())
        };
    }
    
    /**
     * Ajoute une candidature à l'offre si les conditions sont respectées.
     * 
     * Cette méthode vérifie que :
     * - Le candidat n'est pas null
     * - L'offre n'est pas expirée
     * - Le candidat n'a pas déjà postulé
     * 
     * @param candidat Le candidat qui postule
     * @return true si la candidature a été ajoutée avec succès, false sinon
     */
    public boolean ajouterCandidature(Candidat candidat) {
        // Vérification que le candidat n'est pas null
        if (candidat == null) {
            return false;
        }
        
        // Vérification que l'offre n'est pas expirée
        if (estExpiree()) {
            return false;
        }
        
        // Vérification que le candidat n'a pas déjà postulé
        if (!candidatures.contains(candidat)) {
            candidatures.add(candidat);
            return true;
        }
        
        // Le candidat a déjà postulé
        return false;
    }
    
    /**
     * Vérifie si l'offre est expirée.
     * Une offre est considérée comme expirée si sa date d'expiration est dépassée.
     * 
     * @return true si l'offre est expirée, false sinon (ou si la date d'expiration n'est pas définie)
     */
    public boolean estExpiree() {
        // Si la date d'expiration n'est pas définie, l'offre n'est pas expirée
        if (dateExpiration == null) {
            return false;
        }
        
        // Vérification si la date actuelle est après la date d'expiration
        return LocalDate.now().isAfter(dateExpiration);
    }
    
    /**
     * Vérifie si un candidat a déjà postulé à cette offre.
     * 
     * @param candidat Le candidat à vérifier
     * @return true si le candidat a déjà postulé, false sinon
     */
    public boolean candidatAPostule(Candidat candidat) {
        // Vérification que le candidat n'est pas null
        if (candidat == null) {
            return false;
        }
        
        // Vérification de la présence du candidat dans la liste
        return candidatures.contains(candidat);
    }
    
    /**
     * Retourne le nombre de candidatures reçues pour cette offre.
     * 
     * @return Le nombre de candidats ayant postulé
     */
    public int getNombreCandidatures() {
        return candidatures.size();
    }

    /**
     * Compare cette offre avec un autre objet pour déterminer l'égalité.
     * Deux offres sont considérées égales si elles ont le même UUID.
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
        Offre offre = (Offre) obj;
        return id.equals(offre.id);
    }

    /**
     * Calcule le code de hachage de l'offre basé sur son UUID.
     * 
     * @return Le code de hachage de l'offre
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}