package org.example.demo3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

// HelloApplication builds and launches the app.
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml")); // Add hello-view
        Scene scene = new Scene(fxmlLoader.load(), 800, 600); // Build scene
        scene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm()); // Add CSS file
        stage.setTitle("MarkBook+"); // Add app title to window frame
        stage.setScene(scene);
        stage.setMaximized(true); // Maximize window
        stage.show();
    }

    // Launch app
    public static void main(String[] args) {
        launch();
    }

}