package controllers;

// Imports pour les listes observables (TableView)
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Imports JavaFX (FXML + UI)
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

// Imports métier
import models.*;
import service.*;
import utils.DataManager;

/**
 * Controller de la vue "Mes Offres"
 * Permet à une entreprise de :
 *  - voir ses offres publiées
 *  - consulter les candidats d'une offre
 *  - supprimer une offre
 *  - créer une nouvelle offre
 */
public class MesOffresController {

    // Label affichant le nombre d'offres actives
    @FXML private Label lblNbOffres;

    // TableView affichant les offres
    @FXML private TableView<OffreDisplay> tableOffres;

    // Colonnes de la table
    @FXML private TableColumn<OffreDisplay, String> colTitre;
    @FXML private TableColumn<OffreDisplay, String> colType;
    @FXML private TableColumn<OffreDisplay, String> colDate;
    @FXML private TableColumn<OffreDisplay, String> colExpiration;
    @FXML private TableColumn<OffreDisplay, String> colCandidatures;
    @FXML private TableColumn<OffreDisplay, String> colStatut;

    // Boutons d'action
    @FXML private Button btnVoirCandidats;
    @FXML private Button btnSupprimer;

    // Services
    private AuthService authService;
    private OffreService offreService;

    // Entreprise actuellement connectée
    private Entreprise entrepriseConnectee;

    /**
     * Méthode appelée automatiquement après le chargement du FXML
     */
    @FXML
    public void initialize() {
        // Récupération des services via le DataManager (Singleton)
        authService = DataManager.getInstance().getAuthService();
        offreService = DataManager.getInstance().getOffreService();

        // Récupération de l'entreprise connectée
        entrepriseConnectee = authService.getEntrepriseConnectee();

        // Configuration des colonnes de la table
        setupTable();

        // Chargement des offres de l'entreprise
        loadOffres();

        // Listener : active/désactive les boutons selon la sélection
        tableOffres.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean selected = newSelection != null;
                btnVoirCandidats.setDisable(!selected);
                btnSupprimer.setDisable(!selected);
            }
        );
    }

    /**
     * Associe chaque colonne à une propriété de OffreDisplay
     */
    private void setupTable() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("datePublication"));
        colExpiration.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));
        colCandidatures.setCellValueFactory(new PropertyValueFactory<>("nbCandidatures"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    /**
     * Charge les offres publiées par l'entreprise connectée
     */
    private void loadOffres() {
        ObservableList<OffreDisplay> offresData = FXCollections.observableArrayList();

        int nbActives = 0;

        // Parcours des offres publiées
        for (Offre offre : entrepriseConnectee.getOffresPubliees()) {

            // Déterminer le statut de l'offre
            String statut = offre.estExpiree() ? "Expirée" : "Active";
            if (!offre.estExpiree()) nbActives++;

            // Création d'un objet d'affichage pour la TableView
            offresData.add(new OffreDisplay(
                offre.getId().toString(),
                offre.getTitre(),
                offre.getTypeOffre(),
                offre.getDatePublication().toString(),
                offre.getDateExpiration() != null
                    ? offre.getDateExpiration().toString()
                    : "Non définie",
                String.valueOf(offre.getCandidatures().size()),
                statut
            ));
        }

        // Injection des données dans la table
        tableOffres.setItems(offresData);

        // Mise à jour du nombre d'offres actives
        lblNbOffres.setText(String.valueOf(nbActives));
    }

    /**
     * Affiche les candidats ayant postulé à l'offre sélectionnée
     */
    @FXML
    private void handleVoirCandidats() {
        OffreDisplay selected = tableOffres.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/CandidatsOffreView.fxml")
            );
            BorderPane root = loader.load();

            // Passage de l'ID de l'offre au controller suivant
            CandidatsOffreController controller = loader.getController();
            controller.setOffreId(selected.getId());

            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) tableOffres.getScene().getWindow();
            
            // Forcer la taille et position de la fenêtre
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Candidats de l'offre");
            
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime une offre après confirmation
     */
    @FXML
    private void handleSupprimer() {
        OffreDisplay selected = tableOffres.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Boîte de confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText("Supprimer cette offre ?");
        confirm.setContentText(
            "Cette action est irréversible. Tous les candidats seront notifiés."
        );

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean success = offreService.supprimerOffre(
                selected.getId(),
                entrepriseConnectee
            );

            if (success) {
                DataManager.getInstance().sauvegarder();
                showSuccess("Offre supprimée avec succès !");
                loadOffres(); // Rafraîchir la table
            } else {
                showError("Erreur lors de la suppression");
            }
        }
    }

    /**
     * Navigation vers l'écran de création d'offre
     */
    @FXML
    private void handleCreerOffre() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/CreerOffreView.fxml")
            );
            BorderPane root = loader.load();

            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) tableOffres.getScene().getWindow();
            
            // Forcer la taille et position de la fenêtre
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Créer une offre");
            
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retour au dashboard entreprise
     */
    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/EntrepriseDashboardView.fxml")
            );
            BorderPane root = loader.load();

            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) tableOffres.getScene().getWindow();
            
            // Forcer la taille et position de la fenêtre
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Dashboard Entreprise");
            
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthodes utilitaires pour les alertes
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Classe interne utilisée uniquement pour l'affichage
     * (DTO / ViewModel pour la TableView)
     */
    public static class OffreDisplay {

        private final String id;
        private final String titre;
        private final String type;
        private final String datePublication;
        private final String dateExpiration;
        private final String nbCandidatures;
        private final String statut;

        public OffreDisplay(String id, String titre, String type,
                            String datePublication, String dateExpiration,
                            String nbCandidatures, String statut) {
            this.id = id;
            this.titre = titre;
            this.type = type;
            this.datePublication = datePublication;
            this.dateExpiration = dateExpiration;
            this.nbCandidatures = nbCandidatures;
            this.statut = statut;
        }

        // Getters nécessaires pour la TableView
        public String getId() { return id; }
        public String getTitre() { return titre; }
        public String getType() { return type; }
        public String getDatePublication() { return datePublication; }
        public String getDateExpiration() { return dateExpiration; }
        public String getNbCandidatures() { return nbCandidatures; }
        public String getStatut() { return statut; }
    }
}