package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import service.AuthService;
import utils.DataManager;

import java.util.regex.Pattern;

/**
 * Contrôleur de la page de connexion.
 * Gère la connexion des candidats et des entreprises,
 * la validation des champs et la redirection vers le bon dashboard.
 * VERSION AVEC VALIDATION EN TEMPS RÉEL
 */
public class LoginController {

    /* ===================== CONSTANTES DE VALIDATION ===================== */
    
    // Regex pour email IHEC
    private static final Pattern EMAIL_IHEC_PATTERN = Pattern.compile(
        "^[a-z]+\\.[a-z]+\\.\\d{4}@ihec\\.ucar\\.tn$"
    );
    
    // Regex pour email entreprise
    private static final Pattern EMAIL_ENTREPRISE_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Styles CSS pour feedback visuel
    private static final String STYLE_ERROR = "-fx-border-color: #E74C3C; -fx-border-width: 2px;";
    private static final String STYLE_SUCCESS = "-fx-border-color: #2ECC71; -fx-border-width: 2px;";
    private static final String STYLE_NORMAL = "";

    /* ===================== COMPOSANTS FXML ===================== */

    @FXML private Text iconText;
    @FXML private Label lblTitle;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Label lblRegisterLink;
    @FXML private Button btnLogin;
    @FXML private Button btnBack;

    /* ===================== DONNÉES ET SERVICES ===================== */

    private String userType;
    private AuthService authService;

    /**
     * Méthode appelée automatiquement au chargement du FXML.
     */
    @FXML
    public void initialize() {
        authService = DataManager.getInstance().getAuthService();
        lblError.setVisible(false);
        
        // ✅ AJOUT : Listeners pour validation en temps réel
        setupValidationListeners();
    }
    
    /**
     * ✅ NOUVEAU : Configure les listeners pour validation onChange
     */
    private void setupValidationListeners() {
        // Validation email en temps réel
        txtEmail.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                validateEmailField();
            } else {
                txtEmail.setStyle(STYLE_NORMAL);
            }
        });
        
        // Validation mot de passe en temps réel (vérification non vide)
        txtPassword.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.isEmpty()) {
                txtPassword.setStyle(STYLE_SUCCESS);
            } else {
                txtPassword.setStyle(STYLE_NORMAL);
            }
        });
    }
    
    /**
     * ✅ NOUVEAU : Valide le format de l'email selon le type d'utilisateur
     */
    private void validateEmailField() {
        String email = txtEmail.getText().trim().toLowerCase();
        
        if ("candidat".equals(userType)) {
            if (EMAIL_IHEC_PATTERN.matcher(email).matches()) {
                txtEmail.setStyle(STYLE_SUCCESS);
            } else {
                txtEmail.setStyle(STYLE_ERROR);
            }
        } else {
            if (EMAIL_ENTREPRISE_PATTERN.matcher(email).matches()) {
                txtEmail.setStyle(STYLE_SUCCESS);
            } else {
                txtEmail.setStyle(STYLE_ERROR);
            }
        }
    }

    /**
     * Méthode appelée depuis la vue précédente
     * pour définir le type d'utilisateur.
     */
    public void setUserType(String userType) {
        this.userType = userType;
        updateUI();
    }

    /**
     * Met à jour l'interface selon le type d'utilisateur.
     */
    private void updateUI() {
        if ("candidat".equals(userType)) {
            iconText.setText("👤");
            lblTitle.setText("Connexion Candidat");
        } else if ("entreprise".equals(userType)) {
            iconText.setText("🏢");
            lblTitle.setText("Connexion Entreprise");
        }
    }

    /**
     * ✅ MODIFIÉ : Action du bouton "Connexion" avec validation
     */
    @FXML
    private void handleLogin() {

        String email = txtEmail.getText().trim().toLowerCase();
        String password = txtPassword.getText();

        // ✅ Validation des champs obligatoires
        if (email.isEmpty()) {
            showError("L'email est obligatoire");
            txtEmail.setStyle(STYLE_ERROR);
            return;
        }
        
        if (password.isEmpty()) {
            showError("Le mot de passe est obligatoire");
            txtPassword.setStyle(STYLE_ERROR);
            return;
        }
        
        // ✅ Validation format email
        if ("candidat".equals(userType)) {
            if (!EMAIL_IHEC_PATTERN.matcher(email).matches()) {
                showError("Format email IHEC invalide");
                txtEmail.setStyle(STYLE_ERROR);
                return;
            }
        } else {
            if (!EMAIL_ENTREPRISE_PATTERN.matcher(email).matches()) {
                showError("Format email invalide");
                txtEmail.setStyle(STYLE_ERROR);
                return;
            }
        }

        boolean success = false;

        // Tentative de connexion selon le type d'utilisateur
        if ("candidat".equals(userType)) {
            success = authService.loginCandidat(email, password);
        } else if ("entreprise".equals(userType)) {
            success = authService.loginEntreprise(email, password);
        }

        if (success) {
            // Sauvegarde de l'état après connexion
            DataManager.getInstance().sauvegarder();

            // Redirection vers le bon dashboard
            if ("candidat".equals(userType)) {
                navigateToCandidatDashboard();
            } else {
                navigateToEntrepriseDashboard();
            }

        } else {
            showError("Email ou mot de passe incorrect");
            txtEmail.setStyle(STYLE_ERROR);
            txtPassword.setStyle(STYLE_ERROR);
        }
    }

    /**
     * Redirection vers le dashboard candidat
     */
    private void navigateToCandidatDashboard() {
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

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Espace Candidat");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur de navigation: " + e.getMessage());
        }
    }

    /**
     * Redirection vers le dashboard entreprise
     */
    private void navigateToEntrepriseDashboard() {
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

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Espace Entreprise");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur de navigation: " + e.getMessage());
        }
    }

    /**
     * Redirection vers la page d'inscription
     */
    @FXML
    private void handleGoToRegister(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RegisterView.fxml"));
            javafx.scene.layout.VBox root = loader.load();

            RegisterController controller = loader.getController();
            controller.setUserType(userType);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) lblRegisterLink.getScene().getWindow();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Inscription");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retour à la page de sélection du type de profil
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/TypeSelectionView.fxml")
            );
            javafx.scene.layout.VBox root = loader.load();

            TypeSelectionController controller = loader.getController();
            controller.setMode("login");

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Sélection du profil");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche un message d'erreur dans l'interface
     */
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}