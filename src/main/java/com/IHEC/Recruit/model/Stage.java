package com.IHEC.Recruit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stage")
public class Stage extends OffreSpecialisee {

    @Column(name = "duree_en_mois", nullable = false)
    private int dureeEnMois;

    @Column(name = "domaine")
    private String domaine;

    // ---- No-arg constructor required by JPA ----
    public Stage() {}

    public Stage(String titre, String description, Entreprise entreprise, int dureeEnMois, String domaine) {
        super(titre, description, "stage", entreprise);
        if (dureeEnMois <= 0)
            throw new IllegalArgumentException("La durée doit être supérieure à 0");
        this.dureeEnMois = dureeEnMois;
        this.domaine = domaine;
    }

    // ---- Getters & Setters ----

    public int getDureeEnMois() { return dureeEnMois; }
    public void setDureeEnMois(int dureeEnMois) { this.dureeEnMois = dureeEnMois; }

    public String getDomaine() { return domaine; }
    public void setDomaine(String domaine) { this.domaine = domaine; }

    // ---- getInfosPrincipales ----

    @Override
    public String[] getInfosPrincipales() {
        return new String[] {
            getId() != null ? getId().toString() : "",
            getTitre(),
            "Stage",
            domaine,
            dureeEnMois + " mois",
            getDatePublication() != null ? getDatePublication().toString() : "",
            getDateExpiration() != null ? getDateExpiration().toString() : "Non définie",
            getEntreprise() != null ? getEntreprise().getNom() : "",
            String.valueOf(getCandidatures().size())
        };
    }

    @Override
    public String getTypeOffre() { return "Stage"; }
}
