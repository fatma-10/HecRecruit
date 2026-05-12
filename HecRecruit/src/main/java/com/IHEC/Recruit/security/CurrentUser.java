package com.IHEC.Recruit.security;

import com.IHEC.Recruit.entity.Candidat;
import com.IHEC.Recruit.entity.Entreprise;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;

/**
 * Utilisateur connecte stocke en session HTTP.
 * Exactement un des deux champs est non-null si quelqu'un est connecte.
 */
@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentUser implements Serializable {

    private Candidat candidat;
    private Entreprise entreprise;

    public boolean isLogged() { return candidat != null || entreprise != null; }
    public boolean isCandidat() { return candidat != null; }
    public boolean isEntreprise() { return entreprise != null; }

    public Candidat getCandidat() { return candidat; }
    public Entreprise getEntreprise() { return entreprise; }

    public void loginCandidat(Candidat c) {
        this.candidat = c;
        this.entreprise = null;
    }

    public void loginEntreprise(Entreprise e) {
        this.entreprise = e;
        this.candidat = null;
    }

    public void logout() {
        this.candidat = null;
        this.entreprise = null;
    }

    public String getDisplayName() {
        if (candidat != null) return candidat.getPrenom() + " " + candidat.getNom();
        if (entreprise != null) return entreprise.getNom();
        return null;
    }
}
