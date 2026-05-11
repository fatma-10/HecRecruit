package com.IHEC.Recruit.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Offre> offresPubliees = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "entreprise_wishlist",
        joinColumns = @JoinColumn(name = "entreprise_id"),
        inverseJoinColumns = @JoinColumn(name = "candidat_id")
    )
    private List<Candidat> wishlist = new ArrayList<>();

    // ---- No-arg constructor required by JPA ----
    public Entreprise() {}

    public Entreprise(String nom, String secteur, String adresse,
                      String email, String telephone, String mdp) {
        if (nom == null || nom.trim().isEmpty())
            throw new IllegalArgumentException("Le nom de l'entreprise est obligatoire");
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("L'email est obligatoire");
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$"))
            throw new IllegalArgumentException("Format d'email invalide");
        if (telephone == null || telephone.trim().isEmpty())
            throw new IllegalArgumentException("Le téléphone est obligatoire");
        if (mdp == null || mdp.trim().isEmpty())
            throw new IllegalArgumentException("Le mot de passe est obligatoire");

        this.nom = nom.trim();
        this.email = email.trim();
        this.telephone = telephone.trim();
        this.mdp = mdp.trim();
        this.secteur = (secteur != null) ? secteur.trim() : "";
        this.adresse = (adresse != null) ? adresse.trim() : "";
    }

    // ---- Getters & Setters ----

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
    public void setMdp(String mdp) {
        if (mdp == null || mdp.trim().isEmpty())
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        this.mdp = mdp.trim();
    }

    public List<Offre> getOffresPubliees() { return offresPubliees; }
    public void setOffresPubliees(List<Offre> offresPubliees) { this.offresPubliees = offresPubliees; }

    public List<Candidat> getWishlist() { return wishlist; }
    public void setWishlist(List<Candidat> wishlist) { this.wishlist = wishlist; }

    // ---- Méthodes ----

    public String[] getInfosPrincipales() {
        return new String[] {
            nom,
            secteur,
            adresse,
            email,
            telephone,
            String.valueOf(offresPubliees.size()),
            String.valueOf(wishlist.size())
        };
    }

    public boolean verifierMotDePasse(String mdp) {
        return this.mdp.equals(mdp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entreprise e = (Entreprise) obj;
        return id != null && id.equals(e.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
