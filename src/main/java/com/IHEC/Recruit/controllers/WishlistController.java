package com.IHEC.Recruit.controllers;

import com.IHEC.Recruit.models.Entreprise;
import com.IHEC.Recruit.security.CurrentUser;
import com.IHEC.Recruit.services.CandidatureService;
import com.IHEC.Recruit.services.EntrepriseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/entreprise/wishlist")
public class WishlistController {

    private final CandidatureService candidatureService;
    private final EntrepriseService entrepriseService;
    private final CurrentUser currentUser;

    public WishlistController(CandidatureService candidatureService,
                              EntrepriseService entrepriseService,
                              CurrentUser currentUser) {
        this.candidatureService = candidatureService;
        this.entrepriseService = entrepriseService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public String list(Model model) {
        Entreprise me = entrepriseService.getEntrepriseById(currentUser.getEntreprise().getId());
        model.addAttribute("candidats", me.getWishlist());
        return "wishlist/list";
    }

    @PostMapping("/{candidatId}/ajouter")
    public String ajouter(@PathVariable String candidatId, RedirectAttributes attrs) {
        Entreprise me = currentUser.getEntreprise();
        candidatureService.ajouterWishlist(me.getId(), candidatId);
        attrs.addFlashAttribute("success", "Candidat ajouté à la wishlist");
        return "redirect:/entreprise/wishlist";
    }

    @PostMapping("/{candidatId}/retirer")
    public String retirer(@PathVariable String candidatId, RedirectAttributes attrs) {
        Entreprise me = currentUser.getEntreprise();
        candidatureService.retirerWishlist(me.getId(), candidatId);
        attrs.addFlashAttribute("success", "Candidat retiré de la wishlist");
        return "redirect:/entreprise/wishlist";
    }
}
