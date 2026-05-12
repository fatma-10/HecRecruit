package com.IHEC.Recruit.controllers;

import com.IHEC.Recruit.models.Entreprise;
import com.IHEC.Recruit.models.Offre;
import com.IHEC.Recruit.security.CurrentUser;
import com.IHEC.Recruit.services.CandidatureService;
import com.IHEC.Recruit.services.EntrepriseService;
import com.IHEC.Recruit.services.OffreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/entreprise")
public class EntrepriseController {

    private final CurrentUser currentUser;
    private final EntrepriseService entrepriseService;
    private final OffreService offreService;
    private final CandidatureService candidatureService;

    public EntrepriseController(CurrentUser currentUser,
                                 EntrepriseService entrepriseService,
                                 OffreService offreService,
                                 CandidatureService candidatureService) {
        this.currentUser = currentUser;
        this.entrepriseService = entrepriseService;
        this.offreService = offreService;
        this.candidatureService = candidatureService;
    }

    // ===== DASHBOARD =====

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Entreprise me = entrepriseService.getEntrepriseById(currentUser.getEntreprise().getId());
        List<Offre> offres = offreService.getOffresEntreprise(me);
        int totalCandidatures = offres.stream().mapToInt(Offre::getNombreCandidatures).sum();

        model.addAttribute("entreprise", me);
        model.addAttribute("nbOffres", offres.size());
        model.addAttribute("nbCandidatures", totalCandidatures);
        model.addAttribute("nbWishlist", me.getWishlist().size());
        model.addAttribute("offresRecentes", offres.stream().limit(5).toList());
        return "entreprise/dashboard";
    }

    // ===== MES OFFRES =====

    @GetMapping("/mes-offres")
    public String mesOffres(Model model) {
        Entreprise me = entrepriseService.getEntrepriseById(currentUser.getEntreprise().getId());
        model.addAttribute("offres", offreService.getOffresEntreprise(me));
        return "entreprise/mes-offres";
    }

    @GetMapping("/mes-offres/{id}/candidats")
    public String candidatsOffre(@PathVariable Long id, Model model) {
        Offre offre = offreService.getOffreById(id);
        // Verifier que c'est bien notre offre
        if (!offre.getEntreprise().getId().equals(currentUser.getEntreprise().getId())) {
            return "redirect:/entreprise/mes-offres";
        }
        model.addAttribute("offre", offre);
        model.addAttribute("candidats", candidatureService.getCandidatsOffre(id));
        return "entreprise/candidats-offre";
    }

    @PostMapping("/mes-offres/{offreId}/candidats/{candidatId}/supprimer")
    public String supprimerCandidat(@PathVariable Long offreId,
                                    @PathVariable String candidatId,
                                    RedirectAttributes attrs) {
        Entreprise me = currentUser.getEntreprise();
        candidatureService.supprimerCandidatureOffre(offreId, candidatId, me);
        attrs.addFlashAttribute("success", "Candidature supprimée");
        return "redirect:/entreprise/mes-offres/" + offreId + "/candidats";
    }

    // ===== PROFIL =====

    @GetMapping("/profil")
    public String profil(Model model) {
        Entreprise me = entrepriseService.getEntrepriseById(currentUser.getEntreprise().getId());
        model.addAttribute("entreprise", me);
        return "entreprise/profil";
    }

    @PostMapping("/profil")
    public String updateProfil(@RequestParam Map<String, String> params,
                                RedirectAttributes attrs) {
        Entreprise me = currentUser.getEntreprise();
        Map<String, String> infos = new HashMap<>(params);
        Entreprise updated = entrepriseService.modifierProfil(me.getId(), infos);
        currentUser.loginEntreprise(updated);
        attrs.addFlashAttribute("success", "Profil mis à jour");
        return "redirect:/entreprise/profil";
    }
}
