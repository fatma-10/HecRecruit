package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.model.Candidat;
import com.IHEC.Recruit.model.Entreprise;
import com.IHEC.Recruit.model.Forum;
import com.IHEC.Recruit.service.ForumService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/forum")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    // Afficher tous les messages (public mais accessible après login)
    @GetMapping
    public String afficherForum(HttpSession session, Model model) {
        // Récupérer l'utilisateur connecté pour le formulaire
        Object user = session.getAttribute("candidatId");
        boolean estEtudiant = user != null;
        String email = "";
        String nom = "";

        if (estEtudiant) {
            Candidat c = (Candidat) session.getAttribute("candidatObj"); // à stocker lors du login
            // Sinon on peut le récupérer via service, mais pour simplifier on passe juste le type
            email = (String) session.getAttribute("userEmail");
            nom = (String) session.getAttribute("userNom");
        } else if (session.getAttribute("entrepriseId") != null) {
            estEtudiant = false;
            email = (String) session.getAttribute("userEmail");
            nom = (String) session.getAttribute("userNom");
        }

        model.addAttribute("messages", forumService.getAllCommentaires());
        model.addAttribute("estEtudiant", estEtudiant);
        model.addAttribute("userEmail", email);
        model.addAttribute("userNom", nom);
        return "forum/list";
    }

    // Poster un nouveau message
    @PostMapping("/poster")
    public String posterMessage(@RequestParam String message,
                                @RequestParam String auteur,
                                @RequestParam String emailAuteur,
                                @RequestParam boolean estEtudiant,
                                RedirectAttributes ra) {
        try {
            forumService.ajouterCommentaire(auteur, emailAuteur, message, estEtudiant);
            ra.addFlashAttribute("success", "Message publié !");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/forum";
    }

    // Filtrage par auteur, contenu, ou type
    @GetMapping("/recherche")
    public String rechercher(@RequestParam String critere,
                             @RequestParam String valeur,
                             HttpSession session,
                             Model model) {
        List<Forum> resultats = forumService.rechercherCommentaires(critere, valeur);
        model.addAttribute("messages", resultats);
        model.addAttribute("critere", critere);
        model.addAttribute("valeur", valeur);
        // On remet aussi les infos utilisateur pour le formulaire
        model.addAttribute("estEtudiant", session.getAttribute("candidatId") != null);
        return "forum/list";
    }
}