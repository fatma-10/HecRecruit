package com.hecrecruit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stage extends Offre {

    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;

    @Column(name = "gratification", nullable = false)
    private Double gratification;

    @Column(name = "lieu", nullable = false, length = 150)
    private String lieu;
}
