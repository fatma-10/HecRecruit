package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/entreprise")
public class EntrepriseController {

    private final EntrepriseService entrepriseService;
    private final OffreService offreService;
    private final CandidatureService candidatureService;
    private final ScoringService scoringService;

    public EntrepriseController(EntrepriseService entrepriseService,
                                OffreService offreService,
                                CandidatureService candidatureService,
                                ScoringService scoringService) {
        this.entrepriseService = entrepriseService;
        this.offreService = offreService;
        this.candidatureService = candidatureService;
        this.scoringService = scoringService;
    }

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

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        model.addAttribute("entreprise", entreprise);
        model.addAttribute("nbOffres", offreService.getNombreOffresEntreprise(entreprise));
        model.addAttribute("nbCandidatures",
                candidatureService.getTotalCandidaturesParEntreprise(entreprise.getId()));
        model.addAttribute("nbWishlist", candidatureService.getNombreWishlist(entreprise.getId()));
        return "entreprise/dashboard";
    }

    // ================================================================
    //  GESTION DES OFFRES
    // ================================================================

    @GetMapping("/offres")
    public String mesOffres(HttpSession session, Model model) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        model.addAttribute("entreprise", entreprise);
        model.addAttribute("offres", offreService.getOffresEntreprise(entreprise));
        return "entreprise/offres";
    }

    @GetMapping("/offres/creer")
    public String creerOffreForm(HttpSession session, Model model) {
        if (resoudreEntreprise(session) == null) return "redirect:/login?type=entreprise";
        return "entreprise/creer-offre";
    }

    /**
     * Traite la soumission du formulaire de création d'offre.
     *
     * <p>La date est validée AVANT toute persistance (Bug #6).
     * {@code @Transactional} garantit le rollback si {@code setDateExpiration} échoue
     * après la création de l'offre.</p>
     */
    @Transactional
    @PostMapping("/offres/creer")
    public String creerOffre(HttpSession session,
                             @RequestParam String titre,
                             @RequestParam String description,
                             @RequestParam String type,
                             @RequestParam(required = false) String domaine,
                             @RequestParam(required = false) String dureeStage,
                             @RequestParam(required = false) String dureeAlternance,
                             @RequestParam(required = false) String rythme,
                             @RequestParam(required = false) String sujet,
                             @RequestParam(required = false) String technologies,
                             @RequestParam(required = false) String dateExpiration,
                             RedirectAttributes ra) {

        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            // Validation de la date AVANT toute persistance
            LocalDate dateParsee = null;
            if (dateExpiration != null && !dateExpiration.isBlank()) {
                try {
                    dateParsee = LocalDate.parse(dateExpiration);
                    if (!dateParsee.isAfter(LocalDate.now())) {
                        ra.addFlashAttribute("error",
                                "La date d'expiration doit être strictement postérieure à aujourd'hui.");
                        return "redirect:/entreprise/offres/creer";
                    }
                } catch (Exception ex) {
                    ra.addFlashAttribute("error",
                            "Format de date invalide : " + dateExpiration);
                    return "redirect:/entreprise/offres/creer";
                }
            }

            Map<String, String> infos = new HashMap<>();
            switch (type.toLowerCase()) {
                case "stage" -> {
                    infos.put("domaine", domaine);
                    infos.put("duree", dureeStage);
                }
                case "alternance" -> {
                    infos.put("rythme", rythme);
                    infos.put("duree", dureeAlternance);
                }
                case "projet fin d'etudes" -> {
                    infos.put("sujet", sujet);
                    infos.put("technologies", technologies);
                }
            }

            Offre offre = offreService.creerOffre(titre, description, type, entreprise, infos);

            if (dateParsee != null) {
                offreService.setDateExpiration(offre.getId(), dateParsee, entreprise);
            }

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

    @GetMapping("/offres/{idOffre}/candidats")
    public String candidatsOffre(@PathVariable Long idOffre,
                                 HttpSession session,
                                 Model model) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        Offre offre = offreService.getOffreById(idOffre);
        List<Candidature> candidatures = candidatureService.getCandidaturesOffrePourEntreprise(idOffre, entreprise);
        List<Candidat> candidats = candidatures.stream()
                .map(Candidature::getCandidat)
                .toList();
        model.addAttribute("entreprise", entreprise);
        model.addAttribute("offre", offre);
        model.addAttribute("candidatures", candidatures);
        model.addAttribute("candidats", candidats);
        model.addAttribute("scoresCompatibilite",
                scoringService.calculerScoresPourCandidats(offre, candidats));
        return "entreprise/candidats-offre";
    }

    @PostMapping("/offres/{idOffre}/candidats/contacte/{idCandidat}")
    public String contacterCandidat(@PathVariable Long idOffre,
                                    @PathVariable int idCandidat,
                                    HttpSession session,
                                    RedirectAttributes ra) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            candidatureService.marquerCommeContacte(idOffre, idCandidat, entreprise);
            ra.addFlashAttribute("success", "Candidature marquee comme contactee.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/offres/" + idOffre + "/candidats";
    }

    @PostMapping("/offres/{idOffre}/candidats/refuser/{idCandidat}")
    public String refuserCandidat(@PathVariable Long idOffre,
                                  @PathVariable int idCandidat,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            candidatureService.marquerCommeRefuse(idOffre, idCandidat, entreprise);
            ra.addFlashAttribute("success", "Candidature refusee.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/offres/" + idOffre + "/candidats";
    }

    @PostMapping("/offres/{idOffre}/candidats/attente/{idCandidat}")
    public String remettreCandidatEnAttente(@PathVariable Long idOffre,
                                            @PathVariable int idCandidat,
                                            HttpSession session,
                                            RedirectAttributes ra) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        try {
            candidatureService.remettreEnAttente(idOffre, idCandidat, entreprise);
            ra.addFlashAttribute("success", "Candidature remise en attente.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/entreprise/offres/" + idOffre + "/candidats";
    }

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
     * Affiche la wishlist enrichie avec le meilleur score de compatibilité
     * de chaque candidat parmi les offres de l'entreprise auxquelles il a postulé.
     *
     * <p>Algorithme :
     * <ol>
     *   <li>Récupère la wishlist.</li>
     *   <li>Via {@link CandidatureService#getOffresParCandidatWishlist}, obtient
     *       pour chaque candidat la liste des offres de cette entreprise
     *       auxquelles il a postulé.</li>
     *   <li>Calcule le score sur chaque offre et retient le maximum
     *       (= meilleur match). Score 0 si aucune candidature trouvée.</li>
     * </ol>
     * </p>
     */
    @GetMapping("/wishlist")
    public String wishlist(HttpSession session, Model model) {
        Entreprise entreprise = resoudreEntreprise(session);
        if (entreprise == null) return "redirect:/login?type=entreprise";

        List<Candidat> wishlist = candidatureService.getWishlist(entreprise.getId());

        // CIN → liste des offres postulées chez cette entreprise
        Map<Integer, List<Offre>> offresParCandidat =
                candidatureService.getOffresParCandidatWishlist(entreprise.getId(), wishlist);

        // CIN → meilleur score parmi ces offres
        Map<Integer, Integer> meilleursScores = new HashMap<>();
        for (Candidat candidat : wishlist) {
            List<Offre> offres = offresParCandidat.get(candidat.getId());
            if (offres == null || offres.isEmpty()) {
                meilleursScores.put(candidat.getId(), 0);
            } else {
                int max = offres.stream()
                        .mapToInt(o -> scoringService.calculerScoreCompatibilite(candidat, o))
                        .max()
                        .orElse(0);
                meilleursScores.put(candidat.getId(), max);
            }
        }

        model.addAttribute("entreprise", entreprise);
        model.addAttribute("wishlist", wishlist);
        model.addAttribute("scoresCompatibilite", meilleursScores);
        return "entreprise/wishlist";
    }

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

    @GetMapping("/profil")
    public String profil(HttpSession session, Model model) {
        Entreprise entreprise = resoudreEntreprise(session);
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

