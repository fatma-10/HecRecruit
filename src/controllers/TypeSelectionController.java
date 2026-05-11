package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

/**
 * Controller responsable de l'écran de sélection du type d'utilisateur
 * (Candidat ou Entreprise), aussi bien pour la connexion que l'inscription.
 */
public class TypeSelectionController {

    /* ===================== Composants FXML ===================== */

    // Titre affiché en haut de la page
    @FXML
    private Label lblTitle;

    // Bouton retour vers l'écran d'accueil
    @FXML
    private Button btnBack;

    /* ===================== Données métier ===================== */

    // Mode courant : "login" ou "register"
    private String mode;

    /**
     * Définit le mode de l'écran (connexion ou inscription).
     * Cette méthode est appelée depuis l'écran précédent.
     */
    public void setMode(String mode) {
        this.mode = mode;
        updateTitle();
    }

    /**
     * Met à jour le titre de la page selon le mode choisi.
     */
    private void updateTitle() {
        if ("login".equals(mode)) {
            lblTitle.setText("Connectez-vous en tant que...");
        } else if ("register".equals(mode)) {
            lblTitle.setText("Inscrivez-vous en tant que...");
        }
    }

    /**
     * Action déclenchée lorsque l'utilisateur choisit "Candidat".
     */
    @FXML
    private void handleCandidatSelection(MouseEvent event) {
        navigateToAuthView("candidat");
    }

    /**
     * Action déclenchée lorsque l'utilisateur choisit "Entreprise".
     */
    @FXML
    private void handleEntrepriseSelection(MouseEvent event) {
        navigateToAuthView("entreprise");
    }

    /**
     * Navigation vers la vue de connexion ou d'inscription
     * selon le mode, en passant le type d'utilisateur choisi.
     */
    private void navigateToAuthView(String userType) {
        try {
            String fxmlFile = "";
            String title = "";

            // Déterminer la vue à charger selon le mode
            if ("login".equals(mode)) {
                fxmlFile = "/views/LoginView.fxml";
                title = "HecRecruit - Connexion";
            } else if ("register".equals(mode)) {
                fxmlFile = "/views/RegisterView.fxml";
                title = "HecRecruit - Inscription";
            }

            // Chargement de la vue
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(fxmlFile));
            VBox root = loader.load();

            // Passer le type d'utilisateur au controller cible
            if ("login".equals(mode)) {
                LoginController controller = loader.getController();
                controller.setUserType(userType);
            } else {
                RegisterController controller = loader.getController();
                controller.setUserType(userType);
            }

            // Création de la scène
            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css")
                            .toExternalForm());

            // Changement de scène
            Stage stage = (Stage) lblTitle.getScene().getWindow();
            
            // Forcer la taille et position de la fenêtre
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            
            stage.setScene(scene);
            stage.setTitle(title);
            
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur navigation: " + e.getMessage());
        }
    }

    /**
     * Retour à l'écran d'accueil (WelcomeView).
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/views/WelcomeView.fxml"));
            VBox root = loader.load();

            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css")
                            .toExternalForm());

            Stage stage = (Stage) btnBack.getScene().getWindow();
            
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
}