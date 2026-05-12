package com.IHEC.Recruit.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "offre")
@Inheritance(strategy = InheritanceType.JOINED)
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "type_offre", nullable = false)
    private String typeOffre;

    @Column(name = "date_publication")
    private LocalDate datePublication;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "candidature",
        joinColumns = @JoinColumn(name = "offre_id"),
        inverseJoinColumns = @JoinColumn(name = "candidat_id")
    )
    private List<Candidat> candidatures = new ArrayList<>();

    public Offre() {}

    public Offre(String titre, String description, String typeOffre, Entreprise entreprise) {
        this.titre = titre == null ? null : titre.trim();
        this.description = description == null ? null : description.trim();
        this.typeOffre = typeOffre == null ? null : typeOffre.trim();
        this.datePublication = LocalDate.now();
        this.dateExpiration = null;
        this.entreprise = entreprise;
    }

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

    public List<Candidat> getCandidatures() { return candidatures; }
    public void setCandidatures(List<Candidat> candidatures) { this.candidatures = candidatures; }

    public boolean estExpiree() {
        if (dateExpiration == null) return false;
        return LocalDate.now().isAfter(dateExpiration);
    }

    public int getNombreCandidatures() {
        return candidatures == null ? 0 : candidatures.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Offre)) return false;
        Offre other = (Offre) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
