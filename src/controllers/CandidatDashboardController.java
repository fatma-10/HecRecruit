package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import models.*;
import service.*;
import service.RecommendationService.OffreRecommandee;
import utils.DataManager;

import java.util.List;

public class CandidatDashboardController {
    
    @FXML private Label lblNomCandidat;
    @FXML private Label lblTypeCandidat;
    @FXML private Label lblNbCandidatures;
    @FXML private Label lblNbOffres;
    
    // Table des dernières offres
    @FXML private TableView<OffreDisplay> tableDernieresOffres;
    @FXML private TableColumn<OffreDisplay, String> colTitre;
    @FXML private TableColumn<OffreDisplay, String> colType;
    @FXML private TableColumn<OffreDisplay, String> colEntreprise;
    @FXML private TableColumn<OffreDisplay, String> colDate;
    
    // ✅ NOUVEAU : Table des recommandations
    @FXML private TableView<RecommandationDisplay> tableRecommandations;
    @FXML private TableColumn<RecommandationDisplay, String> colTitreReco;
    @FXML private TableColumn<RecommandationDisplay, String> colTypeReco;
    @FXML private TableColumn<RecommandationDisplay, String> colEntrepriseReco;
    @FXML private TableColumn<RecommandationDisplay, String> colScoreReco;
    
    // ✅ NOUVEAU : Boutons recommandations
    @FXML private Button btnVoirDetailsReco;
    @FXML private Button btnPostulerReco;
    
    @FXML private Button btnDeconnexion;
    
    private AuthService authService;
    private OffreService offreService;
    private CandidatureService candidatureService;
    private RecommendationService recommendationService;
    private Candidat candidatConnecte;
    
    @FXML
    public void initialize() {
        authService = DataManager.getInstance().getAuthService();
        offreService = DataManager.getInstance().getOffreService();
        candidatureService = DataManager.getInstance().getCandidatureService();
        recommendationService = DataManager.getInstance().getRecommendationService();
        
        candidatConnecte = authService.getCandidatConnecte();
        
        if (candidatConnecte != null) {
            updateUI();
            loadStatistiques();
            loadDernieresOffres();
            loadRecommandations();
        }
        
        // Listener pour activer/désactiver les boutons de recommandations
        tableRecommandations.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean selected = newSelection != null;
                btnVoirDetailsReco.setDisable(!selected);
                btnPostulerReco.setDisable(!selected);
            }
        );
    }
    
    private void updateUI() {
        lblNomCandidat.setText("Bienvenue, " + candidatConnecte.getPrenom() + " " + candidatConnecte.getNom());
        
        if (candidatConnecte instanceof Etudiant) {
            Etudiant etud = (Etudiant) candidatConnecte;
            lblTypeCandidat.setText("Étudiant - " + etud.getFiliere());
        } else if (candidatConnecte instanceof Alumni) {
            Alumni alumni = (Alumni) candidatConnecte;
            lblTypeCandidat.setText("Alumni - " + alumni.getPosteActuel());
        }
    }
    
    private void loadStatistiques() {
        int nbCandidatures = candidatureService.getNombreCandidaturesActives(candidatConnecte);
        lblNbCandidatures.setText(String.valueOf(nbCandidatures));
        
        int nbOffres = offreService.getOffresDisponibles().size();
        lblNbOffres.setText(String.valueOf(nbOffres));
    }
    
    private void loadDernieresOffres() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colEntreprise.setCellValueFactory(new PropertyValueFactory<>("entreprise"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        List<Offre> offres = offreService.getOffresDisponibles();
        ObservableList<OffreDisplay> offresList = FXCollections.observableArrayList();
        
        int count = 0;
        for (int i = offres.size() - 1; i >= 0 && count < 5; i--) {
            Offre offre = offres.get(i);
            offresList.add(new OffreDisplay(
                offre.getTitre(),
                offre.getTypeOffre(),
                offre.getEntreprise().getNom(),
                offre.getDatePublication().toString()
            ));
            count++;
        }
        
        tableDernieresOffres.setItems(offresList);
    }
    
    private void loadRecommandations() {
        colTitreReco.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colTypeReco.setCellValueFactory(new PropertyValueFactory<>("type"));
        colEntrepriseReco.setCellValueFactory(new PropertyValueFactory<>("entreprise"));
        colScoreReco.setCellValueFactory(new PropertyValueFactory<>("score"));
        
        ObservableList<RecommandationDisplay> recoList = FXCollections.observableArrayList();
        
        List<OffreRecommandee> recommandations;
        
        if (candidatConnecte instanceof Etudiant) {
            recommandations = recommendationService.getRecommandationsEtudiant(
                (Etudiant) candidatConnecte, 
                5
            );
        } else if (candidatConnecte instanceof Alumni) {
            recommandations = recommendationService.getRecommandationsAlumni(
                (Alumni) candidatConnecte, 
                5
            );
        } else {
            recommandations = null;
        }
        
        if (recommandations != null) {
            for (OffreRecommandee reco : recommandations) {
                Offre offre = reco.getOffre();
                recoList.add(new RecommandationDisplay(
                    offre.getId().toString(),
                    offre.getTitre(),
                    offre.getTypeOffre(),
                    offre.getEntreprise().getNom(),
                    reco.getScoreFormate()
                ));
            }
        }
        
        tableRecommandations.setItems(recoList);
    }
    
    @FXML
    private void handleVoirDetailsReco() {
        RecommandationDisplay selected = tableRecommandations.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Offre offre = findOffreById(selected.getIdOffre());
        if (offre == null) return;
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'offre recommandée");
        alert.setHeaderText(offre.getTitre() + " - Correspondance: " + selected.getScore());
        
        StringBuilder content = new StringBuilder();
        content.append("Type: ").append(offre.getTypeOffre()).append("\n\n");
        content.append("Entreprise: ").append(offre.getEntreprise().getNom()).append("\n");
        content.append("Secteur: ").append(offre.getEntreprise().getSecteur()).append("\n\n");
        content.append("Description:\n").append(offre.getDescription()).append("\n\n");
        
        // ✅ CORRECTION : Utiliser le nom complet de la classe pour éviter la confusion
        if (offre instanceof models.Stage) {
            models.Stage stageOffre = (models.Stage) offre;
            content.append("Domaine: ").append(stageOffre.getDomaine()).append("\n");
            content.append("Durée: ").append(stageOffre.getDureeEnMois()).append(" mois\n");
        } else if (offre instanceof Alternance) {
            Alternance alt = (Alternance) offre;
            content.append("Rythme: ").append(alt.getRythme()).append("\n");
            content.append("Durée: ").append(alt.getDureeEnMois()).append(" mois\n");
        } else if (offre instanceof ProjetFinEtudes) {
            ProjetFinEtudes pfe = (ProjetFinEtudes) offre;
            content.append("Sujet: ").append(pfe.getSujet()).append("\n");
            content.append("Technologies: ").append(pfe.getTechnologies()).append("\n");
        }
        
        content.append("\nDate de publication: ").append(offre.getDatePublication());
        if (offre.getDateExpiration() != null) {
            content.append("\nDate d'expiration: ").append(offre.getDateExpiration());
        }
        content.append("\nNombre de candidatures: ").append(offre.getCandidatures().size());
        
        content.append("\n\n💡 Pourquoi cette recommandation ?");
        content.append("\nCette offre correspond à votre profil avec un score de ").append(selected.getScore());
        content.append("\nbasé sur votre filière, niveau d'études et autres critères.");
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    @FXML
    private void handlePostulerReco() {
        RecommandationDisplay selected = tableRecommandations.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        if (candidatConnecte.getCandidaturesEnCours().stream()
            .anyMatch(o -> o.getId().toString().equals(selected.getIdOffre()))) {
            showError("Vous avez déjà postulé à cette offre");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la candidature");
        confirm.setHeaderText("Postuler à cette offre recommandée ?");
        confirm.setContentText("Offre: " + selected.getTitre() + "\n" +
                              "Entreprise: " + selected.getEntreprise() + "\n" +
                              "Correspondance: " + selected.getScore());
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean success = candidatureService.postulerOffre(candidatConnecte, selected.getIdOffre());
            
            if (success) {
                DataManager.getInstance().sauvegarder();
                showSuccess("Candidature envoyée avec succès !");
                loadStatistiques();
                loadRecommandations();
            } else {
                showError("Erreur lors de l'envoi de la candidature");
            }
        }
    }
    
    private Offre findOffreById(String id) {
        for (Offre offre : offreService.getAllOffres()) {
            if (offre.getId().toString().equals(id)) {
                return offre;
            }
        }
        return null;
    }
    
    @FXML
    private void handleDashboard() {
        // Déjà sur le dashboard
    }
    
    @FXML
    private void handleOffres() {
        navigateTo("/views/OffresListView.fxml", "Rechercher des offres");
    }
    
    @FXML
    private void handleCandidatures() {
        navigateTo("/views/MesCandidaturesView.fxml", "Mes candidatures");
    }
    
    @FXML
    private void handleForum() {
        navigateTo("/views/ForumView.fxml", "Forum");
    }
    
    @FXML
    private void handleProfil() {
        navigateTo("/views/MonProfilCandidatView.fxml", "Mon profil");
    }
    
    @FXML
    private void handleDeconnexion() {
        authService.logout();
        DataManager.getInstance().sauvegarder();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/WelcomeView.fxml"));
            VBox root = loader.load();
            
            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
            
            Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
            
            // Forcer la taille et position de la fenêtre
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Accueil");
            
            stage.setMaximized(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            BorderPane root = loader.load();
            
            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
            
            Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
            
            // Forcer la taille et position de la fenêtre
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            
            stage.setScene(scene);
            stage.setTitle("HecRecruit - " + title);
            
            stage.setMaximized(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur navigation: " + e.getMessage());
        }
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static class OffreDisplay {
        private final String titre;
        private final String type;
        private final String entreprise;
        private final String date;
        
        public OffreDisplay(String titre, String type, String entreprise, String date) {
            this.titre = titre;
            this.type = type;
            this.entreprise = entreprise;
            this.date = date;
        }
        
        public String getTitre() { return titre; }
        public String getType() { return type; }
        public String getEntreprise() { return entreprise; }
        public String getDate() { return date; }
    }
    
    public static class RecommandationDisplay {
        private final String idOffre;
        private final String titre;
        private final String type;
        private final String entreprise;
        private final String score;
        
        public RecommandationDisplay(String idOffre, String titre, String type, 
                                    String entreprise, String score) {
            this.idOffre = idOffre;
            this.titre = titre;
            this.type = type;
            this.entreprise = entreprise;
            this.score = score;
        }
        
        public String getIdOffre() { return idOffre; }
        public String getTitre() { return titre; }
        public String getType() { return type; }
        public String getEntreprise() { return entreprise; }
        public String getScore() { return score; }
    }
}