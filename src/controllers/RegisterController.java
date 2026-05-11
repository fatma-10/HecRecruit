package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import service.AuthService;
import utils.DataManager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Controller responsable de l'inscription
 * des candidats (étudiant / alumni) et des entreprises.
 * VERSION AVEC VALIDATION EN TEMPS RÉEL
 */
public class RegisterController {

    /* ===================== CONSTANTES DE VALIDATION ===================== */
    
    // Regex pour email IHEC : prenom.nom.annee@ihec.ucar.tn
    private static final Pattern EMAIL_IHEC_PATTERN = Pattern.compile(
        "^[a-z]+\\.[a-z]+\\.\\d{4}@ihec\\.ucar\\.tn$"
    );
    
    // Regex pour email entreprise : format standard
    private static final Pattern EMAIL_ENTREPRISE_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Regex pour téléphone : exactement 8 chiffres
    private static final Pattern TELEPHONE_PATTERN = Pattern.compile("^\\d{8}$");
    
    // Longueur minimale du mot de passe
    private static final int MIN_PASSWORD_LENGTH = 6;
    
    // Styles CSS pour feedback visuel
    private static final String STYLE_ERROR = "-fx-border-color: #E74C3C; -fx-border-width: 2px;";
    private static final String STYLE_SUCCESS = "-fx-border-color: #2ECC71; -fx-border-width: 2px;";
    private static final String STYLE_NORMAL = "";

    /* ===================== Composants FXML ===================== */

    @FXML private Text iconText;
    @FXML private Label lblTitle;
    @FXML private VBox candidatTypeContainer;
    @FXML private ToggleButton btnEtudiant;
    @FXML private ToggleButton btnAlumni;

    /* ===================== Champs communs ===================== */

    @FXML private Label lblCIN;
    @FXML private TextField txtCIN;
    @FXML private TextField txtNom;
    @FXML private Label lblField2;
    @FXML private TextField txtField2;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private PasswordField txtPassword;

    /* ===================== Champs spécifiques ===================== */

    @FXML private Label lblSpecific1;
    @FXML private TextField txtSpecific1;
    @FXML private Label lblSpecific2;
    @FXML private TextField txtSpecific2;
    @FXML private Label lblSpecific3;
    @FXML private TextField txtSpecific3;

    /* ===================== Autres composants ===================== */

    @FXML private Label lblError;
    @FXML private Label lblLoginLink;
    @FXML private Button btnRegister;
    @FXML private Button btnBack;

    /* ===================== Données métier ===================== */

    private String userType;
    private String candidatType = "etudiant";
    private AuthService authService;

    /**
     * Méthode appelée automatiquement au chargement du FXML.
     */
    @FXML
    public void initialize() {
        authService = DataManager.getInstance().getAuthService();
        lblError.setVisible(false);

        ToggleGroup group = new ToggleGroup();
        btnEtudiant.setToggleGroup(group);
        btnAlumni.setToggleGroup(group);
        btnEtudiant.setSelected(true);
        
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
                if ("candidat".equals(userType)) {
                    validateEmailIHECField(txtEmail);
                } else {
                    validateEmailEntrepriseField(txtEmail);
                }
            } else {
                txtEmail.setStyle(STYLE_NORMAL);
            }
        });
        
        // Validation téléphone en temps réel
        txtTelephone.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                validateTelephoneField(txtTelephone);
            } else {
                txtTelephone.setStyle(STYLE_NORMAL);
            }
        });
        
        // Validation mot de passe en temps réel
        txtPassword.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.isEmpty()) {
                validatePasswordField(txtPassword);
            } else {
                txtPassword.setStyle(STYLE_NORMAL);
            }
        });
        
        // Validation CIN en temps réel (si candidat)
        txtCIN.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty() && "candidat".equals(userType)) {
                validateCINField(txtCIN);
            } else {
                txtCIN.setStyle(STYLE_NORMAL);
            }
        });
    }

    /* ===================== MÉTHODES DE VALIDATION ===================== */
    
    /**
     * ✅ Valide un email IHEC avec feedback visuel
     */
    private boolean validateEmailIHECField(TextField field) {
        String email = field.getText().trim().toLowerCase();
        
        if (EMAIL_IHEC_PATTERN.matcher(email).matches()) {
            field.setStyle(STYLE_SUCCESS);
            return true;
        } else {
            field.setStyle(STYLE_ERROR);
            return false;
        }
    }
    
    /**
     * ✅ Valide un email entreprise avec feedback visuel
     */
    private boolean validateEmailEntrepriseField(TextField field) {
        String email = field.getText().trim();
        
        if (EMAIL_ENTREPRISE_PATTERN.matcher(email).matches()) {
            field.setStyle(STYLE_SUCCESS);
            return true;
        } else {
            field.setStyle(STYLE_ERROR);
            return false;
        }
    }
    
    /**
     * ✅ Valide un téléphone avec feedback visuel
     */
    private boolean validateTelephoneField(TextField field) {
        String tel = field.getText().trim();
        
        if (TELEPHONE_PATTERN.matcher(tel).matches()) {
            field.setStyle(STYLE_SUCCESS);
            return true;
        } else {
            field.setStyle(STYLE_ERROR);
            return false;
        }
    }
    
    /**
     * ✅ Valide un mot de passe avec feedback visuel
     */
    private boolean validatePasswordField(PasswordField field) {
        String password = field.getText();
        
        if (password.length() >= MIN_PASSWORD_LENGTH) {
            field.setStyle(STYLE_SUCCESS);
            return true;
        } else {
            field.setStyle(STYLE_ERROR);
            return false;
        }
    }
    
    /**
     * ✅ Valide un CIN avec feedback visuel
     */
    private boolean validateCINField(TextField field) {
        String cin = field.getText().trim();
        
        try {
            int cinNum = Integer.parseInt(cin);
            if (cin.length() == 8 && cinNum >= 10000000 && cinNum <= 99999999) {
                field.setStyle(STYLE_SUCCESS);
                return true;
            }
        } catch (NumberFormatException e) {
            // Continue to error state
        }
        
        field.setStyle(STYLE_ERROR);
        return false;
    }
    
    /**
     * ✅ Valide qu'un champ n'est pas vide
     */
    private boolean validateNotEmpty(TextField field) {
        if (!field.getText().trim().isEmpty()) {
            field.setStyle(STYLE_SUCCESS);
            return true;
        } else {
            field.setStyle(STYLE_ERROR);
            return false;
        }
    }

    /**
     * Définit le type d'utilisateur choisi depuis l'écran précédent.
     */
    public void setUserType(String userType) {
        this.userType = userType;
        updateUI();
    }

    /**
     * Gère le changement entre étudiant et alumni.
     */
    @FXML
    private void handleTypeChange() {
        if (btnEtudiant.isSelected()) {
            candidatType = "etudiant";
        } else if (btnAlumni.isSelected()) {
            candidatType = "alumni";
        }
        updateSpecificFields();
    }

    /**
     * Met à jour l'interface selon le type d'utilisateur.
     */
    private void updateUI() {

        if ("candidat".equals(userType)) {
            iconText.setText("👤");
            lblTitle.setText("Inscription Candidat");
            candidatTypeContainer.setVisible(true);
            candidatTypeContainer.setManaged(true);
            lblCIN.setVisible(true);
            txtCIN.setVisible(true);
            lblCIN.setManaged(true);
            txtCIN.setManaged(true);
            lblField2.setText("Prénom *");
            txtField2.setPromptText("Prénom");
            updateSpecificFields();

        } else if ("entreprise".equals(userType)) {
            iconText.setText("🏢");
            lblTitle.setText("Inscription Entreprise");
            candidatTypeContainer.setVisible(false);
            candidatTypeContainer.setManaged(false);
            lblCIN.setVisible(false);
            txtCIN.setVisible(false);
            lblCIN.setManaged(false);
            txtCIN.setManaged(false);
            lblField2.setText("Secteur *");
            txtField2.setPromptText("Ex: Informatique, Finance...");
            lblSpecific1.setText("Adresse *");
            txtSpecific1.setPromptText("Adresse complète");
            lblSpecific2.setVisible(false);
            txtSpecific2.setVisible(false);
            lblSpecific2.setManaged(false);
            txtSpecific2.setManaged(false);
            lblSpecific3.setVisible(false);
            txtSpecific3.setVisible(false);
            lblSpecific3.setManaged(false);
            txtSpecific3.setManaged(false);
        }
    }

    /**
     * Met à jour les champs spécifiques selon étudiant ou alumni.
     */
    private void updateSpecificFields() {

        if ("etudiant".equals(candidatType)) {
            lblSpecific1.setText("Niveau *");
            txtSpecific1.setPromptText("Ex: Licence 3");
            lblSpecific2.setText("Filière *");
            txtSpecific2.setPromptText("Ex: Informatique");
            lblSpecific3.setText("Établissement *");
            txtSpecific3.setPromptText("Ex: HEC Carthage");
            lblSpecific2.setVisible(true);
            txtSpecific2.setVisible(true);
            lblSpecific3.setVisible(true);
            txtSpecific3.setVisible(true);
            lblSpecific2.setManaged(true);
            txtSpecific2.setManaged(true);
            lblSpecific3.setManaged(true);
            txtSpecific3.setManaged(true);

        } else if ("alumni".equals(candidatType)) {
            lblSpecific1.setText("Année diplôme *");
            txtSpecific1.setPromptText("Ex: 2020");
            lblSpecific2.setText("Poste actuel");
            txtSpecific2.setPromptText("Ex: Développeur");
            lblSpecific3.setText("Entreprise actuelle");
            txtSpecific3.setPromptText("Ex: Google");
            lblSpecific2.setVisible(true);
            txtSpecific2.setVisible(true);
            lblSpecific3.setVisible(true);
            txtSpecific3.setVisible(true);
            lblSpecific2.setManaged(true);
            txtSpecific2.setManaged(true);
            lblSpecific3.setManaged(true);
            txtSpecific3.setManaged(true);
        }
    }

    /**
     * ✅ MODIFIÉ : Gère l'action d'inscription avec validation complète
     */
    @FXML
    private void handleRegister() {
        
        // Récupération et trim des valeurs
        String nom = txtNom.getText().trim();
        String field2 = txtField2.getText().trim();
        String email = txtEmail.getText().trim().toLowerCase();
        String telephone = txtTelephone.getText().trim();
        String password = txtPassword.getText();

        // ✅ Validation des champs communs
        boolean isValid = true;
        
        // Nom
        if (nom.isEmpty()) {
            showError("Le nom est obligatoire");
            txtNom.setStyle(STYLE_ERROR);
            return;
        }
        validateNotEmpty(txtNom);
        
        // Prénom ou Secteur
        if (field2.isEmpty()) {
            showError("candidat".equals(userType) ? "Le prénom est obligatoire" : "Le secteur est obligatoire");
            txtField2.setStyle(STYLE_ERROR);
            return;
        }
        validateNotEmpty(txtField2);
        
        // Email
        if (email.isEmpty()) {
            showError("L'email est obligatoire");
            txtEmail.setStyle(STYLE_ERROR);
            return;
        }
        
        if ("candidat".equals(userType)) {
            if (!EMAIL_IHEC_PATTERN.matcher(email).matches()) {
                showError("Format email IHEC invalide (prenom.nom.annee@ihec.ucar.tn)");
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
        
        // Téléphone
        if (!TELEPHONE_PATTERN.matcher(telephone).matches()) {
            showError("Le téléphone doit contenir exactement 8 chiffres");
            txtTelephone.setStyle(STYLE_ERROR);
            return;
        }
        
        // Mot de passe
        if (password.length() < MIN_PASSWORD_LENGTH) {
            showError("Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères");
            txtPassword.setStyle(STYLE_ERROR);
            return;
        }

        // Inscription selon le type
        boolean success;
        if ("candidat".equals(userType)) {
            success = registerCandidat(nom, field2, email, telephone, password);
        } else {
            success = registerEntreprise(nom, field2, email, telephone, password);
        }

        if (success) {
            DataManager.getInstance().sauvegarder();

            if ("candidat".equals(userType)) {
                navigateToCandidatDashboard();
            } else {
                navigateToEntrepriseDashboard();
            }
        }
    }

    /**
     * ✅ MODIFIÉ : Inscription d'un candidat avec validation
     */
    private boolean registerCandidat(String nom, String prenom, String email,
                                     String telephone, String password) {

        // Validation CIN
        String cinStr = txtCIN.getText().trim();
        
        if (cinStr.isEmpty()) {
            showError("Le CIN est obligatoire");
            txtCIN.setStyle(STYLE_ERROR);
            return false;
        }

        int cin;
        try {
            cin = Integer.parseInt(cinStr);
            if (cin < 10000000 || cin > 99999999) {
                showError("Le CIN doit être un nombre de 8 chiffres");
                txtCIN.setStyle(STYLE_ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Le CIN doit être un nombre valide");
            txtCIN.setStyle(STYLE_ERROR);
            return false;
        }

        // Validation des champs spécifiques
        String specific1 = txtSpecific1.getText().trim();
        String specific2 = txtSpecific2.getText().trim();
        String specific3 = txtSpecific3.getText().trim();
        
        if (specific1.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires");
            txtSpecific1.setStyle(STYLE_ERROR);
            return false;
        }

        Map<String, String> infosSuppl = new HashMap<>();
        infosSuppl.put("id", String.valueOf(cin));

        if ("etudiant".equals(candidatType)) {

            if (specific2.isEmpty() || specific3.isEmpty()) {
                showError("Veuillez remplir tous les champs obligatoires");
                if (specific2.isEmpty()) txtSpecific2.setStyle(STYLE_ERROR);
                if (specific3.isEmpty()) txtSpecific3.setStyle(STYLE_ERROR);
                return false;
            }

            infosSuppl.put("niveau", specific1);
            infosSuppl.put("filiere", specific2);
            infosSuppl.put("etablissement", specific3);

        } else {

            try {
                int annee = Integer.parseInt(specific1);
                if (annee < 1900 || annee > 2025) {
                    showError("Année de diplôme invalide");
                    txtSpecific1.setStyle(STYLE_ERROR);
                    return false;
                }
                infosSuppl.put("anneeDiplome", specific1);
            } catch (NumberFormatException e) {
                showError("L'année de diplôme doit être un nombre valide");
                txtSpecific1.setStyle(STYLE_ERROR);
                return false;
            }

            infosSuppl.put("posteActuel", specific2);
            infosSuppl.put("entrepriseActuelle", specific3);
        }

        boolean success = authService.registerCandidat(
                nom, prenom, email, telephone, password, candidatType, infosSuppl);

        if (!success) {
            showError("Erreur: Cet email ou ce CIN est déjà utilisé");
        }

        return success;
    }

    /**
     * ✅ MODIFIÉ : Inscription d'une entreprise avec validation
     */
    private boolean registerEntreprise(String nom, String secteur, String email,
                                       String telephone, String password) {

        String adresse = txtSpecific1.getText().trim();

        if (adresse.isEmpty()) {
            showError("L'adresse est obligatoire");
            txtSpecific1.setStyle(STYLE_ERROR);
            return false;
        }

        boolean success = authService.registerEntreprise(
                nom, secteur, adresse, email, telephone, password);

        if (!success) {
            showError("Erreur: Cet email est déjà utilisé");
        }

        return success;
    }

    /* ===================== Navigation ===================== */

    private void navigateToCandidatDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/CandidatDashboardView.fxml"));
            BorderPane root = loader.load();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm());

            Stage stage = (Stage) btnRegister.getScene().getWindow();
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

    private void navigateToEntrepriseDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/EntrepriseDashboardView.fxml"));
            BorderPane root = loader.load();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm());

            Stage stage = (Stage) btnRegister.getScene().getWindow();
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

    @FXML
    private void handleGoToLogin(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/LoginView.fxml"));
            VBox root = loader.load();

            LoginController controller = loader.getController();
            controller.setUserType(userType);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm());

            Stage stage = (Stage) lblLoginLink.getScene().getWindow();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Connexion");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/TypeSelectionView.fxml"));
            VBox root = loader.load();

            TypeSelectionController controller = loader.getController();
            controller.setMode("register");

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm());

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
     * Affiche un message d'erreur à l'écran.
     */
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}