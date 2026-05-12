package com.IHEC.Recruit.service;

import com.IHEC.Recruit.entity.*;
import com.IHEC.Recruit.exception.BusinessException;
import com.IHEC.Recruit.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final EntrepriseRepository entrepriseRepository;
    private final CandidatRepository candidatRepository;

    public AuthService(EntrepriseRepository entrepriseRepository,
                       CandidatRepository candidatRepository) {
        this.entrepriseRepository = entrepriseRepository;
        this.candidatRepository = candidatRepository;
    }

    // ========== CONNEXION ==========

    public Entreprise loginEntreprise(String email, String mdp) {
        if (email == null || mdp == null) return null;
        Optional<Entreprise> opt = entrepriseRepository.findByEmailIgnoreCase(email);
        return (opt.isPresent() && opt.get().verifierMotDePasse(mdp)) ? opt.get() : null;
    }

    public Candidat loginCandidat(String email, String mdp) {
        if (email == null || mdp == null) return null;
        Optional<Candidat> opt = candidatRepository.findByEmailIgnoreCase(email);
        return (opt.isPresent() && opt.get().verifierMotDePasse(mdp)) ? opt.get() : null;
    }

    // ========== INSCRIPTION ==========

    public Entreprise registerEntreprise(String nom, String secteur, String adresse,
                                         String email, String telephone, String mdp) {
        if (entrepriseRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Un compte avec cet email existe déjà");
        }
        Entreprise nouvelle = new Entreprise(nom, secteur, adresse, email, telephone, mdp);
        return entrepriseRepository.save(nouvelle);
    }

    public Candidat registerCandidat(String nom, String prenom, String email,
                                      String telephone, String mdp,
                                      String typeCandidat, Map<String, String> infos) {

        if (email == null || !email.toLowerCase().endsWith("@ihec.ucar.tn")) {
            throw new BusinessException("L'email doit se terminer par @ihec.ucar.tn");
        }
        if (candidatRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Un compte avec cet email existe déjà");
        }

        String idStr = infos.get("id");
        if (idStr == null || idStr.isBlank()) {
            throw new BusinessException("Le CIN est obligatoire");
        }
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new BusinessException("Le CIN doit être un nombre à 8 chiffres");
        }
        if (id < 10000000 || id > 99999999) {
            throw new BusinessException("Le CIN doit être un nombre à 8 chiffres");
        }
        if (candidatRepository.existsById(id)) {
            throw new BusinessException("Ce CIN est déjà utilisé");
        }

        Candidat nouveau;
        switch (typeCandidat == null ? "" : typeCandidat.toLowerCase()) {
            case "etudiant":
                nouveau = new Etudiant(
                        id, nom, prenom, email, telephone, mdp,
                        infos.get("niveau"),
                        infos.get("filiere"),
                        infos.get("etablissement"));
                break;

            case "alumni":
                int annee;
                try {
                    annee = Integer.parseInt(infos.getOrDefault("anneeDiplome", "0"));
                } catch (NumberFormatException e) {
                    throw new BusinessException("L'année de diplôme doit être un nombre");
                }
                if (annee < 1950 || annee > java.time.LocalDate.now().getYear()) {
                    throw new BusinessException("L'année de diplôme est invalide");
                }
                nouveau = new Alumni(
                        id, nom, prenom, email, telephone, mdp,
                        annee,
                        infos.getOrDefault("posteActuel", ""),
                        infos.getOrDefault("entrepriseActuelle", ""));
                break;

            default:
                throw new BusinessException("Type de candidat invalide : " + typeCandidat);
        }

        return candidatRepository.save(nouveau);
    }
}
