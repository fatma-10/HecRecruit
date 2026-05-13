package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.service.CandidatService;
import com.IHEC.Recruit.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recommandations")
public class RecommandationController {

    private final RecommendationService recommendationService;
    private final CandidatService candidatService;

    public RecommandationController(RecommendationService recommendationService,
                                    CandidatService candidatService) {
        this.recommendationService = recommendationService;
        this.candidatService = candidatService;
    }

    @GetMapping
    public String afficherRecommandations(HttpSession session, Model model) {
        Integer idCandidat = (Integer) session.getAttribute("candidatId");
        if (idCandidat == null) {
            return "redirect:/login?type=candidat";
        }
        Candidat candidat = candidatService.getCandidatById(idCandidat);
        model.addAttribute("candidat", candidat);

        if (candidat instanceof Etudiant) {
            model.addAttribute("recommandations",
                    recommendationService.getRecommandationsEtudiant((Etudiant) candidat, 10));
        } else if (candidat instanceof Alumni) {
            model.addAttribute("recommandations",
                    recommendationService.getRecommandationsAlumni((Alumni) candidat, 10));
        }
        return "recommandations/list";
    }
}