package com.IHEC.Recruit.models;

import jakarta.persistence.Entity;

/**
 * Classe abstraite intermediaire pour les offres specialisees
 * (Stage, Alternance, ProjetFinEtudes).
 *
 * BUG FIX: l'ancienne version dupliquait @Inheritance et @Table.
 * Dans une hierarchie JOINED, seule la racine (Offre) porte @Inheritance.
 * Les sous-classes utilisent leur propre @Table.
 */
@Entity
public abstract class OffreSpecialisee extends Offre {

    protected OffreSpecialisee() {}

    protected OffreSpecialisee(String titre, String description, String typeOffre, Entreprise entreprise) {
        super(titre, description, typeOffre, entreprise);
    }
}
