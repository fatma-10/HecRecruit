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

/**
 * Contrôleur du Forum.
 * VERSION AVEC VALIDATION EN TEMPS RÉEL
 */
public class ForumController {

    /* ===================== CONSTANTES DE VALIDATION ===================== */
    
    // Styles CSS pour feedback visuel
    private static final String STYLE_ERROR = "-fx-border-color: #E74C3C; -fx-border-width: 2px;";
    private static final String STYLE_SUCCESS = "-fx-border-color: #2ECC71; -fx-border-width: 2px;";
    private static final String STYLE_NORMAL = "";
    
    // Longueurs min/max pour les messages
    private static final int MIN_MESSAGE_LENGTH = 5;
    private static final int MAX_MESSAGE_LENGTH = 500;

    /* ===================== COMPOSANTS FXML ===================== */

    @FXML private ListView<String> listMessages;
    @FXML private TextArea txtMessage;

    /* ===================== SERVICES ===================== */

    private ForumService forumService;
    private AuthService authService;

    /**
     * Méthode appelée automatiquement au chargement du FXML.
     */
    @FXML
    public void initialize() {

        forumService = DataManager.getInstance().getForumService();
        authService = DataManager.getInstance().getAuthService();

        loadMessages();
        
        // ✅ AJOUT : Listeners pour validation en temps réel
        setupValidationListeners();
    }
    
    /**
     * ✅ NOUVEAU : Configure les listeners pour validation onChange
     */
    private void setupValidationListeners() {
        // Validation message en temps réel
        txtMessage.textProperty().addListener((obs, old, newVal) -> {
            int length = newVal.trim().length();
            
            if (length == 0) {
                txtMessage.setStyle(STYLE_NORMAL);
            } else if (length < MIN_MESSAGE_LENGTH) {
                txtMessage.setStyle(STYLE_ERROR);
            } else if (length > MAX_MESSAGE_LENGTH) {
                txtMessage.setStyle(STYLE_ERROR);
            } else {
                txtMessage.setStyle(STYLE_SUCCESS);
            }
        });
    }

    /**
     * Charge et affiche tous les messages du forum
     */
    private void loadMessages() {

        listMessages.getItems().clear();

        for (Forum f : forumService.getAllCommentaires()) {

            String type = f.isEstEtudiant() ? "👤 Candidat" : "🏢 Entreprise";

            String msg = String.format(
                "[%s] %s (%s):\n%s\n[%s]\n",
                type,
                f.getAuteur(),
                f.getEmailAuteur(),
                f.getMessage(),
                f.getDatePublication().toString()
            );

            listMessages.getItems().add(msg);
        }
    }

    /**
     * ✅ MODIFIÉ : Publier un nouveau message avec validation
     */
    @FXML
    private void handlePublier() {

        String msg = txtMessage.getText().trim();

        // ✅ Validation : message non vide
        if (msg.isEmpty()) {
            showError("Le message ne peut pas être vide");
            txtMessage.setStyle(STYLE_ERROR);
            return;
        }
        
        // ✅ Validation : longueur minimale
        if (msg.length() < MIN_MESSAGE_LENGTH) {
            showError("Le message doit contenir au moins " + MIN_MESSAGE_LENGTH + " caractères");
            txtMessage.setStyle(STYLE_ERROR);
            return;
        }
        
        // ✅ Validation : longueur maximale
        if (msg.length() > MAX_MESSAGE_LENGTH) {
            showError("Le message ne peut pas dépasser " + MAX_MESSAGE_LENGTH + " caractères");
            txtMessage.setStyle(STYLE_ERROR);
            return;
        }

        String auteur = "";
        String email = "";
        boolean estEtudiant = false;

        // Récupération des informations de l'utilisateur connecté
        if (authService.estCandidatConnecte()) {
            Candidat c = authService.getCandidatConnecte();
            auteur = c.getPrenom() + " " + c.getNom();
            email = c.getEmail();
            estEtudiant = true;

        } else if (authService.estEntrepriseConnectee()) {
            Entreprise e = authService.getEntrepriseConnectee();
            auteur = e.getNom();
            email = e.getEmail();
            estEtudiant = false;
        }

        // Appel du service pour ajouter le commentaire
        boolean success = forumService.ajouterCommentaire(
                auteur,
                email,
                msg,
                estEtudiant
        );

        if (success) {
            DataManager.getInstance().sauvegarder();
            txtMessage.clear();
            txtMessage.setStyle(STYLE_NORMAL);
            loadMessages();
            showSuccess("Message publié avec succès !");
        }
    }

    /**
     * Rafraîchir manuellement les messages du forum
     */
    @FXML
    private void handleRefresh() {
        loadMessages();
    }

    /**
     * Retourner vers le dashboard approprié
     */
    @FXML
    private void handleRetour() {

        try {
            String fxml = authService.estCandidatConnecte()
                    ? "/views/CandidatDashboardView.fxml"
                    : "/views/EntrepriseDashboardView.fxml";

            String title = authService.estCandidatConnecte()
                    ? "Dashboard Candidat"
                    : "Dashboard Entreprise";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            BorderPane root = loader.load();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) listMessages.getScene().getWindow();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setScene(scene);
            stage.setTitle("HecRecruit - " + title);
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===================== BOITES DE DIALOGUE ===================== */

    /**
     * Affiche un message de succès
     */
    private void showSuccess(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.showAndWait();
    }
}