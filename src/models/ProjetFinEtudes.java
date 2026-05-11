package models;

/**
 * Classe représentant une offre de Projet de Fin d'Études (PFE) dans le système.
 * 
 * Un Projet de Fin d'Études est un type d'offre spécialisée caractérisée par :
 * - Un sujet de recherche ou de développement spécifique
 * - Les technologies ou compétences requises pour le projet
 * 
 * Le PFE permet aux étudiants de clôturer leur cursus académique par un projet
 * concret réalisé en collaboration avec une entreprise.
 * 
 * Cette classe hérite de OffreSpecialisee et implémente les méthodes abstraites
 * requises pour fournir les informations spécifiques aux projets de fin d'études.
 */
public class ProjetFinEtudes extends OffreSpecialisee {

    // ----------------------------- Attributs -----------------------------
    
    /** 
     * Sujet du projet de fin d'études
     * Décrit le thème principal ou la problématique à traiter
     */
    private String sujet;
    
    /** 
     * Technologies requises ou utilisées pour le projet
     * Ex: "Java, Spring Boot, React, PostgreSQL"
     */
    private String technologies;

    // ----------------------------- Constructeur -----------------------------
    
    /**
     * Construit une nouvelle offre de Projet de Fin d'Études.
     * 
     * Ce constructeur initialise tous les attributs en appelant le constructeur parent
     * et en définissant les attributs spécifiques au PFE.
     * 
     * Aucune validation supplémentaire n'est effectuée au-delà de celles du parent,
     * car le sujet et les technologies peuvent être optionnels ou définis ultérieurement.
     * 
     * @param titre Titre du projet (ex: "PFE - Développement d'une plateforme de gestion")
     * @param description Description détaillée du projet et de ses objectifs
     * @param entreprise Entreprise proposant le projet
     * @param sujet Sujet ou thématique du projet
     * @param technologies Technologies requises pour réaliser le projet
     */
    public ProjetFinEtudes(String titre, String description, Entreprise entreprise,
                           String sujet, String technologies) {
        // Appel au constructeur parent avec le type "projet fin d'etudes"
        super(titre, description, "projet fin d'etudes", entreprise);
        
        // Initialisation des attributs spécifiques au PFE
        this.sujet = sujet;
        this.technologies = technologies;
    }

    // ----------------------------- Getters & Setters -----------------------------
    
    /**
     * Retourne le sujet du projet de fin d'études.
     * 
     * @return Le sujet du projet
     */
    public String getSujet() {
        return sujet;
    }

    /**
     * Modifie le sujet du projet.
     * 
     * @param sujet Le nouveau sujet
     */
    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    /**
     * Retourne les technologies requises pour le projet.
     * 
     * @return Les technologies (généralement une liste séparée par des virgules)
     */
    public String getTechnologies() {
        return technologies;
    }

    /**
     * Modifie les technologies du projet.
     * 
     * @param technologies Les nouvelles technologies
     */
    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    // ----------------------------- Méthodes -----------------------------
    
    /**
     * Retourne un tableau contenant les informations principales du projet de fin d'études.
     * Cette méthode implémente la méthode abstraite de OffreSpecialisee.
     * 
     * Les informations retournées incluent :
     * - L'ID de l'offre
     * - Le titre
     * - Le type ("Projet Fin d'Etudes")
     * - Le sujet du projet
     * - Les technologies requises
     * - La date de publication
     * - La date d'expiration (ou "Non définie")
     * - Le nom de l'entreprise
     * - Le nombre de candidatures reçues
     * 
     * @return Un tableau de String avec toutes les informations du PFE
     */
    @Override
    public String[] getInfosPrincipales() {
        return new String[] {
            getId().toString(),
            getTitre(),
            "Projet Fin d'Etudes",
            sujet,
            technologies,
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
     * @return "Projet Fin d'Etudes"
     */
    @Override
    public String getTypeOffre() {
        return "Projet Fin d'Etudes";
    }
}