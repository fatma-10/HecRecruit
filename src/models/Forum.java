package models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe représentant un message publié sur le forum du système.
 * 
 * Le forum permet aux étudiants et aux entreprises de communiquer et d'échanger
 * des informations. Chaque message est horodaté et identifie clairement l'auteur
 * et son type (étudiant ou entreprise).
 * 
 * Les messages du forum sont identifiés par un UUID unique et contiennent
 * toutes les informations nécessaires pour leur affichage et leur gestion.
 */
public class Forum {
    
    // ----------------------------- Attributs -----------------------------
    
    /** Identifiant unique du message (généré automatiquement) */
    private UUID id;
    
    /** Nom complet de l'auteur du message */
    private String auteur;
    
    /** Adresse email de l'auteur pour identification */
    private String emailAuteur;
    
    /** Contenu textuel du message */
    private String message;
    
    /** Date et heure de publication du message (générée automatiquement) */
    private LocalDateTime datePublication;
    
    /** 
     * Type d'auteur du message
     * true = étudiant ou alumni
     * false = entreprise
     */
    private boolean estEtudiant;

    // ----------------------------- Constructeur -----------------------------
    
    /**
     * Construit un nouveau message de forum.
     * 
     * Ce constructeur génère automatiquement :
     * - Un UUID unique pour identifier le message
     * - La date et l'heure actuelles comme date de publication
     * 
     * Aucune validation n'est effectuée sur les paramètres, mais il est recommandé
     * de s'assurer que l'auteur, l'email et le message ne sont pas vides avant
     * de créer une instance.
     * 
     * @param auteur Nom complet de l'auteur du message
     * @param emailAuteur Email de l'auteur
     * @param message Contenu du message
     * @param estEtudiant true si l'auteur est un étudiant/alumni, false si c'est une entreprise
     */
    public Forum(String auteur, String emailAuteur, String message, boolean estEtudiant) {
        // Génération d'un UUID unique pour le message
        this.id = UUID.randomUUID();
        
        // Initialisation des informations de l'auteur
        this.auteur = auteur;
        this.emailAuteur = emailAuteur;
        
        // Initialisation du contenu
        this.message = message;
        
        // Enregistrement de la date et heure actuelles
        this.datePublication = LocalDateTime.now();
        
        // Définition du type d'auteur
        this.estEtudiant = estEtudiant;
    }

    // ----------------------------- Getters -----------------------------
    
    /**
     * Retourne l'identifiant unique du message.
     * 
     * @return L'UUID du message
     */
    public UUID getId() { 
        return id; 
    }
    
    /**
     * Retourne le nom de l'auteur du message.
     * 
     * @return Le nom complet de l'auteur
     */
    public String getAuteur() { 
        return auteur; 
    }
    
    /**
     * Retourne l'email de l'auteur du message.
     * 
     * @return L'adresse email de l'auteur
     */
    public String getEmailAuteur() { 
        return emailAuteur; 
    }
    
    /**
     * Retourne le contenu du message.
     * 
     * @return Le texte du message
     */
    public String getMessage() { 
        return message; 
    }
    
    /**
     * Retourne la date et l'heure de publication du message.
     * 
     * @return La date et heure de publication
     */
    public LocalDateTime getDatePublication() { 
        return datePublication; 
    }
    
    /**
     * Indique si l'auteur est un étudiant/alumni.
     * 
     * @return true si l'auteur est un étudiant/alumni, false si c'est une entreprise
     */
    public boolean isEstEtudiant() { 
        return estEtudiant; 
    }

    // ----------------------------- Méthodes -----------------------------

    /**
     * Retourne un tableau contenant les informations principales du message du forum.
     * Cette méthode est utilisée pour l'affichage dans les interfaces utilisateur.
     * 
     * Le type d'auteur est converti en chaîne lisible :
     * - "Étudiant" si estEtudiant est true
     * - "Entreprise" si estEtudiant est false
     * 
     * @return Un tableau de String contenant : [ID, auteur, email, message, 
     *         date de publication, type d'auteur]
     */
    public String[] getInfosPrincipales() {
        // Détermination du type d'auteur en chaîne lisible
        String typeAuteur = estEtudiant ? "Étudiant" : "Entreprise";
        
        // Construction et retour du tableau d'informations
        return new String[] {
            id.toString(),
            auteur,
            emailAuteur,
            message,
            datePublication.toString(),
            typeAuteur
        };
    }
}