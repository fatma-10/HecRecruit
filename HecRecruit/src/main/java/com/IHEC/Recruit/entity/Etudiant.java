package com.IHEC.Recruit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "etudiant")
public class Etudiant extends Candidat {

    @Column(nullable = false)
    private String niveau;

    @Column(nullable = false)
    private String filiere;

    @Column(nullable = false)
    private String etablissement;

    public Etudiant() {}

    public Etudiant(Integer id, String nom, String prenom, String email, String telephone,
                    String mdp, String niveau, String filiere, String etablissement) {
        super(id, nom, prenom, email, telephone, mdp);
        this.niveau = niveau == null ? null : niveau.trim();
        this.filiere = filiere == null ? null : filiere.trim();
        this.etablissement = etablissement == null ? null : etablissement.trim();
    }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public String getEtablissement() { return etablissement; }
    public void setEtablissement(String etablissement) { this.etablissement = etablissement; }
}
