package com.IHEC.Recruit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ForumMessageForm {

    @NotBlank(message = "Le message ne peut pas être vide")
    @Size(max = 2000, message = "Le message est trop long (max 2000 caractères)")
    private String message;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
