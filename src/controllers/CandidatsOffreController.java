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
 * Contrôleur responsable de l'affichage et de la gestion
 * des candidats ayant postulé à une offre donnée.
 */
public class CandidatsOffreController {

    /* ===================== COMPOSANTS FXML ===================== */

    // Titre affichant le nom de l'offre
    @FXML private Label lblTitreOffre;

    // Label affichant le nombre total de candidats
    @FXML private Label lblNbCandidats;

    // Tableau contenant la liste des candidats
    @FXML private TableView<CandidatDisplay> tableCandidats;

    // Colonnes du tableau
    @FXML private TableColumn<CandidatDisplay, String> colCIN, colNom, colPrenom,
                                                       colEmail, colTelephone, colType;

    // Boutons d'actions
    @FXML private Button btnWishlist, btnSupprimer;

    /* ===================== ATTRIBUTS METIER ===================== */

    // Identifiant de l'offre sélectionnée
    private String offreId;

    // Offre courante
    private Offre offre;

    // Service gérant les candidatures
    private CandidatureService candidatureService;

    // Entreprise actuellement connectée
    private Entreprise entreprise;

    /**
     * Méthode appelée depuis l'écran précédent
     * pour transmettre l'id de l'offre sélectionnée
     */
    public void setOffreId(String id) {
        this.offreId = id;
        loadData(); // Charger les candidats liés à l'offre
    }

    /**
     * Méthode appelée automatiquement par JavaFX
     * lors du chargement du fichier FXML
     */
    @FXML
    public void initialize() {

        // Récupération des services et de l'entreprise connectée
        candidatureService = DataManager.getInstance().getCandidatureService();
        entreprise = DataManager.getInstance().getAuthService().getEntrepriseConnectee();

        // Liaison entre les colonnes du tableau et les attributs de CandidatDisplay
        colCIN.setCellValueFactory(new PropertyValueFactory<>("cin"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Activer/Désactiver les boutons selon la sélection
        tableCandidats.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, newVal) -> {
                boolean selected = newVal != null;
                btnWishlist.setDisable(!selected);
                btnSupprimer.setDisable(!selected);
            }
        );
    }

    /**
     * Charger les données de l'offre
     * et afficher les candidats dans le tableau
     */
    private void loadData() {

        // Recherche de l'offre par son id
        for (Offre o : DataManager.getInstance().getOffreService().getAllOffres()) {
            if (o.getId().toString().equals(offreId)) {
                offre = o;
                break;
            }
        }

        // Si l'offre existe
        if (offre != null) {

            // Mise à jour des labels
            lblTitreOffre.setText("Candidats - " + offre.getTitre());
            lblNbCandidats.setText(offre.getCandidatures().size() + " candidat(s)");

            // Liste observable pour le TableView
            ObservableList<CandidatDisplay> data = FXCollections.observableArrayList();

            // Transformation des candidats métiers en objets d'affichage
            for (Candidat c : offre.getCandidatures()) {
                String type = c instanceof Etudiant ? "Étudiant" : "Alumni";

                data.add(new CandidatDisplay(
                        String.valueOf(c.getId()),
                        c.getNom(),
                        c.getPrenom(),
                        c.getEmail(),
                        c.getTelephone(),
                        type
                ));
            }

            // Injection des données dans le tableau
            tableCandidats.setItems(data);
        }
    }

    /**
     * Ajouter le candidat sélectionné à la wishlist
     */
    @FXML
    private void handleWishlist() {

        CandidatDisplay selected = tableCandidats.getSelectionModel().getSelectedItem();

        if (selected != null) {
            boolean success = candidatureService.ajouterWishlist(
                    entreprise,
                    selected.getCin()
            );

            if (success) {
                DataManager.getInstance().sauvegarder();
                showSuccess("Candidat ajouté à la wishlist !");
            } else {
                showError("Candidat déjà dans la wishlist");
            }
        }
    }

    /**
     * Supprimer la candidature du candidat pour cette offre
     */
    @FXML
    private void handleSupprimer() {

        CandidatDisplay selected = tableCandidats.getSelectionModel().getSelectedItem();

        if (selected != null && showConfirm("Retirer cette candidature ?")) {

            boolean success = candidatureService.supprimerCandidatureOffre(
                    offreId,
                    selected.getCin()
            );

            if (success) {
                DataManager.getInstance().sauvegarder();
                showSuccess("Candidature retirée !");
                loadData(); // Rafraîchir la table
            }
        }
    }

    /**
     * Retour à la vue "Mes Offres"
     */
    @FXML
    private void handleRetour() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/MesOffresView.fxml")
            );

            BorderPane root = loader.load();
            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) tableCandidats.getScene().getWindow();
            
            // Forcer la taille et position de la fenêtre
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

    /* ===================== BOITES DE DIALOGUE ===================== */

    private boolean showConfirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText(msg);
        return a.showAndWait().get() == ButtonType.OK;
    }

    private void showSuccess(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.showAndWait();
    }

    /* ===================== CLASSE INTERNE ===================== */

    /**
     * Classe utilisée uniquement pour l'affichage
     * dans le TableView (pattern DTO / ViewModel)
     */
    public static class CandidatDisplay {

        private final String cin, nom, prenom, email, telephone, type;

        public CandidatDisplay(String cin, String nom, String prenom,
                               String email, String telephone, String type) {
            this.cin = cin;
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.telephone = telephone;
            this.type = type;
        }

        public String getCin() { return cin; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getEmail() { return email; }
        public String getTelephone() { return telephone; }
        public String getType() { return type; }
    }
}