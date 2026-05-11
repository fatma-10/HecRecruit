package com.IHEC.Recruit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "projet_fin_etudes")
public class ProjetFinEtudes extends OffreSpecialisee {

    @Column(name = "sujet", columnDefinition = "TEXT")
    private String sujet;

    @Column(name = "technologies")
    private String technologies;

    // ---- No-arg constructor required by JPA ----
    public ProjetFinEtudes() {}

    public ProjetFinEtudes(String titre, String description, Entreprise entreprise,
                           String sujet, String technologies) {
        super(titre, description, "projet fin d'etudes", entreprise);
        this.sujet = sujet;
        this.technologies = technologies;
    }

    // ---- Getters & Setters ----

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }

    // ---- getInfosPrincipales ----

    @Override
    public String[] getInfosPrincipales() {
        return new String[] {
            getId() != null ? getId().toString() : "",
            getTitre(),
            "Projet Fin d'Etudes",
            sujet,
            technologies,
            getDatePublication() != null ? getDatePublication().toString() : "",
            getDateExpiration() != null ? getDateExpiration().toString() : "Non définie",
            getEntreprise() != null ? getEntreprise().getNom() : "",
            String.valueOf(getCandidatures().size())
        };
    }

    @Override
    public String getTypeOffre() { return "Projet Fin d'Etudes"; }
}
