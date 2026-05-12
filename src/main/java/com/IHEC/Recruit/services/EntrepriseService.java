package com.IHEC.Recruit.services;

import com.IHEC.Recruit.models.Entreprise;
import com.IHEC.Recruit.exception.NotFoundException;
import com.IHEC.Recruit.repositories.EntrepriseRepository;
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

    public List<Entreprise> getAllEntreprises() {
        return entrepriseRepository.findAll();
    }

    public Entreprise getEntrepriseById(Long id) {
        return entrepriseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entreprise non trouvée"));
    }

    public List<Entreprise> rechercherEntreprises(String critere, String valeur) {
        if (valeur == null) valeur = "";
        switch (critere == null ? "" : critere.toLowerCase()) {
            case "nom":
                return entrepriseRepository.findByNomContainingIgnoreCase(valeur);
            case "secteur":
                return entrepriseRepository.findBySecteurContainingIgnoreCase(valeur);
            case "email":
                return entrepriseRepository.findByEmailIgnoreCase(valeur)
                        .map(List::of).orElse(List.of());
            case "toutes":
                return entrepriseRepository
                        .findByNomContainingIgnoreCaseOrSecteurContainingIgnoreCaseOrAdresseContainingIgnoreCase(
                                valeur, valeur, valeur);
            default:
                return List.of();
        }
    }

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
