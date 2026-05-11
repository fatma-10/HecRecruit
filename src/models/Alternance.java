package models;

/**
 * Classe représentant une offre d'alternance dans le système.
 * 
 * Une alternance est un type d'offre spécialisée caractérisée par :
 * - Un rythme d'alternance (ex: 3 jours/2 jours, 1 semaine/1 semaine)
 * - Une durée déterminée en mois
 * 
 * L'alternance permet aux étudiants de combiner formation théorique en établissement
 * et expérience pratique en entreprise selon un rythme défini.
 * 
 * Cette classe hérite de OffreSpecialisee et implémente les méthodes abstraites
 * requises pour fournir les informations spécifiques aux alternances.
 */
public class Alternance extends OffreSpecialisee {

    // ----------------------------- Attributs -----------------------------
    
    /** 
     * Rythme de l'alternance (ex: "3 jours/2 jours", "1 semaine/1 semaine")
     * Indique le temps passé en entreprise versus le temps en formation
     */
    private String rythme;
    
    /** Durée totale de l'alternance en mois (doit être > 0) */
    private int dureeEnMois;
    
    // ----------------------------- Constructeur -----------------------------
    
    /**
     * Construit une nouvelle offre d'alternance avec validation complète des données.
     * 
     * Ce constructeur effectue les validations suivantes :
     * - Toutes les validations de la classe parent (OffreSpecialisee/Offre)
     * - Validation que la durée est strictement positive
     * 
     * @param titre Titre de l'alternance (ex: "Alternance Développeur Full-Stack")
     * @param description Description détaillée de l'alternance
     * @param entreprise Entreprise proposant l'alternance
     * @param rythme Rythme de l'alternance (ex: "3 jours/2 jours")
     * @param dureeEnMois Durée de l'alternance en mois (doit être > 0)
     * @throws IllegalArgumentException si la durée est négative ou nulle
     */
    public Alternance(String titre, String description, Entreprise entreprise, String rythme, int dureeEnMois) {
        // Appel au constructeur parent avec le type "alternance"
        super(titre, description, "alternance", entreprise);
        
        // Validation de la durée de l'alternance
        if (dureeEnMois <= 0) {
            throw new IllegalArgumentException("La durée doit être supérieure à 0");
        }
        
        // Initialisation des attributs spécifiques à l'alternance
        this.rythme = rythme;
        this.dureeEnMois = dureeEnMois;
    }

    // ----------------------------- Getters & Setters -----------------------------
    
    /**
     * Retourne le rythme de l'alternance.
     * 
     * @return Le rythme (ex: "3 jours/2 jours", "1 semaine/1 semaine")
     */
    public String getRythme() {
        return rythme;
    }

    /**
     * Modifie le rythme de l'alternance.
     * 
     * @param rythme Le nouveau rythme
     */
    public void setRythme(String rythme) {
        this.rythme = rythme;
    }

    /**
     * Retourne la durée de l'alternance en mois.
     * 
     * @return La durée en mois
     */
    public int getDureeEnMois() {
        return dureeEnMois;
    }

    /**
     * Modifie la durée de l'alternance.
     * 
     * @param dureeEnMois La nouvelle durée en mois
     */
    public void setDureeEnMois(int dureeEnMois) {
        this.dureeEnMois = dureeEnMois;
    }
    
    // ----------------------------- Méthodes -----------------------------
    
    /**
     * Retourne un tableau contenant les informations principales de l'alternance.
     * Cette méthode implémente la méthode abstraite de OffreSpecialisee.
     * 
     * Les informations retournées incluent :
     * - L'ID de l'offre
     * - Le titre
     * - Le type ("Alternance")
     * - Le rythme de l'alternance
     * - La durée en mois (formatée avec " mois")
     * - La date de publication
     * - La date d'expiration (ou "Non définie")
     * - Le nom de l'entreprise
     * - Le nombre de candidatures reçues
     * 
     * @return Un tableau de String avec toutes les informations de l'alternance
     */
    @Override
    public String[] getInfosPrincipales() {
        return new String[] {
            getId().toString(),
            getTitre(),
            "Alternance",
            rythme,
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
     * @return "Alternance"
     */
    @Override
    public String getTypeOffre() {
        return "Alternance";
    }
}