package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import models.*;
import service.*;
import utils.DataManager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Controller du profil entreprise
 * VERSION AVEC VALIDATION EN TEMPS RÉEL
 */
public class MonProfilEntrepriseController {

    /* ===================== CONSTANTES DE VALIDATION ===================== */
    
    // Regex pour téléphone : exactement 8 chiffres
    private static final Pattern TELEPHONE_PATTERN = Pattern.compile("^\\d{8}$");
    
    // Styles CSS pour feedback visuel
    private static final String STYLE_ERROR = "-fx-border-color: #E74C3C; -fx-border-width: 2px;";
    private static final String STYLE_SUCCESS = "-fx-border-color: #2ECC71; -fx-border-width: 2px;";
    private static final String STYLE_NORMAL = "";

    /* ===================== Champs liés à l'interface ===================== */

    @FXML private Label lblNom;
    @FXML private Label lblEmail;
    @FXML private Label lblMessage;
    @FXML private TextField txtSecteur;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtTelephone;

    /* ===================== Données métier & services ===================== */

    private Entreprise entreprise;
    private EntrepriseService entrepriseService;

    /**
     * Méthode appelée automatiquement au chargement du FXML
     */
    @FXML
    public void initialize() {

        entreprise = DataManager
                        .getInstance()
                        .getAuthService()
                        .getEntrepriseConnectee();

        entrepriseService = DataManager.getInstance().getEntrepriseService();

        if (entreprise != null) {
            loadData();
        }
        
        // ✅ AJOUT : Listeners pour validation en temps réel
        setupValidationListeners();
    }
    
    /**
     * ✅ NOUVEAU : Configure les listeners pour validation onChange
     */
    private void setupValidationListeners() {
        // Validation téléphone en temps réel
        txtTelephone.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                validateTelephoneField();
            } else {
                txtTelephone.setStyle(STYLE_NORMAL);
            }
        });
        
        // Validation champs non vides
        txtSecteur.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                txtSecteur.setStyle(STYLE_SUCCESS);
            } else {
                txtSecteur.setStyle(STYLE_NORMAL);
            }
        });
        
        txtAdresse.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                txtAdresse.setStyle(STYLE_SUCCESS);
            } else {
                txtAdresse.setStyle(STYLE_NORMAL);
            }
        });
    }
    
    /**
     * ✅ NOUVEAU : Valide le téléphone
     */
    private boolean validateTelephoneField() {
        String tel = txtTelephone.getText().trim();
        
        if (TELEPHONE_PATTERN.matcher(tel).matches()) {
            txtTelephone.setStyle(STYLE_SUCCESS);
            return true;
        } else {
            txtTelephone.setStyle(STYLE_ERROR);
            return false;
        }
    }

    /**
     * Charge les informations de l'entreprise dans les champs
     */
    private void loadData() {
        lblNom.setText(entreprise.getNom());
        lblEmail.setText(entreprise.getEmail());
        txtSecteur.setText(entreprise.getSecteur());
        txtAdresse.setText(entreprise.getAdresse());
        txtTelephone.setText(entreprise.getTelephone());
    }

    /**
     * ✅ MODIFIÉ : Sauvegarde les modifications du profil avec validation
     */
    @FXML
    private void handleSave() {

        // ✅ Validation des champs
        String secteur = txtSecteur.getText().trim();
        String adresse = txtAdresse.getText().trim();
        String telephone = txtTelephone.getText().trim();
        
        if (secteur.isEmpty()) {
            showError("Le secteur ne peut pas être vide");
            txtSecteur.setStyle(STYLE_ERROR);
            return;
        }
        
        if (adresse.isEmpty()) {
            showError("L'adresse ne peut pas être vide");
            txtAdresse.setStyle(STYLE_ERROR);
            return;
        }
        
        if (!TELEPHONE_PATTERN.matcher(telephone).matches()) {
            showError("Le téléphone doit contenir exactement 8 chiffres");
            txtTelephone.setStyle(STYLE_ERROR);
            return;
        }

        // Map contenant les nouvelles valeurs à modifier
        Map<String, String> infos = new HashMap<>();
        infos.put("secteur", secteur);
        infos.put("adresse", adresse);
        infos.put("telephone", telephone);

        // Appel du service métier pour mise à jour
        if (entrepriseService.modifierProfil(entreprise, infos)) {
            DataManager.getInstance().sauvegarder();
            lblMessage.setText("Profil mis à jour avec succès !");
            lblMessage.setVisible(true);
            lblMessage.setStyle("-fx-text-fill: #2ECC71;");
        } else {
            showError("Erreur lors de la mise à jour du profil");
        }
    }

    /**
     * Annule les modifications et recharge les données initiales
     */
    @FXML
    private void handleCancel() {
        loadData();
        lblMessage.setVisible(false);
        
        // ✅ AJOUT : Réinitialiser les styles
        txtSecteur.setStyle(STYLE_NORMAL);
        txtAdresse.setStyle(STYLE_NORMAL);
        txtTelephone.setStyle(STYLE_NORMAL);
    }

    /**
     * Retour vers le dashboard entreprise
     */
    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/EntrepriseDashboardView.fxml")
            );
            BorderPane root = loader.load();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) txtSecteur.getScene().getWindow();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setScene(scene);
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ✅ NOUVEAU : Affiche un message d'erreur
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}