package com.IHEC.Recruit.service;


import com.IHEC.Recruit.model.Entreprise;
import com.IHEC.Recruit.repository.EntrepriseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    public EntrepriseService(EntrepriseRepository entrepriseRepository) {
        this.entrepriseRepository = entrepriseRepository;
    }

    // ========== RÉCUPÉRATION ==========

    public List<Entreprise> getAllEntreprises() {
        return entrepriseRepository.findAll();
    }

    public Entreprise getEntrepriseById(Long id) {
        return entrepriseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvée"));
    }

    // ========== RECHERCHE ==========

    /**
     * Recherche directement en DB selon critère.
     */
    public List<Entreprise> rechercherEntreprises(String critere, String valeur) {
        switch (critere.toLowerCase()) {

            case "nom":
                return entrepriseRepository.findByNomContainingIgnoreCase(valeur);

            case "secteur":
                return entrepriseRepository.findBySecteurContainingIgnoreCase(valeur);

            case "email":
                return entrepriseRepository
                        .findByEmailIgnoreCase(valeur)
                        .map(List::of)
                        .orElse(List.of());

            case "toutes":
                return entrepriseRepository
                        .findByNomContainingIgnoreCaseOrSecteurContainingIgnoreCaseOrAdresseContainingIgnoreCase(
                                valeur, valeur, valeur);

            default:
                return List.of();
        }
    }

    // ========== MODIFICATION ==========

    /**
     * Modifie et sauvegarde le profil entreprise directement en DB.
     */
    public Entreprise modifierProfil(Long idEntreprise, Map<String, String> nouvellesInfos) {
        Entreprise entreprise = getEntrepriseById(idEntreprise);

        if (nouvellesInfos.containsKey("secteur"))
            entreprise.setSecteur(nouvellesInfos.get("secteur"));
        if (nouvellesInfos.containsKey("adresse"))
            entreprise.setAdresse(nouvellesInfos.get("adresse"));
        if (nouvellesInfos.containsKey("telephone"))
            entreprise.setTelephone(nouvellesInfos.get("telephone"));

        return entrepriseRepository.save(entreprise);
    }
}
