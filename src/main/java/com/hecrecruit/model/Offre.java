package com.hecrecruit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "offre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(name = "titre", nullable = false, length = 200)
    private String titre;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "type_offre", nullable = false, length = 50)
    private String typeOffre;

    @Column(name = "date_publication", nullable = false)
    private LocalDate datePublication;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "candidaturesEnCours")
    private List<Candidat> candidatures;

    @PrePersist
    protected void onCreate() {
        if (datePublication == null) {
            datePublication = LocalDate.now();
        }
    }

    public String[] getInfosPrincipales() {
        return new String[]{
                id.toString(),
                titre,
                typeOffre,
                datePublication.toString(),
                dateExpiration != null ? dateExpiration.toString() : "Non définie",
                entreprise.getNom(),
                String.valueOf(candidatures != null ? candidatures.size() : 0)
        };
    }

    public boolean estExpiree() {
        if (dateExpiration == null) {
            return false;
        }
        return LocalDate.now().isAfter(dateExpiration);
    }

    public boolean candidatAPostule(Candidat candidat) {
        if (candidat == null || candidatures == null) {
            return false;
        }
        return candidatures.contains(candidat);
    }

    public int getNombreCandidatures() {
        return candidatures != null ? candidatures.size() : 0;
    }
}
