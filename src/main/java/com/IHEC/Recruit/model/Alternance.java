package com.IHEC.Recruit.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("alternance")
public class Alternance extends Offre {

    @Column(name = "rythme")
    private String rythme;

    @Column(name = "duree_en_mois")
    private Integer dureeEnMois;

    // ---- No-arg constructor required by JPA ----
    public Alternance() {}

    public Alternance(String titre, String description, Entreprise entreprise, String rythme, int dureeEnMois) {
        super(titre, description, "alternance", entreprise);
        if (dureeEnMois <= 0)
            throw new IllegalArgumentException("La durée doit être supérieure à 0");
        this.rythme = rythme;
        this.dureeEnMois = dureeEnMois;
    }

    // ---- Getters & Setters ----

    public String getRythme() { return rythme; }
    public void setRythme(String rythme) { this.rythme = rythme; }

    public int getDureeEnMois() { return dureeEnMois != null ? dureeEnMois : 0; }
    public void setDureeEnMois(int dureeEnMois) { this.dureeEnMois = dureeEnMois; }

    @Override
    public String getTypeOffre() { return "Alternance"; }
}
