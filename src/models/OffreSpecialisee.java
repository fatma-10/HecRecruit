package models;

/**
 * Classe abstraite représentant une offre spécialisée dans le système.
 * 
 * Cette classe hérite de la classe Offre et sert de classe parent pour les types
 * d'offres spécifiques : Stage, Alternance, et ProjetFinEtudes.
 * 
 * Elle définit un contrat que toutes les offres spécialisées doivent respecter
 * en forçant l'implémentation de certaines méthodes spécifiques.
 * 
 * Cette architecture permet de :
 * - Factoriser le code commun à toutes les offres spécialisées
 * - Garantir une implémentation cohérente des méthodes essentielles
 * - Faciliter l'extension avec de nouveaux types d'offres
 */
public abstract class OffreSpecialisee extends Offre {

    // ----------------------------- Constructeur -----------------------------
    
    /**
     * Construit une nouvelle offre spécialisée.
     * 
     * Ce constructeur appelle le constructeur parent (Offre) pour initialiser
     * les attributs de base communs à toutes les offres.
     * 
     * @param titre Titre de l'offre
     * @param description Description détaillée de l'offre
     * @param typeOffre Type spécifique de l'offre (stage, alternance, etc.)
     * @param entreprise Entreprise publiant l'offre
     * @throws IllegalArgumentException si une validation échoue au niveau parent
     */
    public OffreSpecialisee(String titre, String description, String typeOffre, Entreprise entreprise) {
        // Appel au constructeur parent pour initialiser les attributs communs
        super(titre, description, typeOffre, entreprise);
    }

    // ----------------------------- Méthodes abstraites -----------------------------
    
    /**
     * Retourne un tableau contenant les informations principales de l'offre spécialisée.
     * 
     * Cette méthode abstraite doit être implémentée par chaque classe concrète
     * pour fournir les informations spécifiques à son type d'offre.
     * 
     * Par exemple :
     * - Stage : inclut le domaine et la durée
     * - Alternance : inclut le rythme et la durée
     * - Projet Fin d'Etudes : inclut le sujet et les technologies
     * 
     * @return Un tableau de String contenant les informations spécifiques de l'offre
     */
    @Override
    public abstract String[] getInfosPrincipales();
    
    /**
     * Retourne le type spécifique de l'offre sous forme de chaîne lisible.
     * 
     * Cette méthode abstraite doit être implémentée par chaque classe concrète
     * pour retourner son type d'offre formaté pour l'affichage.
     * 
     * Exemples de valeurs de retour :
     * - "Stage"
     * - "Alternance"
     * - "Projet Fin d'Etudes"
     * 
     * @return Le type d'offre formaté
     */
    @Override
    public abstract String getTypeOffre();
    
    // ----------------------------- Méthodes communes (optionnelles) -----------------------------
    
    /**
     * Valide la durée d'une offre spécialisée.
     * 
     * Cette méthode utilitaire peut être utilisée par les classes filles
     * pour valider que la durée est positive.
     * 
     * @param duree La durée en mois à valider
     * @throws IllegalArgumentException si la durée est négative ou nulle
     */
    protected void validerDuree(int duree) {
        if (duree <= 0) {
            throw new IllegalArgumentException("La durée doit être supérieure à 0");
        }
    }
}