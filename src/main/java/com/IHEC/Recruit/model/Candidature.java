package com.IHEC.Recruit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidature")
public class Candidature {

    @EmbeddedId
    private CandidatureId id = new CandidatureId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("offreId")
    @JoinColumn(name = "offre_id")
    private Offre offre;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("candidatId")
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'PENDING'")
    private CandidatureStatus status = CandidatureStatus.PENDING;

    public Candidature() {}

    public Candidature(Offre offre, Candidat candidat) {
        if (offre == null) {
            throw new IllegalArgumentException("L'offre est obligatoire");
        }
        if (candidat == null) {
            throw new IllegalArgumentException("Le candidat est obligatoire");
        }
        this.offre = offre;
        this.candidat = candidat;
        this.id = new CandidatureId(offre.getId(), candidat.getId());
        this.status = CandidatureStatus.PENDING;
    }

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = CandidatureStatus.PENDING;
        }
    }

    public CandidatureId getId() {
        return id;
    }

    public void setId(CandidatureId id) {
        this.id = id;
    }

    public Offre getOffre() {
        return offre;
    }

    public void setOffre(Offre offre) {
        this.offre = offre;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public CandidatureStatus getStatus() {
        return status;
    }

    public void setStatus(CandidatureStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        this.status = status;
    }

    public boolean isPending() {
        return CandidatureStatus.PENDING.equals(status);
    }

    public boolean isContacted() {
        return CandidatureStatus.CONTACTED.equals(status);
    }

    public boolean isRefused() {
        return CandidatureStatus.REFUSED.equals(status);
    }
}
