package com.hecrecruit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alternance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alternance extends Offre {

    @Column(name = "salaire_mensuel", nullable = false)
    private Double salaireMensuel;

    @Column(name = "rythme_alternance", nullable = false, length = 100)
    private String rythmeAlternance;

    @Column(name = "duree_contrat_mois", nullable = false)
    private Integer dureeContratMois;
}
