package com.hecrecruit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "alumni")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alumni extends Candidat {

    @Column(name = "annee_graduation", nullable = false)
    private Integer anneeGraduation;

    @Column(name = "diplome", nullable = false, length = 150)
    private String diplome;

    @Column(name = "emploi_actuel", length = 150)
    private String emploiActuel;

    @Column(name = "entreprise", length = 150)
    private String entreprise;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    @Override
    public String[] getInfosPrincipales() {
        return new String[]{
                String.valueOf(getId()),
                getNom(),
                getPrenom(),
                String.valueOf(anneeGraduation),
                diplome,
                emploiActuel != null ? emploiActuel : "Non spécifié",
                entreprise != null ? entreprise : "Non spécifié",
                getEmail()
        };
    }
}
