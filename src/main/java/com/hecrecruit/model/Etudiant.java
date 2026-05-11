package com.hecrecruit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "etudiant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant extends Candidat {

    @Column(name = "niveau", nullable = false, length = 50)
    private String niveau;

    @Column(name = "filiere", nullable = false, length = 100)
    private String filiere;

    @Column(name = "etablissement", nullable = false, length = 150)
    private String etablissement;

    @Override
    public String[] getInfosPrincipales() {
        return new String[]{
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
