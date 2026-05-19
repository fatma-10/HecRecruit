package com.IHEC.Recruit.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "offre")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_offre", discriminatorType = DiscriminatorType.STRING)
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "type_offre", nullable = false, insertable = false, updatable = false)
    private String typeOffre;

    @Column(name = "date_publication")
    private LocalDate datePublication;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatureDetails = new ArrayList<>();

    // ---- No-arg constructor required by JPA ----
    public Offre() {}

    public Offre(String titre, String description, String typeOffre, Entreprise entreprise) {
        if (titre == null || titre.trim().isEmpty())
            throw new IllegalArgumentException("Le titre est obligatoire");
        if (description == null || description.trim().isEmpty())
            throw new IllegalArgumentException("La description est obligatoire");
        if (typeOffre == null || typeOffre.trim().isEmpty())
            throw new IllegalArgumentException("Le type d'offre est obligatoire");
        if (entreprise == null)
            throw new IllegalArgumentException("L'entreprise est obligatoire");

        this.titre = titre.trim();
        this.description = description.trim();
        this.typeOffre = typeOffre.trim();
        this.datePublication = LocalDate.now();
        this.dateExpiration = null;
        this.entreprise = entreprise;
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTypeOffre() { return typeOffre; }
    public void setTypeOffre(String typeOffre) { this.typeOffre = typeOffre; }

    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate datePublication) { this.datePublication = datePublication; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    public Entreprise getEntreprise() { return entreprise; }
    public void setEntreprise(Entreprise entreprise) { this.entreprise = entreprise; }

    public List<Candidature> getCandidatureDetails() { return candidatureDetails; }
    public void setCandidatureDetails(List<Candidature> candidatureDetails) {
        this.candidatureDetails = candidatureDetails;
    }

    @Transient
    public List<Candidat> getCandidatures() {
        return candidatureDetails.stream()
                .map(Candidature::getCandidat)
                .collect(Collectors.toList());
    }

    // ---- Business methods ----

    public boolean estExpiree() {
        if (dateExpiration == null) return false;
        return LocalDate.now().isAfter(dateExpiration);
    }

    public boolean ajouterCandidature(Candidat candidat) {
        if (candidat == null || estExpiree() || candidatAPostule(candidat))
            return false;
        candidatureDetails.add(new Candidature(this, candidat));
        return true;
    }

    public boolean candidatAPostule(Candidat candidat) {
        if (candidat == null) return false;
        return candidatureDetails.stream()
                .anyMatch(candidature -> candidat.equals(candidature.getCandidat()));
    }

    public int getNombreCandidatures() {
        return candidatureDetails.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Offre offre = (Offre) obj;
        return id != null && id.equals(offre.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
