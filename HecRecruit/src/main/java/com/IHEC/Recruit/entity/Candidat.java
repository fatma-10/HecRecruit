package com.IHEC.Recruit.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "candidat")
@Inheritance(strategy = InheritanceType.JOINED)
public class Candidat {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Integer id; // CIN (8 chiffres) fourni par l'utilisateur, pas auto-genere

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

    @ManyToMany(mappedBy = "candidatures", fetch = FetchType.LAZY)
    private List<Offre> candidaturesEnCours = new ArrayList<>();

    public Candidat() {}

    public Candidat(Integer id, String nom, String prenom, String email,
                    String telephone, String mdp) {
        this.id = id;
        this.nom = nom == null ? null : nom.trim();
        this.prenom = prenom == null ? null : prenom.trim();
        this.email = email == null ? null : email.trim();
        this.telephone = telephone == null ? null : telephone.trim();
        this.mdp = mdp == null ? null : mdp.trim();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }

    public List<Offre> getCandidaturesEnCours() { return candidaturesEnCours; }
    public void setCandidaturesEnCours(List<Offre> candidaturesEnCours) {
        this.candidaturesEnCours = candidaturesEnCours;
    }

    public boolean verifierMotDePasse(String mdp) {
        return this.mdp != null && this.mdp.equals(mdp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Candidat)) return false;
        Candidat other = (Candidat) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
