package org.example.paneljavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader loader = getLoader("/org/example/paneljavafx/login-view.fxml");
        Parent root = loader.load();

        Scene scene = new Scene(root, 1900, 900);
        scene.getStylesheets().add(
                getClass().getResource("/org/example/paneljavafx/trading-theme.css")
                        .toExternalForm()
        );

        stage.setTitle("StockMaster");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void setRoot(String fxmlPath) throws IOException {
        primaryStage.getScene().setRoot(loadFXML(fxmlPath));
    }

    public static Parent loadFXML(String fxmlPath) throws IOException {
        return getLoader(fxmlPath).load();
    }

    public static FXMLLoader getLoader(String fxmlPath) {
        return new FXMLLoader(MainApp.class.getResource(fxmlPath));
    }

    public static void main(String[] args) { launch(); }
}