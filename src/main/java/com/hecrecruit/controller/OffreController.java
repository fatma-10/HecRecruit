package com.hecrecruit.controller;

import com.hecrecruit.model.Offre;
import com.hecrecruit.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/offres")
public class OffreController {

    @Autowired
    private OffreService offreService;

    @GetMapping
    public String listOffres(@RequestParam(required = false) String type, Model model) {
        List<Offre> offres;
        if (type != null && !type.isEmpty()) {
            offres = offreService.filterOffresByType(type);
        } else {
            offres = offreService.getActiveOffers();
        }
        model.addAttribute("offres", offres);
        model.addAttribute("typeFilter", type);
        return "offres/liste";
    }

    @GetMapping("/{id}")
    public String viewOffre(@PathVariable UUID id, Model model) {
        Optional<Offre> offre = offreService.getOffreById(id);
        if (offre.isPresent()) {
            model.addAttribute("offre", offre.get());
            return "offres/detail";
        }
        return "redirect:/offres";
    }
}
