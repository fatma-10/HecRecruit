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

import java.util.List;

/**
 * Controller responsable de l'affichage et de la gestion
 * de la liste des offres disponibles pour le candidat.
 */
public class OffresListController {

    /* ===================== Composants FXML ===================== */

    // Champ de recherche
    @FXML private TextField txtRecherche;

    // ComboBox pour choisir le critère de recherche
    @FXML private ComboBox<String> comboCritere;

    // Label pour afficher des messages d'information
    @FXML private Label lblInfo;

    // Label affichant le nombre d'offres
    @FXML private Label lblNbOffres;

    // TableView affichant les offres
    @FXML private TableView<OffreDisplay> tableOffres;

    // Colonnes de la table
    @FXML private TableColumn<OffreDisplay, String> colTitre;
    @FXML private TableColumn<OffreDisplay, String> colType;
    @FXML private TableColumn<OffreDisplay, String> colEntreprise;
    @FXML private TableColumn<OffreDisplay, String> colDate;
    @FXML private TableColumn<OffreDisplay, String> colExpiration;
    @FXML private TableColumn<OffreDisplay, String> colCandidatures;

    // Boutons d'action
    @FXML private Button btnVoirDetails;
    @FXML private Button btnPostuler;

    /* ===================== Services et données ===================== */

    // Services métiers
    private AuthService authService;
    private OffreService offreService;
    private CandidatureService candidatureService;

    // Candidat actuellement connecté
    private Candidat candidatConnecte;

    // Données affichées dans la table
    private ObservableList<OffreDisplay> offresData;

    /**
     * Méthode appelée automatiquement après le chargement du FXML.
     * Elle initialise les services, la table et charge les offres.
     */
    @FXML
    public void initialize() {

        // Récupération des services depuis le DataManager (singleton)
        authService = DataManager.getInstance().getAuthService();
        offreService = DataManager.getInstance().getOffreService();
        candidatureService = DataManager.getInstance().getCandidatureService();

        // Récupération du candidat connecté
        candidatConnecte = authService.getCandidatConnecte();

        // Initialisation des critères de recherche
        comboCritere.setItems(FXCollections.observableArrayList(
                "Toutes",
                "Titre",
                "Type",
                "Entreprise"
        ));
        comboCritere.setValue("Toutes");

        // Configuration des colonnes de la table
        setupTable();

        // Chargement initial de toutes les offres disponibles
        loadAllOffres();

        // Gestion de l'activation/désactivation des boutons selon la sélection
        tableOffres.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean selected = newSelection != null;
                    btnVoirDetails.setDisable(!selected);
                    btnPostuler.setDisable(!selected);
                }
        );
    }

    /**
     * Associe chaque colonne de la table à un attribut de OffreDisplay.
     */
    private void setupTable() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colEntreprise.setCellValueFactory(new PropertyValueFactory<>("entreprise"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("datePublication"));
        colExpiration.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));
        colCandidatures.setCellValueFactory(new PropertyValueFactory<>("nbCandidatures"));
    }

    /**
     * Charge toutes les offres disponibles depuis le service.
     */
    private void loadAllOffres() {
        List<Offre> offres = offreService.getOffresDisponibles();
        displayOffres(offres);
    }

    /**
     * Transforme les objets Offre en objets OffreDisplay
     * pour un affichage simplifié dans la TableView.
     */
    private void displayOffres(List<Offre> offres) {

        offresData = FXCollections.observableArrayList();

        for (Offre offre : offres) {
            offresData.add(new OffreDisplay(
                    offre.getId().toString(),
                    offre.getTitre(),
                    offre.getTypeOffre(),
                    offre.getEntreprise().getNom(),
                    offre.getDatePublication().toString(),
                    offre.getDateExpiration() != null
                            ? offre.getDateExpiration().toString()
                            : "Non définie",
                    String.valueOf(offre.getCandidatures().size())
            ));
        }

        tableOffres.setItems(offresData);
        lblNbOffres.setText(offres.size() + " offre(s) disponible(s)");
    }

    /**
     * Gère la recherche des offres selon un critère choisi.
     */
    @FXML
    private void handleRecherche() {

        String recherche = txtRecherche.getText().trim();
        String critere = comboCritere.getValue();

        // Vérification si le champ est vide
        if (recherche.isEmpty()) {
            showInfo("Veuillez entrer un terme de recherche");
            return;
        }

        // Conversion du critère d'affichage vers le critère métier
        String critereMapped = mapCritere(critere);

        // Recherche via le service
        List<Offre> resultats =
                offreService.rechercherOffres(critereMapped, recherche);

        displayOffres(resultats);
        showInfo(resultats.size() + " résultat(s) trouvé(s)");
    }

    /**
     * Associe le libellé UI au critère métier utilisé dans le service.
     */
    private String mapCritere(String critere) {
        switch (critere) {
            case "Titre": return "titre";
            case "Type": return "type";
            case "Entreprise": return "entreprise";
            default: return "toutes";
        }
    }

    /**
     * Réinitialise la recherche et recharge toutes les offres.
     */
    @FXML
    private void handleReset() {
        txtRecherche.clear();
        comboCritere.setValue("Toutes");
        loadAllOffres();
        hideInfo();
    }

    /**
     * Affiche les détails complets de l'offre sélectionnée.
     */
    @FXML
    private void handleVoirDetails() {

        OffreDisplay selected =
                tableOffres.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Recherche de l'offre réelle à partir de l'ID
        Offre offre = findOffreById(selected.getId());
        if (offre == null) return;

        // Construction de l'alerte de détails
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'offre");
        alert.setHeaderText(offre.getTitre());

        StringBuilder content = new StringBuilder();

        content.append("Type: ").append(offre.getTypeOffre()).append("\n\n");
        content.append("Entreprise: ")
                .append(offre.getEntreprise().getNom()).append("\n");
        content.append("Secteur: ")
                .append(offre.getEntreprise().getSecteur()).append("\n\n");
        content.append("Description:\n")
                .append(offre.getDescription()).append("\n\n");

        // Détails spécifiques selon le type d'offre
        if (offre instanceof models.Stage) {
            models.Stage stage = (models.Stage) offre;
            content.append("Domaine: ").append(stage.getDomaine()).append("\n");
            content.append("Durée: ")
                    .append(stage.getDureeEnMois()).append(" mois\n");
        } else if (offre instanceof Alternance) {
            Alternance alt = (Alternance) offre;
            content.append("Rythme: ").append(alt.getRythme()).append("\n");
            content.append("Durée: ")
                    .append(alt.getDureeEnMois()).append(" mois\n");
        } else if (offre instanceof ProjetFinEtudes) {
            ProjetFinEtudes pfe = (ProjetFinEtudes) offre;
            content.append("Sujet: ").append(pfe.getSujet()).append("\n");
            content.append("Technologies: ")
                    .append(pfe.getTechnologies()).append("\n");
        }

        content.append("\nDate de publication: ")
                .append(offre.getDatePublication());

        if (offre.getDateExpiration() != null) {
            content.append("\nDate d'expiration: ")
                    .append(offre.getDateExpiration());
        }

        content.append("\nNombre de candidatures: ")
                .append(offre.getCandidatures().size());

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    /**
     * Permet au candidat de postuler à une offre sélectionnée.
     */
    @FXML
    private void handlePostuler() {

        OffreDisplay selected =
                tableOffres.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Vérification si le candidat a déjà postulé
        boolean dejaPostule =
                candidatConnecte.getCandidaturesEnCours().stream()
                        .anyMatch(o ->
                                o.getId().toString().equals(selected.getId()));

        if (dejaPostule) {
            showError("Vous avez déjà postulé à cette offre");
            return;
        }

        // Demande de confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la candidature");
        confirm.setHeaderText("Postuler à cette offre ?");
        confirm.setContentText(
                "Êtes-vous sûr de vouloir postuler à l'offre:\n"
                        + selected.getTitre());

        if (confirm.showAndWait().get() == ButtonType.OK) {

            boolean success =
                    candidatureService.postulerOffre(
                            candidatConnecte, selected.getId());

            if (success) {
                DataManager.getInstance().sauvegarder();
                showSuccess("Candidature envoyée avec succès !");
                loadAllOffres();
            } else {
                showError("Erreur lors de l'envoi de la candidature");
            }
        }
    }

    /**
     * Retour au dashboard du candidat.
     */
    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/views/CandidatDashboardView.fxml"));
            BorderPane root = loader.load();

            // Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(
                    getClass().getResource(
                            "/application/application.css")
                            .toExternalForm());

            Stage stage =
                    (Stage) tableOffres.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("HecRecruit - Dashboard Candidat");
            
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recherche une offre complète à partir de son ID.
     */
    private Offre findOffreById(String id) {
        for (Offre offre : offreService.getAllOffres()) {
            if (offre.getId().toString().equals(id)) {
                return offre;
            }
        }
        return null;
    }

    /* ===================== Méthodes utilitaires UI ===================== */

    private void showInfo(String message) {
        lblInfo.setText(message);
        lblInfo.setVisible(true);
        lblInfo.setStyle("-fx-text-fill: #4A90E2;");
    }

    private void hideInfo() {
        lblInfo.setVisible(false);
    }

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

    /* ===================== Classe interne ===================== */

    /**
     * Classe utilisée uniquement pour l'affichage
     * des offres dans la TableView.
     */
    public static class OffreDisplay {

        private final String id;
        private final String titre;
        private final String type;
        private final String entreprise;
        private final String datePublication;
        private final String dateExpiration;
        private final String nbCandidatures;

        public OffreDisplay(String id, String titre, String type,
                             String entreprise, String datePublication,
                             String dateExpiration, String nbCandidatures) {
            this.id = id;
            this.titre = titre;
            this.type = type;
            this.entreprise = entreprise;
            this.datePublication = datePublication;
            this.dateExpiration = dateExpiration;
            this.nbCandidatures = nbCandidatures;
        }

        public String getId() { return id; }
        public String getTitre() { return titre; }
        public String getType() { return type; }
        public String getEntreprise() { return entreprise; }
        public String getDatePublication() { return datePublication; }
        public String getDateExpiration() { return dateExpiration; }
        public String getNbCandidatures() { return nbCandidatures; }
    }
}