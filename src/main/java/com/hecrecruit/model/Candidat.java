package com.hecrecruit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "candidat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Candidat {

    @Id
    @Column(name = "cin", nullable = false)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "telephone", nullable = false, length = 20)
    private String telephone;

    @Column(name = "mdp", nullable = false, length = 255)
    private String mdp;

    @ManyToMany(cascade = CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinTable(
        name = "candidature",
        joinColumns = @JoinColumn(name = "candidat_id"),
        inverseJoinColumns = @JoinColumn(name = "offre_id")
    )
    private List<Offre> candidaturesEnCours;

    public String[] getInfosPrincipales() {
        return new String[]{
                String.valueOf(id),
                nom,
                prenom,
                email,
                telephone,
                String.valueOf(candidaturesEnCours != null ? candidaturesEnCours.size() : 0)
        };
    }
}
