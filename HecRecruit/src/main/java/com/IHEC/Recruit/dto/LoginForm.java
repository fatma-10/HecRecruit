package com.IHEC.Recruit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginForm {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String mdp;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }
}
