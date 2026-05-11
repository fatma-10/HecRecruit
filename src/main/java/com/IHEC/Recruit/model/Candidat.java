package com.IHEC.Recruit.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidat")
@Inheritance(strategy = InheritanceType.JOINED)
public class Candidat {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private int id; // CIN (8 chiffres) - pas auto-généré, fourni par l'utilisateur

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String mdp;

    @ManyToMany(mappedBy = "candidatures")
    private List<Offre> candidaturesEnCours = new ArrayList<>();

    // ---- No-arg constructor required by JPA ----
    public Candidat() {}

    public Candidat(int id, String nom, String prenom, String email,
                    String telephone, String mdp) {
        valider(id, nom, prenom, email, telephone, mdp);
        this.id = id;
        this.nom = nom.trim();
        this.prenom = prenom.trim();
        this.email = email.trim();
        this.telephone = telephone.trim();
        this.mdp = mdp.trim();
    }

    // ---- Validation (unicité gérée par le service / la BDD) ----

    private void valider(int id, String nom, String prenom, String email,
                         String telephone, String mdp) {
        if (nom == null || nom.trim().isEmpty())
            throw new IllegalArgumentException("Le nom est obligatoire");
        if (prenom == null || prenom.trim().isEmpty())
            throw new IllegalArgumentException("Le prénom est obligatoire");
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("L'email est obligatoire");
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$"))
            throw new IllegalArgumentException("Format d'email invalide");
        if (telephone == null || telephone.trim().isEmpty())
            throw new IllegalArgumentException("Le téléphone est obligatoire");
        if (mdp == null || mdp.trim().isEmpty())
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        if (id < 10000000 || id > 99999999)
            throw new IllegalArgumentException("Le CIN doit être un nombre de 8 chiffres");
    }

    // ---- Getters & Setters ----

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMdp() { return mdp; }
    public void setMdp(String mdp) {
        if (mdp == null || mdp.trim().isEmpty())
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        this.mdp = mdp.trim();
    }

    public List<Offre> getCandidaturesEnCours() { return candidaturesEnCours; }
    public void setCandidaturesEnCours(List<Offre> candidaturesEnCours) {
        this.candidaturesEnCours = candidaturesEnCours;
    }

    // ---- Méthodes ----

    public String[] getInfosPrincipales() {
        return new String[] {
            String.valueOf(id),
            nom,
            prenom,
            email,
            telephone,
            String.valueOf(candidaturesEnCours.size())
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Candidat candidat = (Candidat) obj;
        return id == candidat.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
