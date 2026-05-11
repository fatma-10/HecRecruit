package controllers;

// Imports JavaFX pour les collections observables (utilisées par TableView)
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Imports JavaFX FXML et interface graphique
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

// Imports des modèles et services de l'application
import models.*;
import service.*;
import utils.DataManager;

/**
 * Controller de la vue "Mes Candidatures"
 * Permet au candidat de :
 *  - voir ses candidatures
 *  - consulter les détails
 *  - retirer une candidature
 */
public class MesCandidaturesController {

    // Labels d'information
    @FXML private Label lblNbCandidatures; // Nombre de candidatures actives
    @FXML private Label lblInfo;           // Message informatif

    // TableView affichant les candidatures
    @FXML private TableView<CandidatureDisplay> tableCandidatures;

    // Colonnes de la table
    @FXML private TableColumn<CandidatureDisplay, String> colTitre;
    @FXML private TableColumn<CandidatureDisplay, String> colType;
    @FXML private TableColumn<CandidatureDisplay, String> colEntreprise;
    @FXML private TableColumn<CandidatureDisplay, String> colDate;
    @FXML private TableColumn<CandidatureDisplay, String> colStatut;

    // Boutons d'action
    @FXML private Button btnVoirDetails;
    @FXML private Button btnRetirer;

    // Services
    private AuthService authService;
    private CandidatureService candidatureService;

    // Candidat actuellement connecté
    private Candidat candidatConnecte;

    /**
     * Méthode appelée automatiquement après le chargement du FXML
     */
    @FXML
    public void initialize() {
        // Récupération des services via le DataManager (Singleton)
        authService = DataManager.getInstance().getAuthService();
        candidatureService = DataManager.getInstance().getCandidatureService();

        // Récupération du candidat connecté
        candidatConnecte = authService.getCandidatConnecte();

        // Configuration des colonnes de la table
        setupTable();

        // Chargement des candidatures du candidat
        loadCandidatures();

        // Activation/désactivation des boutons selon la sélection
        tableCandidatures.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean selected = newSelection != null;
                btnVoirDetails.setDisable(!selected);
                btnRetirer.setDisable(!selected);
            }
        );
    }

    /**
     * Associe chaque colonne à une propriété de CandidatureDisplay
     */
    private void setupTable() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colEntreprise.setCellValueFactory(new PropertyValueFactory<>("entreprise"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("datePublication"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    /**
     * Charge les candidatures du candidat connecté dans la TableView
     */
    private void loadCandidatures() {
        ObservableList<CandidatureDisplay> candidaturesData = FXCollections.observableArrayList();

        // Parcours des offres auxquelles le candidat a postulé
        for (Offre offre : candidatConnecte.getCandidaturesEnCours()) {

            // Détermination du statut de l'offre
            String statut = offre.estExpiree() ? "Expirée" : "Active";

            // Création d'un objet d'affichage (adapté à la TableView)
            candidaturesData.add(new CandidatureDisplay(
                offre.getId().toString(),
                offre.getTitre(),
                offre.getTypeOffre(),
                offre.getEntreprise().getNom(),
                offre.getDatePublication().toString(),
                statut
            ));
        }

        // Ajout des données à la table
        tableCandidatures.setItems(candidaturesData);

        // Mise à jour du nombre de candidatures actives
        int nbActives = candidatureService.getNombreCandidaturesActives(candidatConnecte);
        lblNbCandidatures.setText(String.valueOf(nbActives));

        // Message informatif
        if (candidaturesData.isEmpty()) {
            lblInfo.setText("Vous n'avez aucune candidature en cours");
        } else {
            lblInfo.setText(candidaturesData.size() + " candidature(s) au total");
        }
    }

    /**
     * Affiche les détails de l'offre sélectionnée
     */
    @FXML
    private void handleVoirDetails() {
        CandidatureDisplay selected = tableCandidatures.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Recherche de l'offre complète à partir de son ID
        Offre offre = findOffreById(selected.getIdOffre());
        if (offre == null) return;

        // Création d'une alerte d'information
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'offre");
        alert.setHeaderText(offre.getTitre());

        StringBuilder content = new StringBuilder();
        content.append("Type: ").append(offre.getTypeOffre()).append("\n\n");
        content.append("Entreprise: ").append(offre.getEntreprise().getNom()).append("\n");
        content.append("Secteur: ").append(offre.getEntreprise().getSecteur()).append("\n\n");
        content.append("Description:\n").append(offre.getDescription()).append("\n\n");

        // Détails spécifiques selon le type d'offre
        if (offre instanceof models.Stage) {
            models.Stage stage = (models.Stage) offre;
            content.append("Domaine: ").append(stage.getDomaine()).append("\n");
            content.append("Durée: ").append(stage.getDureeEnMois()).append(" mois\n");

        } else if (offre instanceof Alternance) {
            Alternance alt = (Alternance) offre;
            content.append("Rythme: ").append(alt.getRythme()).append("\n");
            content.append("Durée: ").append(alt.getDureeEnMois()).append(" mois\n");

        } else if (offre instanceof ProjetFinEtudes) {
            ProjetFinEtudes pfe = (ProjetFinEtudes) offre;
            content.append("Sujet: ").append(pfe.getSujet()).append("\n");
            content.append("Technologies: ").append(pfe.getTechnologies()).append("\n");
        }

        content.append("\nDate de publication: ").append(offre.getDatePublication());
        if (offre.getDateExpiration() != null) {
            content.append("\nDate d'expiration: ").append(offre.getDateExpiration());
        }

        content.append("\nNombre total de candidatures: ")
               .append(offre.getCandidatures().size());

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    /**
     * Retire une candidature après confirmation
     */
    @FXML
    private void handleRetirer() {
        CandidatureDisplay selected = tableCandidatures.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Boîte de confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer le retrait");
        confirm.setHeaderText("Retirer cette candidature ?");
        confirm.setContentText("Êtes-vous sûr de vouloir retirer votre candidature pour:\n"
                               + selected.getTitre());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean success = candidatureService.retirerCandidature(
                candidatConnecte,
                selected.getIdOffre()
            );

            if (success) {
                DataManager.getInstance().sauvegarder();
                showSuccess("Candidature retirée avec succès !");
                loadCandidatures(); // Rafraîchir la table
            } else {
                showError("Erreur lors du retrait de la candidature");
            }
        }
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

            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                getClass().getResource("/application/application.css").toExternalForm()
            );

            Stage stage = (Stage) tableCandidatures.getScene().getWindow();
            
            // Forcer la taille et position de la fenêtre
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

    /**
     * Recherche une offre par son ID
     */
    private Offre findOffreById(String id) {
        for (Offre offre : candidatConnecte.getCandidaturesEnCours()) {
            if (offre.getId().toString().equals(id)) {
                return offre;
            }
        }
        return null;
    }

    // Alertes utilitaires
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Classe interne utilisée uniquement pour l'affichage
     * (pattern DTO / ViewModel)
     */
    public static class CandidatureDisplay {

        private final String idOffre;
        private final String titre;
        private final String type;
        private final String entreprise;
        private final String datePublication;
        private final String statut;

        public CandidatureDisplay(String idOffre, String titre, String type,
                                  String entreprise, String datePublication,
                                  String statut) {
            this.idOffre = idOffre;
            this.titre = titre;
            this.type = type;
            this.entreprise = entreprise;
            this.datePublication = datePublication;
            this.statut = statut;
        }

        // Getters nécessaires pour TableView
        public String getIdOffre() { return idOffre; }
        public String getTitre() { return titre; }
        public String getType() { return type; }
        public String getEntreprise() { return entreprise; }
        public String getDatePublication() { return datePublication; }
        public String getStatut() { return statut; }
    }
}