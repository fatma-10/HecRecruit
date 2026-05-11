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

/**
 * Contrôleur du tableau de bord de l'entreprise.
 * Il affiche les informations générales de l'entreprise,
 * des statistiques (offres, candidatures, wishlist)
 * et gère la navigation entre les différentes vues.
 */
public class EntrepriseDashboardController {

    /* ===================== COMPOSANTS FXML ===================== */

    // Labels d'informations générales
    @FXML private Label lblNomEntreprise;
    @FXML private Label lblSecteur;

    // Labels de statistiques
    @FXML private Label lblNbOffres;
    @FXML private Label lblNbCandidatures;
    @FXML private Label lblNbWishlist;

    // Bouton de déconnexion
    @FXML private Button btnDeconnexion;

    /* ===================== SERVICES ET DONNÉES ===================== */

    private AuthService authService;
    private OffreService offreService;
    private Entreprise entrepriseConnectee;

    /**
     * Méthode appelée automatiquement lors du chargement du FXML.
     * Elle initialise les services, récupère l'entreprise connectée
     * et met à jour l'interface et les statistiques.
     */
    @FXML
    public void initialize() {

        // Récupération des services depuis le DataManager (singleton)
        authService = DataManager.getInstance().getAuthService();
        offreService = DataManager.getInstance().getOffreService();

        // Récupération de l'entreprise connectée
        entrepriseConnectee = authService.getEntrepriseConnectee();

        // Si une entreprise est bien connectée
        if (entrepriseConnectee != null) {
            updateUI();
            loadStatistiques();
        }
    }

    /**
     * Met à jour les informations générales affichées
     * (nom et secteur de l'entreprise).
     */
    private void updateUI() {
        lblNomEntreprise.setText("Bienvenue, " + entrepriseConnectee.getNom());
        lblSecteur.setText(entrepriseConnectee.getSecteur());
    }

    /**
     * Calcule et affiche les statistiques de l'entreprise :
     * - nombre d'offres publiées
     * - nombre total de candidatures
     * - nombre de candidats dans la wishlist
     */
    private void loadStatistiques() {

        // Nombre d'offres publiées par l'entreprise
        int nbOffres = entrepriseConnectee.getOffresPubliees().size();
        lblNbOffres.setText(String.valueOf(nbOffres));

        // Calcul du nombre total de candidatures
        int nbCandidatures = 0;
        for (Offre offre : entrepriseConnectee.getOffresPubliees()) {
            nbCandidatures += offre.getCandidatures().size();
        }
        lblNbCandidatures.setText(String.valueOf(nbCandidatures));

        // Nombre de candidats dans la wishlist
        int nbWishlist = entrepriseConnectee.getWishlist().size();
        lblNbWishlist.setText(String.valueOf(nbWishlist));
    }

    /* ===================== ACTIONS DU MENU ===================== */

    /**
     * Action Dashboard.
     * L'utilisateur est déjà sur cette page,
     * donc aucune action n'est nécessaire.
     */
    @FXML
    private void handleDashboard() {
        // Déjà sur le dashboard
    }

    /**
     * Navigation vers la page "Mes Offres"
     */
    @FXML
    private void handleOffres() {
        navigateTo("/views/MesOffresView.fxml", "Mes offres");
    }

    /**
     * Navigation vers la page de création d'offre
     */
    @FXML
    private void handleCreerOffre() {
        navigateTo("/views/CreerOffreView.fxml", "Créer une offre");
    }

    /**
     * Navigation vers la wishlist de l'entreprise
     */
    @FXML
    private void handleWishlist() {
        navigateTo("/views/WishlistView.fxml", "Ma wishlist");
    }

    /**
     * Navigation vers le forum
     */
    @FXML
    private void handleForum() {
        navigateTo("/views/ForumView.fxml", "Forum");
    }

    /**
     * Navigation vers le profil de l'entreprise
     */
    @FXML
    private void handleProfil() {
        navigateTo("/views/MonProfilEntrepriseView.fxml", "Mon profil");
    }

    /**
     * Déconnexion de l'entreprise :
     * - suppression de la session
     * - sauvegarde
     * - retour à la page d'accueil
     */
    @FXML
    private void handleDeconnexion() {

        // Déconnexion via AuthService
        authService.logout();

        // Sauvegarde de l'état de l'application
        DataManager.getInstance().sauvegarder();

        try {
            // Chargement de la vue d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/WelcomeView.fxml"));
            VBox root = loader.load();

            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm()
            );

            // Récupération de la fenêtre actuelle
            Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
            
            // Forcer la taille et position de la fenêtre
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Accueil");

            // Configuration de la taille de la fenêtre
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===================== MÉTHODE DE NAVIGATION GÉNÉRIQUE ===================== */

    /**
     * Méthode générique pour naviguer entre les vues
     * du dashboard entreprise.
     */
    private void navigateTo(String fxmlPath, String title) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            BorderPane root = loader.load();

            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
            
            // Forcer la taille et position de la fenêtre
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            
            stage.setScene(scene);
            stage.setTitle("HecRecruit - " + title);
            
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur navigation: " + e.getMessage());
        }
    }
}