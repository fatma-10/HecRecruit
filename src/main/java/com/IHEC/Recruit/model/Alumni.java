package com.IHEC.Recruit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alumni")
public class Alumni extends Candidat {

    @Column(name = "annee_diplome", nullable = false)
    private int anneeDiplome;

    @Column(name = "poste_actuel")
    private String posteActuel;

    @Column(name = "entreprise_actuelle")
    private String entrepriseActuelle;

    // ---- No-arg constructor required by JPA ----
    public Alumni() {}

    public Alumni(int id, String nom, String prenom, String email, String telephone,
                  String mdp, int anneeDiplome, String posteActuel, String entrepriseActuelle) {
        super(id, nom, prenom, email, telephone, mdp);

        if (anneeDiplome <= 0)
            throw new IllegalArgumentException("L'année de diplôme est invalide");

        this.anneeDiplome = anneeDiplome;
        this.posteActuel = (posteActuel == null) ? "" : posteActuel.trim();
        this.entrepriseActuelle = (entrepriseActuelle == null) ? "" : entrepriseActuelle.trim();
    }

    // ---- Getters & Setters ----

    public int getAnneeDiplome() { return anneeDiplome; }
    public void setAnneeDiplome(int anneeDiplome) { this.anneeDiplome = anneeDiplome; }

    public String getPosteActuel() { return posteActuel; }
    public void setPosteActuel(String posteActuel) { this.posteActuel = posteActuel; }

    public String getEntrepriseActuelle() { return entrepriseActuelle; }
    public void setEntrepriseActuelle(String entrepriseActuelle) {
        this.entrepriseActuelle = entrepriseActuelle;
    }

    // ---- Override getInfosPrincipales (héritage + polymorphisme) ----

    @Override
    public String[] getInfosPrincipales() {
        return new String[] {
            String.valueOf(getId()),
            getNom(),
            getPrenom(),
            getEmail(),
            getTelephone(),
            String.valueOf(anneeDiplome),
            (posteActuel == null || posteActuel.isEmpty()) ? "Non spécifié" : posteActuel,
            (entrepriseActuelle == null || entrepriseActuelle.isEmpty()) ? "Non spécifiée" : entrepriseActuelle
        };
    }
}
