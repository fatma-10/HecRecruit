package com.hecrecruit.controller;

import com.hecrecruit.model.Candidat;
import com.hecrecruit.service.CandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/candidats")
public class CandidatController {

    @Autowired
    private CandidatService candidatService;

    @GetMapping
    public String listCandidats(Model model) {
        List<Candidat> candidats = candidatService.getTousCandidats();
        model.addAttribute("candidats", candidats);
        return "candidats/liste";
    }

    @GetMapping("/{id}")
    public String viewCandidat(@PathVariable Integer id, Model model) {
        Optional<Candidat> candidat = candidatService.getCandidatById(id);
        if (candidat.isPresent()) {
            model.addAttribute("candidat", candidat.get());
            return "candidats/detail";
        }
        return "redirect:/candidats";
    }
}
