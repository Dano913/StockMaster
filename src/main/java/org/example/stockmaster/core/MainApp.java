package org.example.stockmaster.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/demostockmaster/view.fxml")
        );

        Scene scene = new Scene(loader.load(), 900, 500);

        stage.setTitle("Simulador de Activo - JavaFX");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}