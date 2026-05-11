package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
 * Controller du profil candidat
 * VERSION AVEC VALIDATION EN TEMPS RÉEL
 */
public class MonProfilCandidatController {

    /* ===================== CONSTANTES DE VALIDATION ===================== */
    
    // Regex pour téléphone : exactement 8 chiffres
    private static final Pattern TELEPHONE_PATTERN = Pattern.compile("^\\d{8}$");
    
    // Styles CSS pour feedback visuel
    private static final String STYLE_ERROR = "-fx-border-color: #E74C3C; -fx-border-width: 2px;";
    private static final String STYLE_SUCCESS = "-fx-border-color: #2ECC71; -fx-border-width: 2px;";
    private static final String STYLE_NORMAL = "";

    /* ===================== Champs FXML (communs) ===================== */

    @FXML private Label lblCIN;
    @FXML private Label lblNom;
    @FXML private Label lblPrenom;
    @FXML private Label lblEmail;
    @FXML private TextField txtTelephone;

    /* ===================== Champs spécifiques (VBox) ===================== */

    @FXML private VBox containerSpecific;
    @FXML private Label lblTitleSpecific;
    @FXML private Label lblField1;
    @FXML private TextField txtField1;
    @FXML private Label lblField2;
    @FXML private TextField txtField2;
    @FXML private Label lblField3;
    @FXML private TextField txtField3;
    @FXML private Label lblMessage;

    /* ===================== Services & utilisateur ===================== */

    private AuthService authService;
    private CandidatService candidatService;
    private Candidat candidatConnecte;

    /**
     * Méthode appelée automatiquement au chargement du FXML
     */
    @FXML
    public void initialize() {
        authService = DataManager.getInstance().getAuthService();
        candidatService = DataManager.getInstance().getCandidatService();
        candidatConnecte = authService.getCandidatConnecte();

        if (candidatConnecte != null) {
            loadProfilData();
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
        
        // Validation champs non vides pour les champs spécifiques
        txtField1.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                txtField1.setStyle(STYLE_SUCCESS);
            } else {
                txtField1.setStyle(STYLE_NORMAL);
            }
        });
        
        txtField2.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                txtField2.setStyle(STYLE_SUCCESS);
            } else {
                txtField2.setStyle(STYLE_NORMAL);
            }
        });
        
        txtField3.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                txtField3.setStyle(STYLE_SUCCESS);
            } else {
                txtField3.setStyle(STYLE_NORMAL);
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
     * Charge les données du candidat dans l'interface
     */
    private void loadProfilData() {

        lblCIN.setText(String.valueOf(candidatConnecte.getId()));
        lblNom.setText(candidatConnecte.getNom());
        lblPrenom.setText(candidatConnecte.getPrenom());
        lblEmail.setText(candidatConnecte.getEmail());
        txtTelephone.setText(candidatConnecte.getTelephone());

        if (candidatConnecte instanceof Etudiant) {
            Etudiant etud = (Etudiant) candidatConnecte;
            lblTitleSpecific.setText("🎓 Informations académiques");
            lblField1.setText("Niveau:");
            txtField1.setText(etud.getNiveau());
            lblField2.setText("Filière:");
            txtField2.setText(etud.getFiliere());
            lblField3.setText("Établissement:");
            txtField3.setText(etud.getEtablissement());

        } else if (candidatConnecte instanceof Alumni) {
            Alumni alumni = (Alumni) candidatConnecte;
            lblTitleSpecific.setText("💼 Informations professionnelles");
            lblField1.setText("Année de diplôme:");
            txtField1.setText(String.valueOf(alumni.getAnneeDiplome()));
            txtField1.setDisable(true);
            lblField2.setText("Poste actuel:");
            txtField2.setText(alumni.getPosteActuel());
            lblField3.setText("Entreprise actuelle:");
            txtField3.setText(alumni.getEntrepriseActuelle());
        }
    }

    /**
     * ✅ MODIFIÉ : Sauvegarde les modifications du profil avec validation
     */
    @FXML
    private void handleSave() {
        try {
            Map<String, String> nouvellesInfos = new HashMap<>();

            // ✅ Validation téléphone
            String telephone = txtTelephone.getText().trim();
            if (telephone.isEmpty()) {
                showError("Le téléphone ne peut pas être vide");
                txtTelephone.setStyle(STYLE_ERROR);
                return;
            }
            
            if (!TELEPHONE_PATTERN.matcher(telephone).matches()) {
                showError("Le téléphone doit contenir exactement 8 chiffres");
                txtTelephone.setStyle(STYLE_ERROR);
                return;
            }
            
            nouvellesInfos.put("telephone", telephone);

            // ✅ Validation champs spécifiques
            if (candidatConnecte instanceof Etudiant) {

                String niveau = txtField1.getText().trim();
                String filiere = txtField2.getText().trim();
                String etablissement = txtField3.getText().trim();

                if (niveau.isEmpty() || filiere.isEmpty() || etablissement.isEmpty()) {
                    showError("Tous les champs doivent être remplis");
                    if (niveau.isEmpty()) txtField1.setStyle(STYLE_ERROR);
                    if (filiere.isEmpty()) txtField2.setStyle(STYLE_ERROR);
                    if (etablissement.isEmpty()) txtField3.setStyle(STYLE_ERROR);
                    return;
                }

                nouvellesInfos.put("niveau", niveau);
                nouvellesInfos.put("filiere", filiere);
                nouvellesInfos.put("etablissement", etablissement);

            } else if (candidatConnecte instanceof Alumni) {

                String poste = txtField2.getText().trim();
                String entreprise = txtField3.getText().trim();

                nouvellesInfos.put("posteActuel", poste);
                nouvellesInfos.put("entrepriseActuelle", entreprise);
            }

            // Mise à jour via le service
            boolean success = candidatService.modifierProfil(
                candidatConnecte,
                nouvellesInfos
            );

            if (success) {
                DataManager.getInstance().sauvegarder();
                showMessage("Profil mis à jour avec succès !");
            } else {
                showError("Erreur lors de la mise à jour du profil");
            }

        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Annule les modifications et recharge les données originales
     */
    @FXML
    private void handleCancel() {
        loadProfilData();
        hideMessage();
        
        // ✅ AJOUT : Réinitialiser les styles
        txtTelephone.setStyle(STYLE_NORMAL);
        txtField1.setStyle(STYLE_NORMAL);
        txtField2.setStyle(STYLE_NORMAL);
        txtField3.setStyle(STYLE_NORMAL);
    }

    /**
     * Retour au dashboard candidat
     */
    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/CandidatDashboardView.fxml")
            );
            BorderPane root = loader.load();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) txtTelephone.getScene().getWindow();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Dashboard Candidat");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===================== Méthodes utilitaires UI ===================== */

    private void showMessage(String message) {
        lblMessage.setText(message);
        lblMessage.setVisible(true);
        lblMessage.setStyle("-fx-text-fill: #2ECC71;");
    }

    private void hideMessage() {
        lblMessage.setVisible(false);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}