package com.IHEC.Recruit.model;

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

    // ---- No-arg constructor required by JPA ----
    public Etudiant() {}

    public Etudiant(int id, String nom, String prenom, String email, String telephone,
                    String mdp, String niveau, String filiere, String etablissement) {
        super(id, nom, prenom, email, telephone, mdp);

        if (niveau == null || niveau.trim().isEmpty())
            throw new IllegalArgumentException("Le niveau est obligatoire");
        if (filiere == null || filiere.trim().isEmpty())
            throw new IllegalArgumentException("La filière est obligatoire");
        if (etablissement == null || etablissement.trim().isEmpty())
            throw new IllegalArgumentException("L'établissement est obligatoire");

        this.niveau = niveau.trim();
        this.filiere = filiere.trim();
        this.etablissement = etablissement.trim();
    }

    // ---- Getters & Setters ----

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public String getEtablissement() { return etablissement; }
    public void setEtablissement(String etablissement) { this.etablissement = etablissement; }

    // ---- Override getInfosPrincipales (héritage + polymorphisme) ----

    @Override
    public String[] getInfosPrincipales() {
        return new String[] {
            String.valueOf(getId()),
            getNom(),
            getPrenom(),
            niveau,
            filiere,
            etablissement,
            getEmail(),
            getTelephone()
        };
    }
}
