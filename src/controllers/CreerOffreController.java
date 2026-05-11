package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import service.*;
import utils.DataManager;
import models.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur JavaFX pour la création d'une nouvelle offre par une entreprise.
 * VERSION AVEC VALIDATION EN TEMPS RÉEL
 */
public class CreerOffreController {

    /* ===================== CONSTANTES DE VALIDATION ===================== */
    
    // Styles CSS pour feedback visuel
    private static final String STYLE_ERROR = "-fx-border-color: #E74C3C; -fx-border-width: 2px;";
    private static final String STYLE_SUCCESS = "-fx-border-color: #2ECC71; -fx-border-width: 2px;";
    private static final String STYLE_NORMAL = "";

    /* ===================== COMPOSANTS FXML DU FORMULAIRE ===================== */

    @FXML private TextField txtTitre;
    @FXML private ComboBox<String> comboType;
    @FXML private TextArea txtDescription;
    @FXML private VBox containerSpecific;
    @FXML private Label lblField1;
    @FXML private TextField txtField1;
    @FXML private Label lblField2;
    @FXML private TextField txtField2;
    @FXML private Label lblError;

    /* ===================== SERVICES ET DONNÉES ===================== */

    private AuthService authService;
    private OffreService offreService;
    private Entreprise entrepriseConnectee;

    /**
     * Méthode appelée automatiquement au chargement du FXML.
     */
    @FXML
    public void initialize() {
        authService = DataManager.getInstance().getAuthService();
        offreService = DataManager.getInstance().getOffreService();
        entrepriseConnectee = authService.getEntrepriseConnectee();

        comboType.setItems(FXCollections.observableArrayList(
            "Stage",
            "Alternance",
            "Projet Fin d'Etudes"
        ));

        comboType.valueProperty().addListener((obs, oldVal, newVal) -> updateSpecificFields(newVal));

        containerSpecific.setVisible(false);
        containerSpecific.setManaged(false);
        
        // ✅ AJOUT : Listeners pour validation en temps réel
        setupValidationListeners();
    }
    
    /**
     * ✅ NOUVEAU : Configure les listeners pour validation onChange
     */
    private void setupValidationListeners() {
        // Validation titre en temps réel
        txtTitre.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                txtTitre.setStyle(STYLE_SUCCESS);
                lblError.setVisible(false);
            } else {
                txtTitre.setStyle(STYLE_NORMAL);
            }
        });
        
        // Validation description en temps réel
        txtDescription.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                txtDescription.setStyle(STYLE_SUCCESS);
                lblError.setVisible(false);
            } else {
                txtDescription.setStyle(STYLE_NORMAL);
            }
        });
        
        // Validation type (ComboBox)
        comboType.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                comboType.setStyle(STYLE_SUCCESS);
                lblError.setVisible(false);
            } else {
                comboType.setStyle(STYLE_NORMAL);
            }
        });
        
        // Validation champs spécifiques en temps réel
        txtField1.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                txtField1.setStyle(STYLE_SUCCESS);
                lblError.setVisible(false);
            } else {
                txtField1.setStyle(STYLE_NORMAL);
            }
        });
        
        txtField2.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                // Vérification spéciale pour les champs numériques (durée)
                String type = comboType.getValue();
                if (type != null && (type.equals("Stage") || type.equals("Alternance"))) {
                    try {
                        int duree = Integer.parseInt(newVal.trim());
                        if (duree > 0) {
                            txtField2.setStyle(STYLE_SUCCESS);
                        } else {
                            txtField2.setStyle(STYLE_ERROR);
                        }
                    } catch (NumberFormatException e) {
                        txtField2.setStyle(STYLE_ERROR);
                    }
                } else {
                    txtField2.setStyle(STYLE_SUCCESS);
                }
                lblError.setVisible(false);
            } else {
                txtField2.setStyle(STYLE_NORMAL);
            }
        });
    }

    /**
     * Affiche les bons labels et placeholders selon le type choisi.
     */
    private void updateSpecificFields(String type) {
        if (type == null) {
            containerSpecific.setVisible(false);
            containerSpecific.setManaged(false);
            return;
        }

        containerSpecific.setVisible(true);
        containerSpecific.setManaged(true);

        switch (type) {
            case "Stage":
                lblField1.setText("Domaine *");
                txtField1.setPromptText("Ex: Informatique");
                lblField2.setText("Durée (mois) *");
                txtField2.setPromptText("Ex: 3");
                break;
            case "Alternance":
                lblField1.setText("Rythme *");
                txtField1.setPromptText("Ex: 3 jours/2 jours");
                lblField2.setText("Durée (mois) *");
                txtField2.setPromptText("Ex: 12");
                break;
            case "Projet Fin d'Etudes":
                lblField1.setText("Sujet *");
                txtField1.setPromptText("Ex: Développement application mobile");
                lblField2.setText("Technologies *");
                txtField2.setPromptText("Ex: React Native, Node.js");
                break;
        }
        
        // ✅ Réinitialiser les styles des champs spécifiques
        txtField1.setStyle(STYLE_NORMAL);
        txtField2.setStyle(STYLE_NORMAL);
    }

    /**
     * ✅ MODIFIÉ : Lors du clic sur "Publier" avec validation complète
     */
    @FXML
    private void handlePublier() {

        // Lecture des champs généraux
        String titre = txtTitre.getText().trim();
        String type = comboType.getValue();
        String description = txtDescription.getText().trim();

        // ✅ Validation de base avec feedback visuel
        if (titre.isEmpty()) {
            showError("Le titre est obligatoire");
            txtTitre.setStyle(STYLE_ERROR);
            return;
        }
        
        if (type == null) {
            showError("Veuillez sélectionner un type d'offre");
            comboType.setStyle(STYLE_ERROR);
            return;
        }
        
        if (description.isEmpty()) {
            showError("La description est obligatoire");
            txtDescription.setStyle(STYLE_ERROR);
            return;
        }

        // Lecture des champs spécifiques
        String field1 = txtField1.getText().trim();
        String field2 = txtField2.getText().trim();

        if (field1.isEmpty()) {
            showError("Veuillez remplir tous les champs spécifiques");
            txtField1.setStyle(STYLE_ERROR);
            return;
        }
        
        if (field2.isEmpty()) {
            showError("Veuillez remplir tous les champs spécifiques");
            txtField2.setStyle(STYLE_ERROR);
            return;
        }

        // Création de la map contenant les infos supplémentaires
        Map<String, String> infosSuppl = new HashMap<>();

        switch (type) {
            case "Stage":
                infosSuppl.put("domaine", field1);
                try {
                    int duree = Integer.parseInt(field2);
                    if (duree <= 0) throw new NumberFormatException();
                    infosSuppl.put("duree", field2);
                } catch (NumberFormatException e) {
                    showError("La durée doit être un nombre positif");
                    txtField2.setStyle(STYLE_ERROR);
                    return;
                }
                break;
            case "Alternance":
                infosSuppl.put("rythme", field1);
                try {
                    int duree = Integer.parseInt(field2);
                    if (duree <= 0) throw new NumberFormatException();
                    infosSuppl.put("duree", field2);
                } catch (NumberFormatException e) {
                    showError("La durée doit être un nombre positif");
                    txtField2.setStyle(STYLE_ERROR);
                    return;
                }
                break;
            case "Projet Fin d'Etudes":
                infosSuppl.put("sujet", field1);
                infosSuppl.put("technologies", field2);
                break;
        }

        // Appel du service métier pour créer l'offre
        boolean success = offreService.creerOffre(
                titre,
                description,
                type.toLowerCase(),
                entrepriseConnectee,
                infosSuppl
        );

        if (success) {
            DataManager.getInstance().sauvegarder();
            showSuccessAndReturn("Offre créée avec succès !");
        } else {
            showError("Erreur lors de la création de l'offre");
        }
    }

    /**
     * ✅ MODIFIÉ : Réinitialise tous les champs du formulaire + styles
     */
    @FXML
    private void handleReset() {
        txtTitre.clear();
        comboType.setValue(null);
        txtDescription.clear();
        txtField1.clear();
        txtField2.clear();
        lblError.setVisible(false);
        
        // ✅ Réinitialiser les styles
        txtTitre.setStyle(STYLE_NORMAL);
        comboType.setStyle(STYLE_NORMAL);
        txtDescription.setStyle(STYLE_NORMAL);
        txtField1.setStyle(STYLE_NORMAL);
        txtField2.setStyle(STYLE_NORMAL);
    }

    /**
     * Retour à la page des offres
     */
    @FXML
    private void handleRetour() {
        navigateToOffres();
    }

    /**
     * Affiche la vue "MesOffresView"
     */
    private void navigateToOffres() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MesOffresView.fxml"));
            BorderPane root = loader.load();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

            Stage stage = (Stage) txtTitre.getScene().getWindow();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Mes offres");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche un message d'erreur dans le label rouge en bas
     */
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    /**
     * Affiche une alerte de succès, puis retourne à la liste des offres
     */
    private void showSuccessAndReturn(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
        navigateToOffres();
    }
}