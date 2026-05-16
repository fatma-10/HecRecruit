package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller des pages entreprise.
 * Toutes les routes commencent par /entreprise/
 *
 * <p>La résolution de l'entreprise depuis la session est centralisée dans
 * {@link #resoudreEntreprise(HttpSession)} afin d'éviter toute duplication.</p>
 *
 * <p>Convention de session :
 * <ul>
 *   <li>{@code entrepriseId} — Long (id de l'entreprise connectée)</li>
 *   <li>{@code userType}     — "entreprise"</li>
 * </ul>
 * </p>
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
    //  Utilitaire interne : résout l'entreprise ou retourne null
    // ----------------------------------------------------------------

    /**
     * Résout l'{@link Entreprise} associée à la session courante.
     *
     * <p>Lit {@code entrepriseId} (Long) dans la session puis délègue
     * au service. Retourne {@code null} si la session est vide ou si
     * l'entreprise n'existe plus en base.</p>
     *
     * @param session la session HTTP courante
     * @return l'{@link Entreprise} connectée, ou {@code null}
     */
    private Entreprise resoudreEntreprise(HttpSession session) {
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

    /**
     * Affiche le tableau de bord de l'entreprise connectée.
     *
     * <p>Correction BUG 2 : le calcul du nombre total de candidatures n'est
     * plus effectué via un {@code stream().mapToInt().sum()} dans le controller.
     * Il est délégué à {@link CandidatureService#getTotalCandidaturesParEntreprise(Long)},
     * qui encapsule cette logique de comptage dans la couche service.</p>
     *
     * <p>Injecte dans le modèle :
     * <ul>
     *   <li>{@code entreprise}      — l'entreprise connectée</li>
     *   <li>{@code nbOffres}        — nombre d'offres publiées</li>
     *   <li>{@code nbCandidatures}  — total des candidatures reçues toutes offres confondues</li>
     *   <li>{@code nbWishlist}      — nombre de candidats en wishlist</li>
     * </ul>
     * </p>
     *
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf
     * @return la vue "entreprise/dashboard" ou une redirection vers le login
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        model.addAttribute("entreprise", entreprise);
        model.addAttribute("nbOffres", entreprise.getOffresPubliees().size());
        model.addAttribute("nbCandidatures",
                candidatureService.getTotalCandidaturesParEntreprise(entreprise.getId()));
        model.addAttribute("nbWishlist", entreprise.getWishlist().size());
        return "entreprise/dashboard";
    }

    // ================================================================
    //  GESTION DES OFFRES
    // ================================================================

    /**
     * Affiche la liste des offres publiées par l'entreprise connectée.
     *
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf
     * @return la vue "entreprise/offres"
     */
    @GetMapping("/offres")
    public String mesOffres(HttpSession session, Model model) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        model.addAttribute("entreprise", entreprise);
        model.addAttribute("offres", entreprise.getOffresPubliees());
        return "entreprise/offres";
    }

    /**
     * Affiche le formulaire de création d'une nouvelle offre.
     *
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf (requis par Spring MVC)
     * @return la vue "entreprise/creer-offre"
     */
    @GetMapping("/offres/creer")
    public String creerOffreForm(HttpSession session, Model model) {
        if (resoudreEntreprise(session) == null) return "redirect:/login?type=entreprise";
        return "entreprise/creer-offre";
    }

    /**
     * Traite la soumission du formulaire de création d'offre.
     *
     * <p>Les paramètres spécifiques (domaine, rythme, sujet, technologies)
     * sont optionnels et dépendent du type d'offre sélectionné.</p>
     *
     * @param session      la session HTTP
     * @param titre        le titre de l'offre
     * @param description  la description de l'offre
     * @param type         le type : "stage", "alternance" ou "projet fin d'etudes"
     * @param domaine      domaine du stage (nullable)
     * @param duree        durée en mois pour stage ou alternance (nullable selon le type)
     * @param rythme       rythme de l'alternance (nullable)
     * @param sujet        sujet du PFE (nullable)
     * @param technologies technologies du PFE (nullable)
     * @param ra           les attributs de redirection pour les messages flash
     * @return redirection vers la liste des offres de l'entreprise
     */
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
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            Map<String, String> infos = new HashMap<>();
            switch (type.toLowerCase()) {
                case "stage" -> {
                    infos.put("domaine", domaine);
                    infos.put("duree", duree);
                }
                case "alternance" -> {
                    infos.put("rythme", rythme);
                    infos.put("duree", duree);
                }
                case "projet fin d'etudes" -> {
                    infos.put("sujet", sujet);
                    infos.put("technologies", technologies);
                }
            }
            offreService.creerOffre(titre, description, type, entreprise, infos);
            ra.addFlashAttribute("success", "Offre publiée avec succès !");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/offres";
    }

    /**
     * Supprime une offre appartenant à l'entreprise connectée.
     *
     * @param idOffre l'identifiant de l'offre à supprimer
     * @param session la session HTTP
     * @param ra      les attributs de redirection pour les messages flash
     * @return redirection vers la liste des offres
     */
    @PostMapping("/offres/supprimer/{idOffre}")
    public String supprimerOffre(@PathVariable Long idOffre,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Entreprise entreprise = resoudreEntreprise(session);
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

    /**
     * Affiche la liste des candidats ayant postulé à une offre donnée.
     *
     * @param idOffre l'identifiant de l'offre
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf
     * @return la vue "entreprise/candidats-offre"
     */
    @GetMapping("/offres/{idOffre}/candidats")
    public String candidatsOffre(@PathVariable Long idOffre,
                                 HttpSession session,
                                 Model model) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        Offre offre = offreService.getOffreById(idOffre);
        model.addAttribute("entreprise", entreprise);
        model.addAttribute("offre", offre);
        model.addAttribute("candidats", offre.getCandidatures());
        return "entreprise/candidats-offre";
    }

    /**
     * Retire la candidature d'un candidat d'une offre (action entreprise).
     *
     * @param idOffre    l'identifiant de l'offre
     * @param idCandidat le CIN du candidat à retirer
     * @param session    la session HTTP
     * @param ra         les attributs de redirection pour les messages flash
     * @return redirection vers la liste des candidats de l'offre
     */
    @PostMapping("/offres/{idOffre}/candidats/supprimer/{idCandidat}")
    public String supprimerCandidatureOffre(@PathVariable Long idOffre,
                                            @PathVariable int idCandidat,
                                            HttpSession session,
                                            RedirectAttributes ra) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            candidatureService.supprimerCandidatureOffre(idOffre, idCandidat, entreprise);
            ra.addFlashAttribute("success", "Candidature retirée.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/offres/" + idOffre + "/candidats";
    }

    /**
     * Ajoute un candidat à la wishlist de l'entreprise depuis la vue des candidats d'une offre.
     *
     * @param idOffre    l'identifiant de l'offre (utilisé pour la redirection)
     * @param idCandidat le CIN du candidat à ajouter
     * @param session    la session HTTP
     * @param ra         les attributs de redirection pour les messages flash
     * @return redirection vers la liste des candidats de l'offre
     */
    @PostMapping("/offres/{idOffre}/candidats/wishlist/{idCandidat}")
    public String ajouterWishlistDepuisOffre(@PathVariable Long idOffre,
                                             @PathVariable int idCandidat,
                                             HttpSession session,
                                             RedirectAttributes ra) {
        Entreprise entreprise = resoudreEntreprise(session);
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

    /**
     * Affiche la wishlist complète de l'entreprise connectée.
     *
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf
     * @return la vue "entreprise/wishlist"
     */
    @GetMapping("/wishlist")
    public String wishlist(HttpSession session, Model model) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        model.addAttribute("entreprise", entreprise);
        model.addAttribute("wishlist", entreprise.getWishlist());
        return "entreprise/wishlist";
    }

    /**
     * Retire un candidat de la wishlist de l'entreprise connectée.
     *
     * @param idCandidat le CIN du candidat à retirer
     * @param session    la session HTTP
     * @param ra         les attributs de redirection pour les messages flash
     * @return redirection vers la wishlist
     */
    @PostMapping("/wishlist/retirer/{idCandidat}")
    public String retirerWishlist(@PathVariable int idCandidat,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        Entreprise entreprise = resoudreEntreprise(session);
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

    /**
     * Affiche le profil de l'entreprise connectée avec le formulaire de modification.
     *
     * @param session la session HTTP
     * @param model   le modèle Thymeleaf
     * @return la vue "entreprise/profil"
     */
    @GetMapping("/profil")
    public String profil(HttpSession session, Model model) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        model.addAttribute("entreprise", entreprise);
        return "entreprise/profil";
    }

    /**
     * Applique les modifications de profil soumises par l'entreprise connectée.
     *
     * @param session   la session HTTP
     * @param secteur   le nouveau secteur d'activité
     * @param adresse   la nouvelle adresse
     * @param telephone le nouveau numéro de téléphone
     * @param ra        les attributs de redirection pour les messages flash
     * @return redirection vers la page de profil entreprise
     */
    @PostMapping("/profil/modifier")
    public String modifierProfil(HttpSession session,
                                 @RequestParam String secteur,
                                 @RequestParam String adresse,
                                 @RequestParam String telephone,
                                 RedirectAttributes ra) {
        Entreprise entreprise = resoudreEntreprise(session);
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