package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Controller des pages candidat.
 * Toutes les routes commencent par /candidat/
 *
 * <p>La résolution du candidat depuis la session est centralisée dans
 * {@link #resoudreCandidat(HttpSession)} afin d'éviter toute duplication.</p>
 *
 * <p>Convention de session :
 * <ul>
 *   <li>{@code candidatId} — int (CIN du candidat connecté)</li>
 *   <li>{@code userType}   — "candidat"</li>
 * </ul>
 * </p>
 */
@Controller
@RequestMapping("/candidat")
public class CandidatController {

    private final CandidatService candidatService;
    private final CandidatureService candidatureService;
    private final OffreService offreService;
    private final RecommendationService recommendationService;
    private final ScoringService scoringService;

    public CandidatController(CandidatService candidatService,
                               CandidatureService candidatureService,
                               OffreService offreService,
                               RecommendationService recommendationService,
                               ScoringService scoringService) {
        this.candidatService = candidatService;
        this.candidatureService = candidatureService;
        this.offreService = offreService;
        this.recommendationService = recommendationService;
        this.scoringService = scoringService;
    }

    // ----------------------------------------------------------------
    //  Utilitaire interne : résout le candidat ou retourne null
    // ----------------------------------------------------------------

    /**
     * Résout le {@link Candidat} associé à la session courante.
     *
     * <p>Lit {@code candidatId} (Integer / CIN) dans la session puis
     * délègue au service. Retourne {@code null} si la session est vide
     * ou si le candidat n'existe plus en base.</p>
     *
     * @param session la session HTTP courante
     * @return le {@link Candidat} connecté, ou {@code null}
     */
    private Candidat resoudreCandidat(HttpSession session) {
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

    /**
     * Affiche le tableau de bord du candidat connecté.
     *
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf
     * @return la vue "candidat/dashboard" ou une redirection vers le login
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Candidat candidat = resoudreCandidat(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        model.addAttribute("candidat", candidat);
        model.addAttribute("nbCandidatures",
                candidatureService.getNombreCandidaturesCandidat(candidat.getId()));
        model.addAttribute("nbOffresDisponibles", offreService.getOffresDisponiblesCount());
        List<Offre> dernieresOffres = offreService.getDernieresOffres(5);
        model.addAttribute("dernieresOffres", dernieresOffres);
        model.addAttribute("scoresCompatibilite",
                scoringService.calculerScoresPourOffres(candidat, dernieresOffres));

        if (candidat instanceof Etudiant etudiant) {
            model.addAttribute("recommandations",
                    recommendationService.getRecommandationsEtudiant(etudiant, 5));
        } else if (candidat instanceof Alumni alumni) {
            model.addAttribute("recommandations",
                    recommendationService.getRecommandationsAlumni(alumni, 5));
        }

        return "candidat/dashboard";
    }

    // ================================================================
    //  LISTE DES OFFRES + POSTULER
    // ================================================================

    /**
     * Affiche la liste des offres disponibles, avec filtrage optionnel.
     *
     * @param session la session HTTP
     * @param critere le critère de recherche ; peut être null
     * @param valeur  la valeur recherchée ; peut être null ou vide
     * @param model   le modèle Thymeleaf
     * @return la vue "candidat/offres"
     */
    @GetMapping("/offres")
    public String offres(HttpSession session,
                         @RequestParam(required = false) String critere,
                         @RequestParam(required = false) String valeur,
                         Model model) {
        Candidat candidat = resoudreCandidat(session);
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
        model.addAttribute("scoresCompatibilite",
                scoringService.calculerScoresPourOffres(candidat, offres));
        return "candidat/offres";
    }

    // ----------------------------------------------------------------
    //  BUG #3 — Détail d'une offre
    // ----------------------------------------------------------------

    /**
     * Affiche le détail d'une offre.
     *
     * @param id      l'identifiant de l'offre
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf
     * @return la vue "candidat/offre-detail" ou une redirection vers le login
     */
    @GetMapping("/offres/{id}")
    public String detailOffre(@PathVariable Long id,
                              HttpSession session,
                              Model model) {
        Candidat candidat = resoudreCandidat(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        try {
            Offre offre = offreService.getOffreById(id);
            model.addAttribute("candidat", candidat);
            model.addAttribute("offre", offre);
            model.addAttribute("scoreCompatibilite",
                    scoringService.calculerScoreCompatibilite(candidat, offre));
        } catch (Exception ex) {
            return "redirect:/candidat/offres";
        }
        return "candidat/offre-detail";
    }

    /**
     * Soumet une candidature du candidat connecté pour l'offre donnée.
     *
     * @param idOffre l'identifiant de l'offre visée
     * @param session la session HTTP
     * @param ra      les attributs de redirection pour les messages flash
     * @return redirection vers la liste des offres
     */
    @PostMapping("/offres/postuler/{idOffre}")
    public String postuler(@PathVariable Long idOffre,
                           HttpSession session,
                           RedirectAttributes ra) {
        Candidat candidat = resoudreCandidat(session);
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

    /**
     * Affiche toutes les candidatures en cours du candidat connecté.
     *
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf
     * @return la vue "candidat/candidatures"
     */
    @GetMapping("/candidatures")
    public String mesCandidatures(HttpSession session, Model model) {
        Candidat candidat = resoudreCandidat(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        model.addAttribute("candidat", candidat);
        model.addAttribute("candidatures",
                candidatureService.getCandidaturesDetailsCandidat(candidat.getId()));
        return "candidat/candidatures";
    }

    /**
     * Retire la candidature du candidat connecté pour l'offre donnée.
     *
     * @param idOffre l'identifiant de l'offre concernée
     * @param session la session HTTP
     * @param ra      les attributs de redirection pour les messages flash
     * @return redirection vers la liste des candidatures
     */
    @PostMapping("/candidatures/retirer/{idOffre}")
    public String retirerCandidature(@PathVariable Long idOffre,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        Candidat candidat = resoudreCandidat(session);
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

    /**
     * Affiche le profil du candidat connecté.
     *
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf
     * @return la vue "candidat/profil"
     */
    @GetMapping("/profil")
    public String profil(HttpSession session, Model model) {
        Candidat candidat = resoudreCandidat(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        model.addAttribute("candidat", candidat);
        return "candidat/profil";
    }

    /**
     * Applique les modifications de profil soumises par le candidat connecté.
     *
     * @param session la session HTTP
     * @param params  tous les paramètres du formulaire
     * @param ra      les attributs de redirection pour les messages flash
     * @return redirection vers la page de profil
     */
    @PostMapping("/profil/modifier")
    public String modifierProfil(HttpSession session,
                                 @RequestParam Map<String, String> params,
                                 RedirectAttributes ra) {
        Candidat candidat = resoudreCandidat(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        try {
            params.remove("_csrf");
            candidatService.modifierProfil(candidat.getId(), params);
            ra.addFlashAttribute("success", "Profil mis à jour avec succès !");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/candidat/profil";
    }
}
