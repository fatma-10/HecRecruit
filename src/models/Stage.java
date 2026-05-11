package models;

/**
 * Classe représentant une offre de stage dans le système.
 * 
 * Un stage est un type d'offre spécialisée caractérisée par :
 * - Une durée déterminée en mois
 * - Un domaine d'activité spécifique
 * 
 * Cette classe hérite de OffreSpecialisee et implémente les méthodes abstraites
 * requises pour fournir les informations spécifiques aux stages.
 */
public class Stage extends OffreSpecialisee {
    
    // ----------------------------- Attributs privés -----------------------------
    
    /** Durée du stage en mois (doit être > 0) */
    private int dureeEnMois;
    
    /** Domaine d'activité du stage (ex: Développement Web, Marketing, Finance) */
    private String domaine;
    
    // ----------------------------- Constructeur -----------------------------
    
    /**
     * Construit une nouvelle offre de stage avec validation complète des données.
     * 
     * Ce constructeur effectue les validations suivantes :
     * - Toutes les validations de la classe parent (OffreSpecialisee/Offre)
     * - Validation que la durée est strictement positive
     * 
     * @param titre Titre du stage (ex: "Stage Développeur Java")
     * @param description Description détaillée du stage
     * @param entreprise Entreprise proposant le stage
     * @param dureeEnMois Durée du stage en mois (doit être > 0)
     * @param domaine Domaine d'activité du stage
     * @throws IllegalArgumentException si la durée est négative ou nulle
     */
    public Stage(String titre, String description, Entreprise entreprise, int dureeEnMois, String domaine) {
        // Appel au constructeur parent avec le type "stage"
        super(titre, description, "stage", entreprise);
        
        // Validation de la durée du stage
        if (dureeEnMois <= 0) {
            throw new IllegalArgumentException("La durée doit être supérieure à 0");
        }
        
        // Initialisation des attributs spécifiques au stage
        this.dureeEnMois = dureeEnMois;
        this.domaine = domaine;
    }
    
    // ----------------------------- Getters & Setters -----------------------------
    
    /**
     * Retourne la durée du stage en mois.
     * 
     * @return La durée en mois
     */
    public int getDureeEnMois() {
        return dureeEnMois;
    }

    /**
     * Modifie la durée du stage.
     * 
     * @param dureeEnMois La nouvelle durée en mois
     */
    public void setDureeEnMois(int dureeEnMois) {
        this.dureeEnMois = dureeEnMois;
    }

    /**
     * Retourne le domaine d'activité du stage.
     * 
     * @return Le domaine du stage
     */
    public String getDomaine() {
        return domaine;
    }

    /**
     * Modifie le domaine du stage.
     * 
     * @param domaine Le nouveau domaine
     */
    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }
    
    // ----------------------------- Méthodes -----------------------------
    
    /**
     * Retourne un tableau contenant les informations principales du stage.
     * Cette méthode implémente la méthode abstraite de OffreSpecialisee.
     * 
     * Les informations retournées incluent :
     * - L'ID de l'offre
     * - Le titre
     * - Le type ("Stage")
     * - Le domaine d'activité
     * - La durée en mois (formatée avec " mois")
     * - La date de publication
     * - La date d'expiration (ou "Non définie")
     * - Le nom de l'entreprise
     * - Le nombre de candidatures reçues
     * 
     * @return Un tableau de String avec toutes les informations du stage
     */
    @Override
    public String[] getInfosPrincipales() {
        return new String[] {
            getId().toString(),
            getTitre(),
            "Stage",
            domaine,
            dureeEnMois + " mois",
            getDatePublication().toString(),
            (getDateExpiration() != null ? getDateExpiration().toString() : "Non définie"),
            getEntreprise().getNom(),
            String.valueOf(getCandidatures().size())
        };
    }
    
    /**
     * Retourne le type de l'offre sous forme de chaîne lisible.
     * Cette méthode implémente la méthode abstraite de OffreSpecialisee.
     * 
     * @return "Stage"
     */
    @Override
    public String getTypeOffre() {
        return "Stage";
    }
}