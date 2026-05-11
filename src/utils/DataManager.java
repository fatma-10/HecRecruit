package utils;

import java.util.*;
import service.*;
import models.*;

public class DataManager {
    private static DataManager instance;
    
    // Données en mémoire
    private List<Entreprise> entreprises = new ArrayList<>();
    private List<Candidat> candidats = new ArrayList<>();
    private List<Offre> offres = new ArrayList<>();
    private List<Forum> commentaires = new ArrayList<>();
    
    // Services
    private FileManager fileManager;
    private AuthService authService;
    private OffreService offreService;
    private CandidatService candidatService;
    private EntrepriseService entrepriseService;
    private CandidatureService candidatureService;
    private ForumService forumService;
    private RecommendationService recommendationService; // ✅ NOUVEAU
    
    // Constructeur privé (Singleton)
    private DataManager() {
        fileManager = new FileManager();
    }
    
    // Obtenir l'instance unique
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    // Initialiser et charger les données
    public void initialiser() {
        System.out.println("Chargement des données...");
        
        // Charger dans l'ordre
        entreprises = fileManager.chargerEntreprises();
        candidats = fileManager.chargerCandidats();
        offres = fileManager.chargerOffres(entreprises);
        commentaires = fileManager.chargerCommentaires();
        
        // Initialiser les services
        authService = new AuthService(entreprises, candidats);
        offreService = new OffreService(offres);
        candidatService = new CandidatService(candidats);
        entrepriseService = new EntrepriseService(entreprises);
        candidatureService = new CandidatureService(offres, candidats);
        forumService = new ForumService(commentaires);
        recommendationService = new RecommendationService(offres); 
        
        System.out.println("✅ Données chargées!");
        System.out.println("- Entreprises: " + entreprises.size());
        System.out.println("- Candidats: " + candidats.size());
        System.out.println("- Offres: " + offres.size());
    }
    
    // Sauvegarder les données
    public void sauvegarder() {
        System.out.println("Sauvegarde des données...");
        fileManager.sauvegarderEntreprises(entreprises);
        fileManager.sauvegarderCandidats(candidats);
        fileManager.sauvegarderOffres(offres);
        fileManager.sauvegarderCommentaires(commentaires);
        System.out.println("✅ Données sauvegardées!");
    }
    
    // Getters pour les services
    public AuthService getAuthService() { return authService; }
    public OffreService getOffreService() { return offreService; }
    public CandidatService getCandidatService() { return candidatService; }
    public EntrepriseService getEntrepriseService() { return entrepriseService; }
    public CandidatureService getCandidatureService() { return candidatureService; }
    public ForumService getForumService() { return forumService; }
    public RecommendationService getRecommendationService() { return recommendationService; } 
    
    // Getters pour les listes
    public List<Entreprise> getEntreprises() { return entreprises; }
    public List<Candidat> getCandidats() { return candidats; }
    public List<Offre> getOffres() { return offres; }
}
