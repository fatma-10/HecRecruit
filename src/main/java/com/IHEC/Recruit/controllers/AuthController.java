package com.IHEC.Recruit.controllers;

import com.IHEC.Recruit.dto.LoginForm;
import com.IHEC.Recruit.dto.RegisterCandidatForm;
import com.IHEC.Recruit.dto.RegisterEntrepriseForm;
import com.IHEC.Recruit.models.Candidat;
import com.IHEC.Recruit.models.Entreprise;
import com.IHEC.Recruit.exception.BusinessException;
import com.IHEC.Recruit.security.CurrentUser;
import com.IHEC.Recruit.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    private final AuthService authService;
    private final CurrentUser currentUser;

    public AuthController(AuthService authService, CurrentUser currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;
    }

    /**
     * Prepare la page slider avec les deux forms (login + register) et le mode initial.
     * Mode = "login" ou "register" : determine quel panel est affiche au chargement.
     */
    private String prepareSlider(String userType, String initialMode, Model model) {
        if (!"candidat".equals(userType) && !"entreprise".equals(userType)) {
            return "redirect:/";
        }
        model.addAttribute("userType", userType);
        model.addAttribute("initialMode", initialMode);

        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        if (!model.containsAttribute("registerForm")) {
            if ("candidat".equals(userType)) {
                model.addAttribute("registerForm", new RegisterCandidatForm());
            } else {
                model.addAttribute("registerForm", new RegisterEntrepriseForm());
            }
        }
        return "auth/slider";
    }

    // ===== LOGIN =====

    @GetMapping("/login/{userType}")
    public String showLogin(@PathVariable String userType, Model model) {
        return prepareSlider(userType, "login", model);
    }

    @PostMapping("/login/{userType}")
    public String doLogin(@PathVariable String userType,
                          @Valid @ModelAttribute("loginForm") LoginForm form,
                          BindingResult br,
                          Model model) {
        if (br.hasErrors()) {
            return prepareSlider(userType, "login", model);
        }

        if ("candidat".equals(userType)) {
            Candidat c = authService.loginCandidat(form.getEmail(), form.getMdp());
            if (c == null) {
                model.addAttribute("error", "Email ou mot de passe incorrect");
                return prepareSlider(userType, "login", model);
            }
            currentUser.loginCandidat(c);
            return "redirect:/candidat/dashboard";
        }

        if ("entreprise".equals(userType)) {
            Entreprise e = authService.loginEntreprise(form.getEmail(), form.getMdp());
            if (e == null) {
                model.addAttribute("error", "Email ou mot de passe incorrect");
                return prepareSlider(userType, "login", model);
            }
            currentUser.loginEntreprise(e);
            return "redirect:/entreprise/dashboard";
        }

        return "redirect:/";
    }

    // ===== REGISTER CANDIDAT =====

    @GetMapping("/register/candidat")
    public String showRegisterCandidat(Model model) {
        return prepareSlider("candidat", "register", model);
    }

    @PostMapping("/register/candidat")
    public String doRegisterCandidat(@Valid @ModelAttribute("registerForm") RegisterCandidatForm form,
                                     BindingResult br,
                                     Model model) {
        if (br.hasErrors()) {
            return prepareSlider("candidat", "register", model);
        }

        String type = form.getTypeCandidat();
        if ("etudiant".equalsIgnoreCase(type)) {
            if (isBlank(form.getNiveau()) || isBlank(form.getFiliere()) || isBlank(form.getEtablissement())) {
                model.addAttribute("error", "Tous les champs étudiant sont obligatoires");
                return prepareSlider("candidat", "register", model);
            }
        } else if ("alumni".equalsIgnoreCase(type)) {
            if (form.getAnneeDiplome() == null) {
                model.addAttribute("error", "L'année de diplôme est obligatoire");
                return prepareSlider("candidat", "register", model);
            }
        }

        try {
            Map<String, String> infos = new HashMap<>();
            infos.put("id", String.valueOf(form.getId()));
            if ("etudiant".equalsIgnoreCase(type)) {
                infos.put("niveau", form.getNiveau());
                infos.put("filiere", form.getFiliere());
                infos.put("etablissement", form.getEtablissement());
            } else {
                infos.put("anneeDiplome", String.valueOf(form.getAnneeDiplome()));
                infos.put("posteActuel", form.getPosteActuel() == null ? "" : form.getPosteActuel());
                infos.put("entrepriseActuelle", form.getEntrepriseActuelle() == null ? "" : form.getEntrepriseActuelle());
            }

            Candidat c = authService.registerCandidat(
                    form.getNom(), form.getPrenom(), form.getEmail(),
                    form.getTelephone(), form.getMdp(), type, infos);
            currentUser.loginCandidat(c);
            return "redirect:/candidat/dashboard";

        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return prepareSlider("candidat", "register", model);
        }
    }

    // ===== REGISTER ENTREPRISE =====

    @GetMapping("/register/entreprise")
    public String showRegisterEntreprise(Model model) {
        return prepareSlider("entreprise", "register", model);
    }

    @PostMapping("/register/entreprise")
    public String doRegisterEntreprise(@Valid @ModelAttribute("registerForm") RegisterEntrepriseForm form,
                                       BindingResult br,
                                       Model model) {
        if (br.hasErrors()) {
            return prepareSlider("entreprise", "register", model);
        }
        try {
            Entreprise e = authService.registerEntreprise(
                    form.getNom(), form.getSecteur(), form.getAdresse(),
                    form.getEmail(), form.getTelephone(), form.getMdp());
            currentUser.loginEntreprise(e);
            return "redirect:/entreprise/dashboard";
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return prepareSlider("entreprise", "register", model);
        }
    }

    // ===== LOGOUT =====

    @PostMapping("/logout")
    public String logout(RedirectAttributes attrs) {
        currentUser.logout();
        attrs.addFlashAttribute("info", "Vous avez été déconnecté");
        return "redirect:/";
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
