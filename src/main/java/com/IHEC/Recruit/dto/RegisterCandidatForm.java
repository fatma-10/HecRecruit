package com.IHEC.Recruit.dto;

import jakarta.validation.constraints.*;

public class RegisterCandidatForm {

    @NotBlank(message = "Le CIN est obligatoire")
    @Pattern(regexp = "\\d{8}", message = "Le CIN doit contenir exactement 8 chiffres")
    private String id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Pattern(regexp = "^[\\w.-]+@ihec\\.ucar\\.tn$",
            message = "L'email doit se terminer par @ihec.ucar.tn")
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "\\d{8}", message = "Le téléphone doit contenir 8 chiffres")
    private String telephone;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String mdp;

    @NotBlank(message = "Le type est obligatoire")
    private String typeCandidat; // "etudiant" ou "alumni"

    // Champs etudiant (requis si typeCandidat = "etudiant", verifie au controller)
    private String niveau;
    private String filiere;
    private String etablissement;

    // Champs alumni
    private Integer anneeDiplome;
    private String posteActuel;
    private String entrepriseActuelle;

    // Getters / Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }
    public String getTypeCandidat() { return typeCandidat; }
    public void setTypeCandidat(String typeCandidat) { this.typeCandidat = typeCandidat; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public String getEtablissement() { return etablissement; }
    public void setEtablissement(String etablissement) { this.etablissement = etablissement; }
    public Integer getAnneeDiplome() { return anneeDiplome; }
    public void setAnneeDiplome(Integer anneeDiplome) { this.anneeDiplome = anneeDiplome; }
    public String getPosteActuel() { return posteActuel; }
    public void setPosteActuel(String posteActuel) { this.posteActuel = posteActuel; }
    public String getEntrepriseActuelle() { return entrepriseActuelle; }
    public void setEntrepriseActuelle(String entrepriseActuelle) { this.entrepriseActuelle = entrepriseActuelle; }
}
