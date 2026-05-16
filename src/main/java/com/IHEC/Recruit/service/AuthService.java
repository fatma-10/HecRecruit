package com.IHEC.Recruit.service;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Service d'authentification et d'inscription pour les candidats et les entreprises.
 *
 * <p>Les mots de passe sont systématiquement hachés avec BCrypt avant persistance
 * et vérifiés via {@link BCryptPasswordEncoder#matches(CharSequence, String)}.
 * Aucun mot de passe en clair n'est jamais stocké en base.</p>
 */
@Service
@Transactional
public class AuthService {

    private final EntrepriseRepository entrepriseRepository;
    private final CandidatRepository candidatRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Construit le service avec les dépendances nécessaires.
     *
     * @param entrepriseRepository repository JPA des entreprises
     * @param candidatRepository   repository JPA des candidats
     * @param passwordEncoder      encodeur BCrypt déclaré comme bean dans RecruitApplication
     */
    public AuthService(EntrepriseRepository entrepriseRepository,
                       CandidatRepository candidatRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.entrepriseRepository = entrepriseRepository;
        this.candidatRepository = candidatRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ========== CONNEXION ==========

    /**
     * Authentifie une entreprise par email et mot de passe en clair.
     *
     * <p>Le mot de passe fourni est comparé au hash BCrypt stocké en base
     * via {@link BCryptPasswordEncoder#matches(CharSequence, String)}.
     * Aucune comparaison en clair n'est effectuée.</p>
     *
     * @param email email de l'entreprise (insensible à la casse)
     * @param mdp   mot de passe en clair saisi par l'utilisateur
     * @return l'entité {@link Entreprise} si les credentials sont corrects, {@code null} sinon
     */
    public Entreprise loginEntreprise(String email, String mdp) {
        Optional<Entreprise> opt = entrepriseRepository.findByEmailIgnoreCase(email);

        if (opt.isPresent() && passwordEncoder.matches(mdp, opt.get().getMdp())) {
            return opt.get();
        }
        return null;
    }

    /**
     * Authentifie un candidat (étudiant ou alumni) par email et mot de passe en clair.
     *
     * <p>Le mot de passe fourni est comparé au hash BCrypt stocké en base
     * via {@link BCryptPasswordEncoder#matches(CharSequence, String)}.
     * Aucune comparaison en clair n'est effectuée.</p>
     *
     * @param email email du candidat (insensible à la casse)
     * @param mdp   mot de passe en clair saisi par l'utilisateur
     * @return l'entité {@link Candidat} si les credentials sont corrects, {@code null} sinon
     */
    public Candidat loginCandidat(String email, String mdp) {
        Optional<Candidat> opt = candidatRepository.findByEmailIgnoreCase(email);

        if (opt.isPresent() && passwordEncoder.matches(mdp, opt.get().getMdp())) {
            return opt.get();
        }
        return null;
    }

    // ========== INSCRIPTION ==========

    /**
     * Inscrit une nouvelle entreprise après vérification de l'unicité de l'email.
     *
     * <p>Le mot de passe en clair est haché avec BCrypt avant d'être passé
     * au constructeur de {@link Entreprise} et persisté en base.</p>
     *
     * @param nom       nom de l'entreprise
     * @param secteur   secteur d'activité (peut être vide)
     * @param adresse   adresse physique (peut être vide)
     * @param email     email unique de l'entreprise
     * @param telephone numéro de téléphone
     * @param mdp       mot de passe en clair choisi par l'entreprise
     * @return l'entité {@link Entreprise} persistée
     * @throws IllegalArgumentException si l'email est déjà utilisé
     */
    public Entreprise registerEntreprise(String nom, String secteur, String adresse,
                                         String email, String telephone, String mdp) {
        // Vérifier unicité email directement en DB
        if (entrepriseRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà");
        }

        // Hacher le mot de passe avant persistance
        String mdpHache = passwordEncoder.encode(mdp);

        Entreprise nouvelle = new Entreprise(nom, secteur, adresse, email, telephone, mdpHache);
        return entrepriseRepository.save(nouvelle);
    }

    /**
     * Inscrit un nouveau candidat (Etudiant ou Alumni) après vérifications d'unicité.
     *
     * <p>Validations effectuées (dans l'ordre) :</p>
     * <ul>
     *   <li>L'email doit se terminer par {@code @ihec.ucar.tn}</li>
     *   <li>L'email ne doit pas déjà exister en base</li>
     *   <li>Le CIN (id) ne doit pas déjà exister en base</li>
     * </ul>
     *
     * <p>Le mot de passe en clair est haché avec BCrypt avant persistance.
     * Toute la logique de validation des champs métier reste dans les constructeurs
     * des entités ({@link Etudiant}, {@link Alumni}).</p>
     *
     * @param nom           nom de famille du candidat
     * @param prenom        prénom du candidat
     * @param email         email institutionnel (doit finir par {@code @ihec.ucar.tn})
     * @param telephone     numéro de téléphone
     * @param mdp           mot de passe en clair choisi par le candidat
     * @param typeCandidat  {@code "etudiant"} ou {@code "alumni"} (insensible à la casse)
     * @param infos         map de champs supplémentaires selon le type :
     *                      <ul>
     *                        <li>Commun : {@code "id"} (CIN 8 chiffres)</li>
     *                        <li>Etudiant : {@code "niveau"}, {@code "filiere"}, {@code "etablissement"}</li>
     *                        <li>Alumni : {@code "anneeDiplome"}, {@code "posteActuel"}, {@code "entrepriseActuelle"}</li>
     *                      </ul>
     * @return l'entité {@link Candidat} persistée (instance concrète Etudiant ou Alumni)
     * @throws IllegalArgumentException si une validation échoue ou si le type est inconnu
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

        // Hacher le mot de passe avant persistance
        String mdpHache = passwordEncoder.encode(mdp);

        Candidat nouveau;

        switch (typeCandidat.toLowerCase()) {
            case "etudiant":
                nouveau = new Etudiant(
                    id, nom, prenom, email, telephone, mdpHache,
                    infos.get("niveau"),
                    infos.get("filiere"),
                    infos.get("etablissement")
                );
                break;

            case "alumni":
                nouveau = new Alumni(
                    id, nom, prenom, email, telephone, mdpHache,
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