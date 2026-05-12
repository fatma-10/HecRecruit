package com.IHEC.Recruit.controllers;

import com.IHEC.Recruit.dto.OffreForm;
import com.IHEC.Recruit.models.*;
import com.IHEC.Recruit.security.CurrentUser;
import com.IHEC.Recruit.services.CandidatureService;
import com.IHEC.Recruit.services.OffreService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OffreController {

    private final OffreService offreService;
    private final CandidatureService candidatureService;
    private final CurrentUser currentUser;

    public OffreController(OffreService offreService,
                            CandidatureService candidatureService,
                            CurrentUser currentUser) {
        this.offreService = offreService;
        this.candidatureService = candidatureService;
        this.currentUser = currentUser;
    }

    // ===== LISTE / RECHERCHE =====

    @GetMapping("/offres")
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String type,
                       Model model) {
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("type", type == null ? "" : type);
        model.addAttribute("offres", offreService.rechercher(q, type));
        return "offre/list";
    }

    // ===== DETAIL =====

    @GetMapping("/offres/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Offre offre = offreService.getOffreById(id);
        model.addAttribute("offre", offre);

        // Sous-type pour rendu specifique
        if (offre instanceof Stage s) {
            model.addAttribute("stage", s);
        } else if (offre instanceof Alternance a) {
            model.addAttribute("alternance", a);
        } else if (offre instanceof ProjetFinEtudes p) {
            model.addAttribute("pfe", p);
        }

        // Le candidat a-t-il deja postule ?
        boolean dejaPostule = false;
        if (currentUser.isCandidat()) {
            String idC = currentUser.getCandidat().getId();
            dejaPostule = offre.getCandidatures().stream()
                    .anyMatch(c -> idC != null && idC.equals(c.getId()));
        }
        model.addAttribute("dejaPostule", dejaPostule);
        return "offre/detail";
    }

    // ===== POSTULER =====

    @PostMapping("/offres/{id}/postuler")
    public String postuler(@PathVariable Long id, RedirectAttributes attrs) {
        if (!currentUser.isCandidat()) return "redirect:/login/candidat";
        candidatureService.postulerOffre(currentUser.getCandidat().getId(), id);
        attrs.addFlashAttribute("success", "Candidature envoyée");
        return "redirect:/offres/" + id;
    }

    // ===== CREER (entreprise) =====

    @GetMapping("/entreprise/creer-offre")
    public String showCreer(Model model) {
        if (!model.containsAttribute("offreForm")) {
            model.addAttribute("offreForm", new OffreForm());
        }
        return "offre/creer";
    }

    @PostMapping("/entreprise/creer-offre")
    public String doCreer(@Valid @ModelAttribute("offreForm") OffreForm form,
                          BindingResult br,
                          RedirectAttributes attrs,
                          Model model) {
        if (br.hasErrors()) return "offre/creer";

        Map<String, String> infos = new HashMap<>();
        String type = form.getType() == null ? "" : form.getType().toLowerCase();
        if ("stage".equals(type)) {
            infos.put("duree", form.getDuree() == null ? "" : String.valueOf(form.getDuree()));
            infos.put("domaine", form.getDomaine());
        } else if ("alternance".equals(type)) {
            infos.put("rythme", form.getRythme());
            infos.put("duree", form.getDuree() == null ? "" : String.valueOf(form.getDuree()));
        } else if ("pfe".equals(type) || "projet fin d'etudes".equals(type)) {
            infos.put("sujet", form.getSujet());
            infos.put("technologies", form.getTechnologies());
            type = "projet fin d'etudes";
        }

        Entreprise me = currentUser.getEntreprise();
        Offre nouvelle = offreService.creerOffre(form.getTitre(), form.getDescription(), type, me, infos);
        attrs.addFlashAttribute("success", "Offre créée");
        return "redirect:/offres/" + nouvelle.getId();
    }

    // ===== SUPPRIMER =====

    @PostMapping("/entreprise/offres/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes attrs) {
        offreService.supprimerOffre(id, currentUser.getEntreprise());
        attrs.addFlashAttribute("success", "Offre supprimée");
        return "redirect:/entreprise/mes-offres";
    }

    // ===== DATE EXPIRATION =====

    @PostMapping("/entreprise/offres/{id}/expiration")
    public String setExpiration(@PathVariable Long id,
                                @RequestParam(required = false) String dateExpiration,
                                RedirectAttributes attrs) {
        LocalDate date = (dateExpiration == null || dateExpiration.isBlank())
                ? null : LocalDate.parse(dateExpiration);
        offreService.setDateExpiration(id, date, currentUser.getEntreprise());
        attrs.addFlashAttribute("success", "Date d'expiration mise à jour");
        return "redirect:/offres/" + id;
    }
}
