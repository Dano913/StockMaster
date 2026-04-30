package org.example.paneljavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import org.example.paneljavafx.service.GestorService;

import java.io.IOException;
import java.util.List;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/paneljavafx/admin-view.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root, 1900, 900);

        scene.getStylesheets().add(
                getClass().getResource("/org/example/paneljavafx/trading-theme.css").toExternalForm()
        );

        stage.setTitle("StockMaster");
        stage.setScene(scene);

        stage.centerOnScreen();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}