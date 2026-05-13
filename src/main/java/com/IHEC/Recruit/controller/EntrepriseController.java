package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller des pages entreprise.
 * Toutes les routes commencent par /entreprise/
 */
@Controller
@RequestMapping("/entreprise")
public class EntrepriseController {

    private final EntrepriseService entrepriseService;
    private final OffreService offreService;
    private final CandidatureService candidatureService;

    public EntrepriseController(EntrepriseService entrepriseService,
                                OffreService offreService,
                                CandidatureService candidatureService) {
        this.entrepriseService = entrepriseService;
        this.offreService = offreService;
        this.candidatureService = candidatureService;
    }

    // ----------------------------------------------------------------
    //  Utilitaire session
    // ----------------------------------------------------------------

    private Entreprise getEntrepriseFromSession(HttpSession session) {
        Long id = (Long) session.getAttribute("entrepriseId");
        if (id == null) return null;
        try {
            return entrepriseService.getEntrepriseById(id);
        } catch (Exception e) {
            return null;
        }
    }

    // ================================================================
    //  DASHBOARD
    // ================================================================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        // Calcul du nombre total de candidatures
        int totalCandidatures = entreprise.getOffresPubliees().stream()
                .mapToInt(o -> o.getCandidatures().size())
                .sum();

        model.addAttribute("entreprise", entreprise);
        model.addAttribute("nbOffres", entreprise.getOffresPubliees().size());
        model.addAttribute("nbCandidatures", totalCandidatures);
        model.addAttribute("nbWishlist", entreprise.getWishlist().size());
        return "entreprise/dashboard";
    }

    // ================================================================
    //  GESTION DES OFFRES
    // ================================================================

    @GetMapping("/offres")
    public String mesOffres(HttpSession session, Model model) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        model.addAttribute("entreprise", entreprise);
        model.addAttribute("offres", entreprise.getOffresPubliees());
        return "entreprise/offres";
    }

    @GetMapping("/offres/creer")
    public String creerOffreForm(HttpSession session, Model model) {
        if (getEntrepriseFromSession(session) == null) return "redirect:/login?type=entreprise";
        return "entreprise/creer-offre";
    }

    @PostMapping("/offres/creer")
    public String creerOffre(HttpSession session,
                             @RequestParam String titre,
                             @RequestParam String description,
                             @RequestParam String type,
                             @RequestParam(required = false) String domaine,
                             @RequestParam(required = false) String duree,
                             @RequestParam(required = false) String rythme,
                             @RequestParam(required = false) String sujet,
                             @RequestParam(required = false) String technologies,
                             RedirectAttributes ra) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            Map<String, String> infos = new HashMap<>();
            switch (type.toLowerCase()) {
                case "stage":
                    infos.put("domaine", domaine);
                    infos.put("duree", duree);
                    break;
                case "alternance":
                    infos.put("rythme", rythme);
                    infos.put("duree", duree);
                    break;
                case "projet fin d'etudes":
                    infos.put("sujet", sujet);
                    infos.put("technologies", technologies);
                    break;
            }

            offreService.creerOffre(titre, description, type, entreprise, infos);
            ra.addFlashAttribute("success", "Offre publiée avec succès !");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/offres";
    }

    @PostMapping("/offres/supprimer/{idOffre}")
    public String supprimerOffre(@PathVariable Long idOffre,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            offreService.supprimerOffre(idOffre, entreprise);
            ra.addFlashAttribute("success", "Offre supprimée.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/offres";
    }

    // ================================================================
    //  CANDIDATS D'UNE OFFRE
    // ================================================================

    @GetMapping("/offres/{idOffre}/candidats")
    public String candidatsOffre(@PathVariable Long idOffre,
                                 HttpSession session,
                                 Model model) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        Offre offre = offreService.getOffreById(idOffre);
        model.addAttribute("entreprise", entreprise);
        model.addAttribute("offre", offre);
        model.addAttribute("candidats", offre.getCandidatures());
        return "entreprise/candidats-offre";
    }

    /** L'entreprise retire la candidature d'un candidat */
    @PostMapping("/offres/{idOffre}/candidats/supprimer/{idCandidat}")
    public String supprimerCandidatureOffre(@PathVariable Long idOffre,
                                            @PathVariable int idCandidat,
                                            HttpSession session,
                                            RedirectAttributes ra) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            candidatureService.supprimerCandidatureOffre(idOffre, idCandidat, entreprise);
            ra.addFlashAttribute("success", "Candidature retirée.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/offres/" + idOffre + "/candidats";
    }

    /** L'entreprise ajoute un candidat à sa wishlist depuis la liste des candidats */
    @PostMapping("/offres/{idOffre}/candidats/wishlist/{idCandidat}")
    public String ajouterWishlistDepuisOffre(@PathVariable Long idOffre,
                                             @PathVariable int idCandidat,
                                             HttpSession session,
                                             RedirectAttributes ra) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            candidatureService.ajouterWishlist(entreprise.getId(), idCandidat);
            ra.addFlashAttribute("success", "Candidat ajouté à la wishlist !");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/offres/" + idOffre + "/candidats";
    }

    // ================================================================
    //  WISHLIST
    // ================================================================

    @GetMapping("/wishlist")
    public String wishlist(HttpSession session, Model model) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        model.addAttribute("entreprise", entreprise);
        model.addAttribute("wishlist", entreprise.getWishlist());
        return "entreprise/wishlist";
    }

    @PostMapping("/wishlist/retirer/{idCandidat}")
    public String retirerWishlist(@PathVariable int idCandidat,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            candidatureService.retirerWishlist(entreprise.getId(), idCandidat);
            ra.addFlashAttribute("success", "Candidat retiré de la wishlist.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/wishlist";
    }

    // ================================================================
    //  PROFIL
    // ================================================================

    @GetMapping("/profil")
    public String profil(HttpSession session, Model model) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        model.addAttribute("entreprise", entreprise);
        return "entreprise/profil";
    }

    @PostMapping("/profil/modifier")
    public String modifierProfil(HttpSession session,
                                 @RequestParam String secteur,
                                 @RequestParam String adresse,
                                 @RequestParam String telephone,
                                 RedirectAttributes ra) {
        Entreprise entreprise = getEntrepriseFromSession(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            Map<String, String> infos = new HashMap<>();
            infos.put("secteur", secteur);
            infos.put("adresse", adresse);
            infos.put("telephone", telephone);
            entrepriseService.modifierProfil(entreprise.getId(), infos);
            ra.addFlashAttribute("success", "Profil mis à jour !");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/profil";
    }
}