package com.IHEC.Recruit.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "entreprise")
public class Entreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column
    private String secteur;

    @Column
    private String adresse;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String mdp;

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Offre> offresPubliees = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "entreprise_wishlist",
        joinColumns = @JoinColumn(name = "entreprise_id"),
        inverseJoinColumns = @JoinColumn(name = "candidat_id")
    )
    private List<Candidat> wishlist = new ArrayList<>();

    public Entreprise() {}

    public Entreprise(String nom, String secteur, String adresse,
                      String email, String telephone, String mdp) {
        this.nom = nom == null ? null : nom.trim();
        this.email = email == null ? null : email.trim();
        this.telephone = telephone == null ? null : telephone.trim();
        this.mdp = mdp == null ? null : mdp.trim();
        this.secteur = secteur == null ? "" : secteur.trim();
        this.adresse = adresse == null ? "" : adresse.trim();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getSecteur() { return secteur; }
    public void setSecteur(String secteur) { this.secteur = secteur; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }

    public List<Offre> getOffresPubliees() { return offresPubliees; }
    public void setOffresPubliees(List<Offre> offresPubliees) { this.offresPubliees = offresPubliees; }

    public List<Candidat> getWishlist() { return wishlist; }
    public void setWishlist(List<Candidat> wishlist) { this.wishlist = wishlist; }

    public boolean verifierMotDePasse(String mdp) {
        return this.mdp != null && this.mdp.equals(mdp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Entreprise)) return false;
        Entreprise other = (Entreprise) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
