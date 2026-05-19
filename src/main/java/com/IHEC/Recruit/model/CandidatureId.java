package com.IHEC.Recruit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CandidatureId implements Serializable {

    @Column(name = "offre_id")
    private Long offreId;

    @Column(name = "candidat_id")
    private Integer candidatId;

    public CandidatureId() {}

    public CandidatureId(Long offreId, Integer candidatId) {
        this.offreId = offreId;
        this.candidatId = candidatId;
    }

    public Long getOffreId() {
        return offreId;
    }

    public void setOffreId(Long offreId) {
        this.offreId = offreId;
    }

    public Integer getCandidatId() {
        return candidatId;
    }

    public void setCandidatId(Integer candidatId) {
        this.candidatId = candidatId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CandidatureId that = (CandidatureId) obj;
        return Objects.equals(offreId, that.offreId)
                && Objects.equals(candidatId, that.candidatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offreId, candidatId);
    }
}
