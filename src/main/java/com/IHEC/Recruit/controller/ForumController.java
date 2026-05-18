package com.IHEC.Recruit.controller;

import com.IHEC.Recruit.model.Forum;
import com.IHEC.Recruit.service.ForumService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller gérant le forum de discussion entre candidats et entreprises.
 *
 * <p>Les utilisateurs connectés (candidat ou entreprise) peuvent :
 * <ul>
 *   <li>Consulter tous les messages triés par date décroissante</li>
 *   <li>Poster de nouveaux messages</li>
 *   <li>Rechercher / filtrer les messages par auteur, contenu ou type d'utilisateur</li>
 * </ul>
 *
 * <p><strong>Sécurité (BUG 1 corrigé) :</strong> l'identité de l'auteur (nom et email)
 * est extraite exclusivement depuis la session HTTP — les champs cachés du formulaire
 * ne sont plus acceptés, ce qui empêche toute usurpation d'identité.
 *
 * <p><strong>Navigation (BUG 2 corrigé) :</strong> {@code userType} est injecté dans
 * le modèle afin que la vue n'affiche que le lien retour correspondant au type
 * d'utilisateur effectivement connecté.
 *
 * <p><strong>Vue (BUG 7 corrigé) :</strong> toutes les méthodes retournent désormais
 * {@code "forum/forum"} au lieu de {@code "forum/list"}.</p>
 */
@Controller
@RequestMapping("/forum")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    // ----------------------------------------------------------------
    //  Utilitaire interne : injecte les infos session dans le modèle
    // ----------------------------------------------------------------

    /**
     * Injecte dans le modèle les informations de l'utilisateur connecté
     * nécessaires au formulaire de publication et à la navigation conditionnelle.
     *
     * <p>Attributs ajoutés au modèle :
     * <ul>
     *   <li>{@code estEtudiant} – {@code true} si l'utilisateur est un candidat</li>
     *   <li>{@code userEmail}   – email de l'utilisateur connecté (chaîne vide si absent)</li>
     *   <li>{@code userNom}     – nom complet de l'utilisateur connecté (chaîne vide si absent)</li>
     *   <li>{@code userType}    – "candidat" ou "entreprise" (permet la navigation conditionnelle)</li>
     * </ul>
     *
     * @param session la session HTTP courante
     * @param model   le modèle Thymeleaf à enrichir
     */
    private void alimenterInfosUtilisateur(HttpSession session, Model model) {
        String userType = (String) session.getAttribute("userType");
        model.addAttribute("estEtudiant", "candidat".equals(userType));
        model.addAttribute("userEmail",
                session.getAttribute("userEmail") != null ? session.getAttribute("userEmail") : "");
        model.addAttribute("userNom",
                session.getAttribute("userNom") != null ? session.getAttribute("userNom") : "");
        // BUG 2 — userType transmis à la vue pour la navigation conditionnelle
        model.addAttribute("userType", userType);
    }

    // ================================================================
    //  AFFICHAGE DU FORUM
    // ================================================================

    /**
     * Affiche tous les messages du forum avec le formulaire de publication.
     *
     * @param session la session HTTP contenant les attributs de l'utilisateur connecté
     * @param model   le modèle Thymeleaf
     * @return la vue {@code forum/forum} (BUG 7 corrigé)
     */
    @GetMapping
    public String afficherForum(HttpSession session, Model model) {
        model.addAttribute("messages", forumService.getAllCommentaires());
        alimenterInfosUtilisateur(session, model);
        // BUG 7 — "forum/forum" au lieu de "forum/list"
        return "forum/forum";
    }

    // ================================================================
    //  PUBLICATION D'UN MESSAGE
    // ================================================================

    /**
     * Publie un nouveau message sur le forum au nom de l'utilisateur connecté.
     *
     * <p><strong>BUG 1 corrigé :</strong> {@code auteur} et {@code email} sont lus
     * exclusivement depuis les attributs de session {@code userNom} et {@code userEmail}.
     *
     * @param message le contenu textuel du message à publier
     * @param session la session HTTP contenant {@code userNom}, {@code userEmail}
     *                et {@code userType}
     * @param ra      les attributs de redirection pour les messages flash
     * @return redirection vers {@code /forum}
     */
    @PostMapping("/poster")
    public String posterMessage(@RequestParam String message,
                                HttpSession session,
                                RedirectAttributes ra) {
        // BUG 1 — lecture de l'identité depuis la session uniquement
        String auteur   = (String) session.getAttribute("userNom");
        String email    = (String) session.getAttribute("userEmail");
        String userType = (String) session.getAttribute("userType");

        if (auteur == null || email == null || userType == null) {
            ra.addFlashAttribute("error", "Vous devez être connecté pour poster un message.");
            return "redirect:/forum";
        }

        try {
            boolean estEtudiant = "candidat".equals(userType);
            forumService.ajouterCommentaire(auteur, email, message, estEtudiant);
            ra.addFlashAttribute("success", "Message publié !");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/forum";
    }

    // ================================================================
    //  RECHERCHE / FILTRAGE
    // ================================================================

    /**
     * Recherche et filtre les messages du forum selon un critère donné.
     *
     * @param critere le critère de recherche (auteur, message, etudiant, entreprise)
     * @param valeur  la valeur recherchée (peut être vide pour "etudiant" et "entreprise")
     * @param session la session HTTP pour récupérer les informations de l'utilisateur
     * @param model   le modèle Thymeleaf
     * @return la vue {@code forum/forum} (BUG 7 corrigé)
     */
    @GetMapping("/recherche")
    public String rechercher(@RequestParam String critere,
                             @RequestParam String valeur,
                             HttpSession session,
                             Model model) {
        List<Forum> resultats = forumService.rechercherCommentaires(critere, valeur);
        model.addAttribute("messages", resultats);
        model.addAttribute("critere", critere);
        model.addAttribute("valeur", valeur);
        alimenterInfosUtilisateur(session, model);
        // BUG 7 — "forum/forum" au lieu de "forum/list"
        return "forum/forum";
    }
}