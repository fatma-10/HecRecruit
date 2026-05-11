package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import models.*;
import service.*;
import utils.DataManager;

/**
 * Controller responsable de la gestion de la wishlist
 * d'une entreprise (liste des candidats favoris).
 */
public class WishlistController {

    /* ===================== Composants FXML ===================== */

    // Label affichant le nombre de candidats favoris
    @FXML private Label lblNbCandidats;

    // Table affichant les candidats présents dans la wishlist
    @FXML private TableView<CandidatsOffreController.CandidatDisplay> tableWishlist;

    // Colonnes de la table
    @FXML private TableColumn<CandidatsOffreController.CandidatDisplay, String>
            colCIN, colNom, colPrenom, colEmail, colTelephone, colType;

    // Bouton permettant de retirer un candidat de la wishlist
    @FXML private Button btnRetirer;

    /* ===================== Données métier ===================== */

    // Service de gestion des candidatures et wishlist
    private CandidatureService candidatureService;

    // Entreprise actuellement connectée
    private Entreprise entreprise;

    /**
     * Méthode appelée automatiquement après le chargement du FXML.
     */
    @FXML
    public void initialize() {

        // Récupération du service de candidature
        candidatureService =
                DataManager.getInstance().getCandidatureService();

        // Récupération de l'entreprise connectée
        entreprise =
                DataManager.getInstance()
                        .getAuthService()
                        .getEntrepriseConnectee();

        // Association des colonnes avec les attributs du CandidatDisplay
        colCIN.setCellValueFactory(new PropertyValueFactory<>("cin"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Chargement initial de la wishlist
        loadWishlist();

        // Activer / désactiver le bouton selon la sélection
        tableWishlist.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (obs, old, newVal) ->
                                btnRetirer.setDisable(newVal == null)
                );
    }

    /**
     * Charge les candidats favoris de l'entreprise
     * et les affiche dans la TableView.
     */
    private void loadWishlist() {

        ObservableList<CandidatsOffreController.CandidatDisplay> data =
                FXCollections.observableArrayList();

        // Parcours des candidats présents dans la wishlist
        for (Candidat c : entreprise.getWishlist()) {

            // Déterminer le type de candidat
            String type = c instanceof Etudiant ? "Étudiant" : "Alumni";

            // Création d'un objet d'affichage
            data.add(new CandidatsOffreController.CandidatDisplay(
                    String.valueOf(c.getId()),
                    c.getNom(),
                    c.getPrenom(),
                    c.getEmail(),
                    c.getTelephone(),
                    type
            ));
        }

        // Mise à jour de la table et du label
        tableWishlist.setItems(data);
        lblNbCandidats.setText(
                data.size() + " candidat(s) favori(s)");
    }

    /**
     * Retire le candidat sélectionné de la wishlist.
     */
    @FXML
    private void handleRetirer() {

        CandidatsOffreController.CandidatDisplay selected =
                tableWishlist.getSelectionModel().getSelectedItem();

        if (selected != null) {

            // Demande de confirmation
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setContentText(
                    "Retirer ce candidat de la wishlist ?");

            if (confirm.showAndWait().get() == ButtonType.OK) {

                boolean success =
                        candidatureService.retirerWishlist(
                                entreprise, selected.getCin());

                if (success) {
                    // Sauvegarde et rafraîchissement
                    DataManager.getInstance().sauvegarder();
                    loadWishlist();
                }
            }
        }
    }

    /**
     * Retour au dashboard de l'entreprise.
     */
    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/views/EntrepriseDashboardView.fxml"));
            BorderPane root = loader.load();

            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource(
                            "/application/application.css")
                            .toExternalForm());

            Stage stage =
                    (Stage) tableWishlist.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(
                    "HecRecruit - Dashboard Entreprise");
            
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}