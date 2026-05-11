package com.hecrecruit.controller;

import com.hecrecruit.model.Entreprise;
import com.hecrecruit.service.EntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/entreprises")
public class EntrepriseController {

    @Autowired
    private EntrepriseService entrepriseService;

    @GetMapping
    public String listEntreprises(Model model) {
        List<Entreprise> entreprises = entrepriseService.getToutesEntreprises();
        model.addAttribute("entreprises", entreprises);
        return "entreprises/liste";
    }

    @GetMapping("/{id}")
    public String viewEntreprise(@PathVariable UUID id, Model model) {
        Optional<Entreprise> entreprise = entrepriseService.getEntrepriseById(id);
        if (entreprise.isPresent()) {
            model.addAttribute("entreprise", entreprise.get());
            return "entreprises/detail";
        }
        return "redirect:/entreprises";
    }
}
