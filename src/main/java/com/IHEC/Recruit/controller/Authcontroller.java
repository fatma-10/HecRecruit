package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.model.Candidat;
import com.IHEC.Recruit.model.Entreprise;
import com.IHEC.Recruit.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller gérant l'authentification :
 * - Page d'accueil
 * - Connexion candidat / entreprise
 * - Inscription candidat / entreprise
 * - Déconnexion
 *
 * Convention de session :
 *   "candidatId"    → int  (CIN du candidat connecté)
 *   "entrepriseId"  → Long (id de l'entreprise connectée)
 *   "userType"      → "candidat" | "entreprise"
 */
@Controller
public class Authcontroller {

    private final AuthService authService;

    public Authcontroller(AuthService authService) {
        this.authService = authService;
    }

    // ================================================================
    //  Page d'accueil
    // ================================================================

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    // ================================================================
    //  CONNEXION
    // ================================================================

    @GetMapping("/login")
    public String loginForm(@RequestParam(defaultValue = "candidat") String type,
                            Model model) {
        model.addAttribute("type", type);
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String type,
                              @RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes ra) {
        if ("candidat".equals(type)) {
            Candidat c = authService.loginCandidat(email, password);
            if (c == null) {
                ra.addFlashAttribute("error", "Email ou mot de passe incorrect");
                return "redirect:/login?type=candidat";
            }
            session.setAttribute("candidatId", c.getId());
            session.setAttribute("userType", "candidat");
            session.setAttribute("candidatObj", c);
            session.setAttribute("userEmail", c.getEmail());
            session.setAttribute("userNom", c.getPrenom() + " " + c.getNom());
            return "redirect:/candidat/dashboard";
        } else {
            Entreprise e = authService.loginEntreprise(email, password);
            if (e == null) {
                ra.addFlashAttribute("error", "Email ou mot de passe incorrect");
                return "redirect:/login?type=entreprise";
            }
            session.setAttribute("entrepriseId", e.getId());
            session.setAttribute("userType", "entreprise");
            session.setAttribute("entrepriseObj", e);
            session.setAttribute("userEmail", e.getEmail());
            session.setAttribute("userNom", e.getNom());
            return "redirect:/entreprise/dashboard";
        }
    }

    // ================================================================
    //  INSCRIPTION
    // ================================================================

    @GetMapping("/register")
    public String registerForm(@RequestParam(defaultValue = "candidat") String type,
                               Model model) {
        model.addAttribute("type", type);
        return "register";
    }

    @PostMapping("/register/candidat")
    public String registerCandidat(@RequestParam String nom,
                                   @RequestParam String prenom,
                                   @RequestParam String email,
                                   @RequestParam String telephone,
                                   @RequestParam String password,
                                   @RequestParam String candidatType,
                                   @RequestParam String cin,
                                   @RequestParam(required = false) String niveau,
                                   @RequestParam(required = false) String filiere,
                                   @RequestParam(required = false) String etablissement,
                                   @RequestParam(required = false) String anneeDiplome,
                                   @RequestParam(required = false) String posteActuel,
                                   @RequestParam(required = false) String entrepriseActuelle,
                                   HttpSession session,
                                   RedirectAttributes ra) {
        try {
            Map<String, String> infos = new HashMap<>();
            infos.put("id", cin);

            if ("etudiant".equals(candidatType)) {
                infos.put("niveau", niveau);
                infos.put("filiere", filiere);
                infos.put("etablissement", etablissement);
            } else {
                infos.put("anneeDiplome", anneeDiplome);
                infos.put("posteActuel", posteActuel != null ? posteActuel : "");
                infos.put("entrepriseActuelle", entrepriseActuelle != null ? entrepriseActuelle : "");
            }

            Candidat c = authService.registerCandidat(
                    nom, prenom, email, telephone, password, candidatType, infos);

            session.setAttribute("candidatId", c.getId());
            session.setAttribute("userType", "candidat");
            session.setAttribute("candidatObj", c);
            session.setAttribute("userEmail", c.getEmail());
            session.setAttribute("userNom", c.getPrenom() + " " + c.getNom());

            return "redirect:/candidat/dashboard";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/register?type=candidat";
        }
    }

    @PostMapping("/register/entreprise")
    public String registerEntreprise(@RequestParam String nom,
                                     @RequestParam String secteur,
                                     @RequestParam String adresse,
                                     @RequestParam String email,
                                     @RequestParam String telephone,
                                     @RequestParam String password,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        try {
            Entreprise e = authService.registerEntreprise(
                    nom, secteur, adresse, email, telephone, password);

            session.setAttribute("entrepriseId", e.getId());
            session.setAttribute("userType", "entreprise");
            session.setAttribute("entrepriseObj", e);
            session.setAttribute("userEmail", e.getEmail());
            session.setAttribute("userNom", e.getNom());

            return "redirect:/entreprise/dashboard";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/register?type=entreprise";
        }
    }

    // ================================================================
    //  DÉCONNEXION
    // ================================================================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}