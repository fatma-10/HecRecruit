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

    public CandidatController(CandidatService candidatService,
                               CandidatureService candidatureService,
                               OffreService offreService,
                               RecommendationService recommendationService) {
        this.candidatService = candidatService;
        this.candidatureService = candidatureService;
        this.offreService = offreService;
        this.recommendationService = recommendationService;
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
     * <p>Correction BUG 1 : {@code getOffresDisponibles()} n'est plus appelée
     * deux fois. Le comptage est délégué à {@link OffreService#getOffresDisponiblesCount()}
     * et la sous-liste des dernières offres à {@link OffreService#getDernieresOffres(int)},
     * supprimant ainsi toute manipulation de {@code subList} dans la couche présentation.</p>
     *
     * <p>Injecte dans le modèle :
     * <ul>
     *   <li>{@code candidat}           — le candidat connecté</li>
     *   <li>{@code nbCandidatures}      — nombre de candidatures en cours</li>
     *   <li>{@code nbOffresDisponibles} — nombre total d'offres actives</li>
     *   <li>{@code dernieresOffres}     — les 5 offres les plus récentes</li>
     *   <li>{@code recommandations}     — top 5 recommandations personnalisées</li>
     * </ul>
     * </p>
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
        model.addAttribute("nbCandidatures", candidat.getCandidaturesEnCours().size());
        model.addAttribute("nbOffresDisponibles", offreService.getOffresDisponiblesCount());
        model.addAttribute("dernieresOffres", offreService.getDernieresOffres(5));

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
     * @param critere le critère de recherche (titre, type, domaine, rythme, technologies) ; peut être null
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
        return "candidat/offres";
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
        model.addAttribute("candidatures", candidat.getCandidaturesEnCours());
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
     * <p>Le token CSRF est retiré de la map avant de la transmettre au service,
     * conformément à la convention de l'application.</p>
     *
     * @param session la session HTTP
     * @param params  tous les paramètres du formulaire (téléphone, niveau, filière, etc.)
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