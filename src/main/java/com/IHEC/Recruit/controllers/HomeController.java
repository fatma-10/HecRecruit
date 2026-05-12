package com.IHEC.Recruit.controllers;

import com.IHEC.Recruit.security.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    private final CurrentUser currentUser;

    public HomeController(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @GetMapping("/")
    public String welcome() {
        if (currentUser.isCandidat()) return "redirect:/candidat/dashboard";
        if (currentUser.isEntreprise()) return "redirect:/entreprise/dashboard";
        return "home/index";
    }

    /** Page de choix du type d'utilisateur, en mode "login" ou "register". */
    @GetMapping("/type-selection/{mode}")
    public String typeSelection(@PathVariable String mode, Model model) {
        if (!"login".equals(mode) && !"register".equals(mode)) {
            return "redirect:/";
        }
        model.addAttribute("mode", mode);
        return "home/type-selection";
    }
}
