package com.IHEC.Recruit.service;

import com.IHEC.Recruit.entity.*;
import com.IHEC.Recruit.exception.NotFoundException;
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

    public List<Candidat> getAllCandidats() {
        return candidatRepository.findAll();
    }

    public Candidat getCandidatById(int id) {
        return candidatRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Candidat non trouvé"));
    }

    public List<? extends Candidat> rechercherCandidats(String critere, String valeur) {
        if (valeur == null) valeur = "";
        switch (critere == null ? "" : critere.toLowerCase()) {
            case "nom":
                return candidatRepository.findByNomContainingIgnoreCase(valeur);
            case "email":
                return candidatRepository.findByEmailIgnoreCase(valeur)
                        .map(List::of).orElse(List.of());
            case "toutes":
                return candidatRepository
                        .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
                                valeur, valeur, valeur);
            case "etudiant":
                return etudiantRepository
                        .findByFiliereContainingIgnoreCaseOrEtablissementContainingIgnoreCase(
                                valeur, valeur);
            case "alumni":
                return alumniRepository.findByEntrepriseActuelleContainingIgnoreCase(valeur);
            default:
                return List.of();
        }
    }

    public Candidat modifierProfil(int idCandidat, Map<String, String> nouvellesInfos) {
        Candidat candidat = getCandidatById(idCandidat);

        if (nouvellesInfos.containsKey("telephone"))
            candidat.setTelephone(nouvellesInfos.get("telephone"));

        if (candidat instanceof Etudiant etud) {
            if (nouvellesInfos.containsKey("niveau"))
                etud.setNiveau(nouvellesInfos.get("niveau"));
            if (nouvellesInfos.containsKey("filiere"))
                etud.setFiliere(nouvellesInfos.get("filiere"));
            if (nouvellesInfos.containsKey("etablissement"))
                etud.setEtablissement(nouvellesInfos.get("etablissement"));
        } else if (candidat instanceof Alumni alumni) {
            if (nouvellesInfos.containsKey("posteActuel"))
                alumni.setPosteActuel(nouvellesInfos.get("posteActuel"));
            if (nouvellesInfos.containsKey("entrepriseActuelle"))
                alumni.setEntrepriseActuelle(nouvellesInfos.get("entrepriseActuelle"));
        }

        return candidatRepository.save(candidat);
    }
}
