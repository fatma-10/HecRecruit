package com.hecrecruit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "entreprise")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "secteur", length = 100)
    private String secteur;

    @Column(name = "adresse", length = 255)
    private String adresse;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "telephone", nullable = false, length = 20)
    private String telephone;

    @Column(name = "mdp", nullable = false, length = 255)
    private String mdp;

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Offre> offresPubliees;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "wishlist",
        joinColumns = @JoinColumn(name = "entreprise_id"),
        inverseJoinColumns = @JoinColumn(name = "candidat_id")
    )
    private List<Candidat> wishlist;

    public String[] getInfosPrincipales() {
        return new String[]{
                nom,
                secteur != null ? secteur : "Non spécifié",
                adresse != null ? adresse : "Non spécifié",
                email,
                telephone,
                String.valueOf(offresPubliees != null ? offresPubliees.size() : 0),
                String.valueOf(wishlist != null ? wishlist.size() : 0)
        };
    }

    public boolean verifierMotDePasse(String mdp) {
        return this.mdp.equals(mdp);
    }
}
