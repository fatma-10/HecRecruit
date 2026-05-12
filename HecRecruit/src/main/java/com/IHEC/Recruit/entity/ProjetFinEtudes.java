package com.IHEC.Recruit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "projet_fin_etudes")
public class ProjetFinEtudes extends OffreSpecialisee {

    @Column(name = "sujet", columnDefinition = "TEXT")
    private String sujet;

    @Column(name = "technologies")
    private String technologies;

    public ProjetFinEtudes() {}

    public ProjetFinEtudes(String titre, String description, Entreprise entreprise,
                           String sujet, String technologies) {
        super(titre, description, "projet fin d'etudes", entreprise);
        this.sujet = sujet == null ? "" : sujet.trim();
        this.technologies = technologies == null ? "" : technologies.trim();
    }

    public String getSujet() { return sujet == null ? "" : sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getTechnologies() { return technologies == null ? "" : technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }
}
