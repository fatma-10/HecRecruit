package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.dto.LoginForm;
import com.IHEC.Recruit.dto.RegisterCandidatForm;
import com.IHEC.Recruit.dto.RegisterEntrepriseForm;
import com.IHEC.Recruit.entity.Candidat;
import com.IHEC.Recruit.entity.Entreprise;
import com.IHEC.Recruit.exception.BusinessException;
import com.IHEC.Recruit.security.CurrentUser;
import com.IHEC.Recruit.service.AuthService;
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

    // ===== LOGIN =====

    @GetMapping("/login/{userType}")
    public String showLogin(@PathVariable String userType, Model model) {
        if (!"candidat".equals(userType) && !"entreprise".equals(userType)) {
            return "redirect:/";
        }
        model.addAttribute("userType", userType);
        model.addAttribute("loginForm", new LoginForm());
        return "auth/login";
    }

    @PostMapping("/login/{userType}")
    public String doLogin(@PathVariable String userType,
                          @Valid @ModelAttribute("loginForm") LoginForm form,
                          BindingResult br,
                          Model model) {
        model.addAttribute("userType", userType);
        if (br.hasErrors()) return "auth/login";

        if ("candidat".equals(userType)) {
            Candidat c = authService.loginCandidat(form.getEmail(), form.getMdp());
            if (c == null) {
                model.addAttribute("error", "Email ou mot de passe incorrect");
                return "auth/login";
            }
            currentUser.loginCandidat(c);
            return "redirect:/candidat/dashboard";
        }

        if ("entreprise".equals(userType)) {
            Entreprise e = authService.loginEntreprise(form.getEmail(), form.getMdp());
            if (e == null) {
                model.addAttribute("error", "Email ou mot de passe incorrect");
                return "auth/login";
            }
            currentUser.loginEntreprise(e);
            return "redirect:/entreprise/dashboard";
        }

        return "redirect:/";
    }

    // ===== REGISTER =====

    @GetMapping("/register/candidat")
    public String showRegisterCandidat(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterCandidatForm());
        }
        return "auth/register-candidat";
    }

    @PostMapping("/register/candidat")
    public String doRegisterCandidat(@Valid @ModelAttribute("registerForm") RegisterCandidatForm form,
                                     BindingResult br,
                                     Model model) {
        if (br.hasErrors()) return "auth/register-candidat";

        // Validation conditionnelle selon le type
        String type = form.getTypeCandidat();
        if ("etudiant".equalsIgnoreCase(type)) {
            if (isBlank(form.getNiveau()) || isBlank(form.getFiliere()) || isBlank(form.getEtablissement())) {
                model.addAttribute("error", "Tous les champs étudiant sont obligatoires");
                return "auth/register-candidat";
            }
        } else if ("alumni".equalsIgnoreCase(type)) {
            if (form.getAnneeDiplome() == null) {
                model.addAttribute("error", "L'année de diplôme est obligatoire");
                return "auth/register-candidat";
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
            return "auth/register-candidat";
        }
    }

    @GetMapping("/register/entreprise")
    public String showRegisterEntreprise(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterEntrepriseForm());
        }
        return "auth/register-entreprise";
    }

    @PostMapping("/register/entreprise")
    public String doRegisterEntreprise(@Valid @ModelAttribute("registerForm") RegisterEntrepriseForm form,
                                       BindingResult br,
                                       Model model) {
        if (br.hasErrors()) return "auth/register-entreprise";
        try {
            Entreprise e = authService.registerEntreprise(
                    form.getNom(), form.getSecteur(), form.getAdresse(),
                    form.getEmail(), form.getTelephone(), form.getMdp());
            currentUser.loginEntreprise(e);
            return "redirect:/entreprise/dashboard";
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/register-entreprise";
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
