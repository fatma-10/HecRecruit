package com.IHEC.Recruit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stage")
public class Stage extends OffreSpecialisee {

    @Column(name = "duree_en_mois", nullable = false)
    private int dureeEnMois;

    @Column(name = "domaine")
    private String domaine;

    public Stage() {}

    public Stage(String titre, String description, Entreprise entreprise, int dureeEnMois, String domaine) {
        super(titre, description, "stage", entreprise);
        this.dureeEnMois = dureeEnMois;
        this.domaine = domaine == null ? "" : domaine.trim();
    }

    public int getDureeEnMois() { return dureeEnMois; }
    public void setDureeEnMois(int dureeEnMois) { this.dureeEnMois = dureeEnMois; }

    public String getDomaine() { return domaine == null ? "" : domaine; }
    public void setDomaine(String domaine) { this.domaine = domaine; }
}
