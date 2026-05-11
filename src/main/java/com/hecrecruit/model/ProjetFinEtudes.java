package com.hecrecruit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "projet_fin_etudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjetFinEtudes extends Offre {

    @Column(name = "technologies", nullable = false, length = 255)
    private String technologies;

    @Column(name = "niveau_requis", nullable = false, length = 100)
    private String niveauRequis;

    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;
}
