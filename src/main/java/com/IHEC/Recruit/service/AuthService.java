package com.IHEC.Recruit.service;

import com.IHEC.Recruit.model.*;
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

    /**
     * Connexion entreprise — cherche directement en DB par email.
     * Retourne l'entreprise si credentials corrects, null sinon.
     */
    public Entreprise loginEntreprise(String email, String mdp) {
        Optional<Entreprise> opt = entrepriseRepository.findByEmailIgnoreCase(email);

        if (opt.isPresent() && opt.get().verifierMotDePasse(mdp)) {
            return opt.get();
        }
        return null;
    }

    /**
     * Connexion candidat — cherche directement en DB par email.
     * Retourne le candidat si credentials corrects, null sinon.
     */
    public Candidat loginCandidat(String email, String mdp) {
        Optional<Candidat> opt = candidatRepository.findByEmailIgnoreCase(email);

        if (opt.isPresent() && opt.get().getMdp().equals(mdp)) {
            return opt.get();
        }
        return null;
    }

    // ========== INSCRIPTION ==========

    /**
     * Inscription entreprise — vérifie unicité email via DB, puis sauvegarde.
     */
    public Entreprise registerEntreprise(String nom, String secteur, String adresse,
                                         String email, String telephone, String mdp) {
        // Vérifier unicité email directement en DB
        if (entrepriseRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà");
        }

        Entreprise nouvelle = new Entreprise(nom, secteur, adresse, email, telephone, mdp);
        return entrepriseRepository.save(nouvelle);
    }

    /**
     * Inscription candidat (Etudiant ou Alumni).
     * La validation d'unicité est faite en DB, pas en mémoire.
     */
    public Candidat registerCandidat(String nom, String prenom, String email,
                                      String telephone, String mdp,
                                      String typeCandidat, Map<String, String> infos) {
        // Validation email institutionnel
        if (!email.toLowerCase().endsWith("@ihec.ucar.tn")) {
            throw new IllegalArgumentException("L'email doit se terminer par @ihec.ucar.tn");
        }

        // Vérifier unicité email directement en DB
        if (candidatRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà");
        }

        int id = Integer.parseInt(infos.get("id"));

        // Vérifier unicité CIN directement en DB
        if (candidatRepository.existsById(id)) {
            throw new IllegalArgumentException("Ce CIN est déjà utilisé");
        }

        Candidat nouveau;

        switch (typeCandidat.toLowerCase()) {
            case "etudiant":
                nouveau = new Etudiant(
                    id, nom, prenom, email, telephone, mdp,
                    infos.get("niveau"),
                    infos.get("filiere"),
                    infos.get("etablissement")
                );
                break;

            case "alumni":
                nouveau = new Alumni(
                    id, nom, prenom, email, telephone, mdp,
                    Integer.parseInt(infos.get("anneeDiplome")),
                    infos.getOrDefault("posteActuel", ""),
                    infos.getOrDefault("entrepriseActuelle", "")
                );
                break;

            default:
                throw new IllegalArgumentException("Type de candidat invalide : " + typeCandidat);
        }

        return candidatRepository.save(nouveau);
    }
}
