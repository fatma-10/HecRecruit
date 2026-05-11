package com.IHEC.Recruit.service;

import com.IHEC.Recruit.model.*;
import com.IHEC.Recruit.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CandidatService {

    private final CandidatRepository candidatRepository;
    private final EtudiantRepository etudiantRepository;
    private final AlumniRepository alumniRepository;

    public CandidatService(CandidatRepository candidatRepository,
                            EtudiantRepository etudiantRepository,
                            AlumniRepository alumniRepository) {
        this.candidatRepository = candidatRepository;
        this.etudiantRepository = etudiantRepository;
        this.alumniRepository = alumniRepository;
    }

    // ========== RÉCUPÉRATION ==========

    public List<Candidat> getAllCandidats() {
        return candidatRepository.findAll();
    }

    public Candidat getCandidatById(int id) {
        return candidatRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Candidat non trouvé"));
    }

    // ========== RECHERCHE ==========

    /**
     * Recherche unifiée selon critère — toutes les requêtes vont directement en DB.
     */
    public List<? extends Candidat> rechercherCandidats(String critere, String valeur) {
        switch (critere.toLowerCase()) {

            case "nom":
                return candidatRepository.findByNomContainingIgnoreCase(valeur);

            case "email":
                return candidatRepository
                    .findByEmailIgnoreCase(valeur)
                    .map(List::of)
                    .orElse(List.of());

            case "toutes":
                return candidatRepository
                    .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        valeur, valeur, valeur);

            case "etudiant":
                // Recherche filière OU établissement — uniquement dans la table etudiant
                return etudiantRepository
                    .findByFiliereContainingIgnoreCaseOrEtablissementContainingIgnoreCase(
                        valeur, valeur);

            case "alumni":
                // Recherche dans entreprise actuelle — uniquement dans la table alumni
                return alumniRepository.findByEntrepriseActuelleContainingIgnoreCase(valeur);

            default:
                return List.of();
        }
    }

    // ========== MODIFICATION PROFIL ==========

    /**
     * Met à jour le profil d'un candidat et sauvegarde directement en DB.
     */
    public Candidat modifierProfil(int idCandidat, Map<String, String> nouvellesInfos) {
        Candidat candidat = getCandidatById(idCandidat);

        // Champs communs à tous les candidats
        if (nouvellesInfos.containsKey("telephone")) {
            candidat.setTelephone(nouvellesInfos.get("telephone"));
        }

        // Champs spécifiques aux étudiants
        if (candidat instanceof Etudiant) {
            Etudiant etud = (Etudiant) candidat;

            if (nouvellesInfos.containsKey("niveau"))
                etud.setNiveau(nouvellesInfos.get("niveau"));
            if (nouvellesInfos.containsKey("filiere"))
                etud.setFiliere(nouvellesInfos.get("filiere"));
            if (nouvellesInfos.containsKey("etablissement"))
                etud.setEtablissement(nouvellesInfos.get("etablissement"));
        }
        // Champs spécifiques aux alumni
        else if (candidat instanceof Alumni) {
            Alumni alumni = (Alumni) candidat;

            if (nouvellesInfos.containsKey("posteActuel"))
                alumni.setPosteActuel(nouvellesInfos.get("posteActuel"));
            if (nouvellesInfos.containsKey("entrepriseActuelle"))
                alumni.setEntrepriseActuelle(nouvellesInfos.get("entrepriseActuelle"));
        }

        return candidatRepository.save(candidat);
    }
}
