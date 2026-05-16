package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.service.CandidatService;
import com.IHEC.Recruit.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller des recommandations personnalisées.
 *
 * <p>La résolution du candidat depuis la session est centralisée dans
 * {@link #resoudreCandidat(HttpSession)}, alignée sur la convention des
 * autres controllers du projet.</p>
 */
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
    //  RECOMMANDATIONS
    // ================================================================

    /**
     * Affiche les recommandations personnalisées pour le candidat connecté.
     *
     * <p>Retourne au maximum 10 recommandations, calculées selon le profil :
     * <ul>
     *   <li>{@link Etudiant} — score basé sur filière, niveau, nouveauté, popularité et secteur</li>
     *   <li>{@link Alumni}   — score basé sur le type d'offre et le secteur de l'entreprise</li>
     * </ul>
     * </p>
     *
     * <p>Injecte dans le modèle :
     * <ul>
     *   <li>{@code candidat}        — le candidat connecté</li>
     *   <li>{@code recommandations} — liste de {@link RecommendationService.OffreRecommandee}</li>
     * </ul>
     * </p>
     *
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf
     * @return la vue "recommandations/list" ou une redirection vers le login
     */
    @GetMapping
    public String afficherRecommandations(HttpSession session, Model model) {
        Candidat candidat = resoudreCandidat(session);
        if (candidat == null) return "redirect:/login?type=candidat";

        model.addAttribute("candidat", candidat);

        if (candidat instanceof Etudiant etudiant) {
            model.addAttribute("recommandations",
                    recommendationService.getRecommandationsEtudiant(etudiant, 10));
        } else if (candidat instanceof Alumni alumni) {
            model.addAttribute("recommandations",
                    recommendationService.getRecommandationsAlumni(alumni, 10));
        }

        return "recommandations/list";
    }
}