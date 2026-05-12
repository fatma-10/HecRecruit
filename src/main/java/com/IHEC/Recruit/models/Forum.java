package com.IHEC.Recruit.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "forum")
public class Forum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String auteur;

    @Column(name = "email_auteur", nullable = false)
    private String emailAuteur;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "date_publication", nullable = false)
    private LocalDateTime datePublication;

    @Column(name = "est_etudiant", nullable = false)
    private boolean estEtudiant;

    public Forum() {}

    public Forum(String auteur, String emailAuteur, String message, boolean estEtudiant) {
        this.auteur = auteur;
        this.emailAuteur = emailAuteur;
        this.message = message;
        this.datePublication = LocalDateTime.now();
        this.estEtudiant = estEtudiant;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public String getEmailAuteur() { return emailAuteur; }
    public void setEmailAuteur(String emailAuteur) { this.emailAuteur = emailAuteur; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDateTime datePublication) { this.datePublication = datePublication; }

    public boolean isEstEtudiant() { return estEtudiant; }
    public void setEstEtudiant(boolean estEtudiant) { this.estEtudiant = estEtudiant; }
}
