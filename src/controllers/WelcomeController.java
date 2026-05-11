package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

/**
 * Controller de la page d’accueil de l’application HecRecruit.
 * Cette page permet à l’utilisateur de choisir entre
 * se connecter ou s’inscrire.
 */
public class WelcomeController {

    /* ===================== Composants FXML ===================== */

    // Bouton menant à l’écran de connexion
    @FXML
    private Button btnLogin;

    // Bouton menant à l’écran d’inscription
    @FXML
    private Button btnRegister;

    /**
     * Action déclenchée lorsque l’utilisateur clique sur "Se connecter".
     */
    @FXML
    private void handleLogin() {
        navigateToTypeSelection("login");
    }

    /**
     * Action déclenchée lorsque l’utilisateur clique sur "S’inscrire".
     */
    @FXML
    private void handleRegister() {
        navigateToTypeSelection("register");
    }

    /**
     * Navigation vers l’écran de sélection du profil
     * en précisant le mode (login ou register).
     */
    private void navigateToTypeSelection(String mode) {
        try {
            // Chargement de la vue
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/TypeSelectionView.fxml"));
            VBox root = loader.load();

            // Passage du mode au controller
            TypeSelectionController controller = loader.getController();
            controller.setMode(mode);

            // Obtenir la taille de l'écran UNE SEULE FOIS
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Création de la scène
            Scene scene = new Scene(
                    root,
                    screenBounds.getWidth(),
                    screenBounds.getHeight()
            );
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css")
                            .toExternalForm()
            );

            // Récupération du stage actuel
            Stage stage = (Stage) btnLogin.getScene().getWindow();

            // Appliquer taille et position
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());

            stage.setScene(scene);
            stage.setTitle("HecRecruit - Sélection du profil");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur navigation : " + e.getMessage());
        }
    }
}
