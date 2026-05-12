package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.entity.Alumni;
import com.IHEC.Recruit.entity.Candidat;
import com.IHEC.Recruit.entity.Etudiant;
import com.IHEC.Recruit.security.CurrentUser;
import com.IHEC.Recruit.service.CandidatService;
import com.IHEC.Recruit.service.CandidatureService;
import com.IHEC.Recruit.service.RecommendationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/candidat")
public class CandidatController {

    private final CurrentUser currentUser;
    private final CandidatService candidatService;
    private final CandidatureService candidatureService;
    private final RecommendationService recommendationService;

    public CandidatController(CurrentUser currentUser,
                              CandidatService candidatService,
                              CandidatureService candidatureService,
                              RecommendationService recommendationService) {
        this.currentUser = currentUser;
        this.candidatService = candidatService;
        this.candidatureService = candidatureService;
        this.recommendationService = recommendationService;
    }

    // ===== DASHBOARD =====

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Candidat me = currentUser.getCandidat();
        // Reload to ensure latest state
        me = candidatService.getCandidatById(me.getId());

        model.addAttribute("candidat", me);
        if (me instanceof Etudiant e) {
            model.addAttribute("recommandations",
                    recommendationService.getRecommandationsEtudiant(e, 6));
        } else if (me instanceof Alumni a) {
            model.addAttribute("recommandations",
                    recommendationService.getRecommandationsAlumni(a, 6));
        }
        model.addAttribute("nbCandidatures", me.getCandidaturesEnCours().size());
        return "candidat/dashboard";
    }

    // ===== MES CANDIDATURES =====

    @GetMapping("/mes-candidatures")
    public String mesCandidatures(Model model) {
        Candidat me = currentUser.getCandidat();
        model.addAttribute("candidatures",
                candidatureService.getCandidaturesCandidat(me.getId()));
        return "candidat/mes-candidatures";
    }

    @PostMapping("/candidatures/{offreId}/retirer")
    public String retirerCandidature(@PathVariable Long offreId, RedirectAttributes attrs) {
        Candidat me = currentUser.getCandidat();
        candidatureService.retirerCandidature(me.getId(), offreId);
        attrs.addFlashAttribute("success", "Candidature retirée");
        return "redirect:/candidat/mes-candidatures";
    }

    // ===== PROFIL =====

    @GetMapping("/profil")
    public String profil(Model model) {
        Candidat me = candidatService.getCandidatById(currentUser.getCandidat().getId());
        model.addAttribute("candidat", me);
        model.addAttribute("isEtudiant", me instanceof Etudiant);
        model.addAttribute("isAlumni", me instanceof Alumni);
        return "candidat/profil";
    }

    @PostMapping("/profil")
    public String updateProfil(@RequestParam Map<String, String> params,
                                RedirectAttributes attrs) {
        Candidat me = currentUser.getCandidat();
        Map<String, String> infos = new HashMap<>(params);
        Candidat updated = candidatService.modifierProfil(me.getId(), infos);
        currentUser.loginCandidat(updated);
        attrs.addFlashAttribute("success", "Profil mis à jour");
        return "redirect:/candidat/profil";
    }
}
