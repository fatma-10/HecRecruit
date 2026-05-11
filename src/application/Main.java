package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import utils.DataManager;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialiser les données au démarrage
            DataManager.getInstance().initialiser();
            
            // Charger la vue d'accueil (WelcomeView)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/WelcomeView.fxml"));
            VBox root = loader.load();
            
            // ✅ NOUVEAU : Obtenir la taille de l'écran
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // ✅ NOUVEAU : Créer la scène avec la taille de l'écran
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            
            primaryStage.setTitle("HecRecruit - Plateforme de Recrutement");
            
            // ✅ NOUVEAU : Forcer la position et taille AVANT show()
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());
            
            primaryStage.setScene(scene);
            
            // ✅ NOUVEAU : Maximiser AVANT show()
            primaryStage.setMaximized(true);
            
            // ✅ MODIFIÉ : show() en dernier
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Erreur au démarrage: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        // Sauvegarder les données avant de quitter
        DataManager.getInstance().sauvegarder();
        System.out.println("✅ Application fermée et données sauvegardées !");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}