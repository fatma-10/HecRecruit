package com.IHEC.Recruit.dto;

import jakarta.validation.constraints.NotBlank;

public class OffreForm {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "Le type est obligatoire")
    private String type; // "stage", "alternance", "pfe"

    // Champs Stage
    private Integer duree;
    private String domaine;

    // Champs Alternance
    private String rythme;
    // (duree reutilise pour alternance)

    // Champs PFE
    private String sujet;
    private String technologies;

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }
    public String getDomaine() { return domaine; }
    public void setDomaine(String domaine) { this.domaine = domaine; }
    public String getRythme() { return rythme; }
    public void setRythme(String rythme) { this.rythme = rythme; }
    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }
}
