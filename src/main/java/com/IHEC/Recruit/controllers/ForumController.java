package com.IHEC.Recruit.controllers;

import com.IHEC.Recruit.dto.ForumMessageForm;
import com.IHEC.Recruit.models.Candidat;
import com.IHEC.Recruit.models.Entreprise;
import com.IHEC.Recruit.security.CurrentUser;
import com.IHEC.Recruit.services.ForumService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/forum")
public class ForumController {

    private final ForumService forumService;
    private final CurrentUser currentUser;

    public ForumController(ForumService forumService, CurrentUser currentUser) {
        this.forumService = forumService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String critere,
                       @RequestParam(required = false) String q,
                       Model model) {
        if (!currentUser.isLogged()) return "redirect:/";

        model.addAttribute("critere", critere == null ? "" : critere);
        model.addAttribute("q", q == null ? "" : q);

        if (q != null && !q.isBlank()) {
            model.addAttribute("messages", forumService.rechercherCommentaires(critere, q));
        } else if (critere != null && (critere.equals("etudiant") || critere.equals("entreprise"))) {
            model.addAttribute("messages", forumService.rechercherCommentaires(critere, ""));
        } else {
            model.addAttribute("messages", forumService.getAllCommentaires());
        }

        model.addAttribute("stats", forumService.getStatistiquesForum());
        if (!model.containsAttribute("messageForm")) {
            model.addAttribute("messageForm", new ForumMessageForm());
        }
        return "forum/list";
    }

    @PostMapping("/messages")
    public String publier(@Valid @ModelAttribute("messageForm") ForumMessageForm form,
                          BindingResult br,
                          RedirectAttributes attrs) {
        if (!currentUser.isLogged()) return "redirect:/";
        if (br.hasErrors()) {
            attrs.addFlashAttribute("error", "Le message ne peut pas être vide");
            return "redirect:/forum";
        }

        String auteur;
        String email;
        boolean estEtudiant;

        if (currentUser.isCandidat()) {
            Candidat c = currentUser.getCandidat();
            auteur = c.getPrenom() + " " + c.getNom();
            email = c.getEmail();
            estEtudiant = true;
        } else {
            Entreprise e = currentUser.getEntreprise();
            auteur = e.getNom();
            email = e.getEmail();
            estEtudiant = false;
        }

        forumService.ajouterCommentaire(auteur, email, form.getMessage(), estEtudiant);
        attrs.addFlashAttribute("success", "Message publié");
        return "redirect:/forum";
    }
}
