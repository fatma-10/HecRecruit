package com.IHEC.Recruit.models;

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

    public Alumni() {}

    public Alumni(String id, String nom, String prenom, String email, String telephone,
                  String mdp, int anneeDiplome, String posteActuel, String entrepriseActuelle) {
        super(id, nom, prenom, email, telephone, mdp);
        this.anneeDiplome = anneeDiplome;
        this.posteActuel = posteActuel == null ? "" : posteActuel.trim();
        this.entrepriseActuelle = entrepriseActuelle == null ? "" : entrepriseActuelle.trim();
    }

    public int getAnneeDiplome() { return anneeDiplome; }
    public void setAnneeDiplome(int anneeDiplome) { this.anneeDiplome = anneeDiplome; }

    public String getPosteActuel() { return posteActuel; }
    public void setPosteActuel(String posteActuel) { this.posteActuel = posteActuel; }

    public String getEntrepriseActuelle() { return entrepriseActuelle; }
    public void setEntrepriseActuelle(String entrepriseActuelle) { this.entrepriseActuelle = entrepriseActuelle; }
}
