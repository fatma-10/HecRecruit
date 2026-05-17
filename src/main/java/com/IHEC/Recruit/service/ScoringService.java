package com.IHEC.Recruit.service;

import com.IHEC.Recruit.model.Candidat;
import com.IHEC.Recruit.model.Offre;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScoringService {

    public int calculerScoreCompatibilite(Candidat candidat, Offre offre) {
        if (candidat == null || offre == null) {
            return 0;
        }

        Set<String> competences = extraireMotsCles(candidat.getSkills());
        Set<String> description = extraireMotsCles(offre.getDescription());

        if (competences.isEmpty() || description.isEmpty()) {
            return 0;
        }

        long correspondances = competences.stream()
                .filter(description::contains)
                .count();

        return (int) Math.round((correspondances * 100.0) / competences.size());
    }

    public Map<Long, Integer> calculerScoresPourOffres(Candidat candidat, Collection<Offre> offres) {
        return offres.stream()
                .collect(Collectors.toMap(
                        Offre::getId,
                        offre -> calculerScoreCompatibilite(candidat, offre)
                ));
    }

    public Map<Integer, Integer> calculerScoresPourCandidats(Offre offre, Collection<Candidat> candidats) {
        return candidats.stream()
                .collect(Collectors.toMap(
                        Candidat::getId,
                        candidat -> calculerScoreCompatibilite(candidat, offre)
                ));
    }

    private Set<String> extraireMotsCles(String texte) {
        if (texte == null || texte.isBlank()) {
            return Set.of();
        }

        String normalise = Normalizer.normalize(texte.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9]+", " ");

        return Arrays.stream(normalise.split("\\s+"))
                .map(String::trim)
                .filter(mot -> mot.length() >= 2)
                .collect(Collectors.toSet());
    }
}
