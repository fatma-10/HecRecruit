package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller des pages candidat.
 * Toutes les routes commencent par /candidat/
 * Chaque méthode vérifie que la session contient "candidatId".
 */
@Controller
@RequestMapping("/candidat")
public class CandidatController {

    private final CandidatService candidatService;
    private final CandidatureService candidatureService;
    private final OffreService offreService;
    private final RecommendationService recommendationService;

    public CandidatController(CandidatService candidatService,
                              CandidatureService candidatureService,
                              OffreService offreService,
                              RecommendationService  recommendationService) {
        this.candidatService = candidatService;
        this.candidatureService = candidatureService;
        this.offreService = offreService;
        this.recommendationService = recommendationService;
    }

    // ----------------------------------------------------------------
    //  Méthode utilitaire : récupère le candidat depuis la session
    // ----------------------------------------------------------------

    private Candidat getCandidatFromSession(HttpSession session) {
        Integer id = (Integer) session.getAttribute("candidatId");
        if (id == null) return null;
        try {
            return candidatService.getCandidatById(id);
        } catch (Exception e) {
            return null;
        }
    }

    // ================================================================
    //  DASHBOARD
    // ================================================================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Candidat candidat = getCandidatFromSession(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        model.addAttribute("candidat", candidat);
        model.addAttribute("nbCandidatures", candidat.getCandidaturesEnCours().size());
        model.addAttribute("nbOffresDisponibles", offreService.getOffresDisponibles().size());

        // 5 dernières offres disponibles
        List<Offre> offres = offreService.getOffresDisponibles();
        int debut = Math.max(0, offres.size() - 5);
        model.addAttribute("dernieresOffres", offres.subList(debut, offres.size()));

        // Recommandations (top 5)
        if (candidat instanceof Etudiant) {
            model.addAttribute("recommandations",
                    recommendationService.getRecommandationsEtudiant((Etudiant) candidat, 5));
        } else if (candidat instanceof Alumni) {
            model.addAttribute("recommandations",
                    recommendationService.getRecommandationsAlumni((Alumni) candidat, 5));
        }

        return "candidat/dashboard";
    }

    // ================================================================
    //  LISTE DES OFFRES + POSTULER
    // ================================================================

    @GetMapping("/offres")
    public String offres(HttpSession session,
                         @RequestParam(required = false) String critere,
                         @RequestParam(required = false) String valeur,
                         Model model) {
        Candidat candidat = getCandidatFromSession(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        List<Offre> offres;
        if (critere != null && valeur != null && !valeur.isBlank()) {
            offres = offreService.rechercherOffres(critere, valeur);
            model.addAttribute("critere", critere);
            model.addAttribute("valeur", valeur);
        } else {
            offres = offreService.getOffresDisponibles();
        }

        model.addAttribute("candidat", candidat);
        model.addAttribute("offres", offres);
        return "candidat/offres";
    }

    @PostMapping("/offres/postuler/{idOffre}")
    public String postuler(@PathVariable Long idOffre,
                           HttpSession session,
                           RedirectAttributes ra) {
        Candidat candidat = getCandidatFromSession(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        try {
            candidatureService.postulerOffre(candidat.getId(), idOffre);
            ra.addFlashAttribute("success", "Candidature envoyée avec succès !");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/candidat/offres";
    }

    // ================================================================
    //  MES CANDIDATURES
    // ================================================================

    @GetMapping("/candidatures")
    public String mesCandidatures(HttpSession session, Model model) {
        Candidat candidat = getCandidatFromSession(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        model.addAttribute("candidat", candidat);
        model.addAttribute("candidatures", candidat.getCandidaturesEnCours());
        return "candidat/candidatures";
    }

    @PostMapping("/candidatures/retirer/{idOffre}")
    public String retirerCandidature(@PathVariable Long idOffre,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        Candidat candidat = getCandidatFromSession(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        try {
            candidatureService.retirerCandidature(candidat.getId(), idOffre);
            ra.addFlashAttribute("success", "Candidature retirée.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/candidat/candidatures";
    }


    // ================================================================
    //  PROFIL
    // ================================================================

    @GetMapping("/profil")
    public String profil(HttpSession session, Model model) {
        Candidat candidat = getCandidatFromSession(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        model.addAttribute("candidat", candidat);
        return "candidat/profil";
    }

    @PostMapping("/profil/modifier")
    public String modifierProfil(HttpSession session,
                                 @RequestParam Map<String, String> params,
                                 RedirectAttributes ra) {
        Candidat candidat = getCandidatFromSession(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        try {
            // On retire le token CSRF si présent, il ne doit pas passer dans le service
            params.remove("_csrf");
            candidatService.modifierProfil(candidat.getId(), params);
            ra.addFlashAttribute("success", "Profil mis à jour avec succès !");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/candidat/profil";
    }
}