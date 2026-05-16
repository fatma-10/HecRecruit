package com.IHEC.Recruit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "offre_specialisee")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class OffreSpecialisee extends Offre {

    public OffreSpecialisee() {}

    public OffreSpecialisee(String titre, String description, String typeOffre, Entreprise entreprise) {
        super(titre, description, typeOffre, entreprise);
    }

    protected void validerDuree(int duree) {
        if (duree <= 0)
            throw new IllegalArgumentException("La durée doit être supérieure à 0");
    }
}