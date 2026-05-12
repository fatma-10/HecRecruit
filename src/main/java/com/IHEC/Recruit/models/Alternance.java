package com.IHEC.Recruit.models;

import jakarta.persistence.*;

@Entity
@Table(name = "alternance")
public class Alternance extends OffreSpecialisee {

    @Column(name = "rythme")
    private String rythme;

    @Column(name = "duree_en_mois", nullable = false)
    private int dureeEnMois;

    public Alternance() {}

    public Alternance(String titre, String description, Entreprise entreprise, String rythme, int dureeEnMois) {
        super(titre, description, "alternance", entreprise);
        this.rythme = rythme == null ? "" : rythme.trim();
        this.dureeEnMois = dureeEnMois;
    }

    public String getRythme() { return rythme == null ? "" : rythme; }
    public void setRythme(String rythme) { this.rythme = rythme; }

    public int getDureeEnMois() { return dureeEnMois; }
    public void setDureeEnMois(int dureeEnMois) { this.dureeEnMois = dureeEnMois; }
}
