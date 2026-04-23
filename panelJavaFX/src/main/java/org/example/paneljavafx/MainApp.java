package org.example.paneljavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;

import java.io.IOException;
import java.util.List;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // 🔥 Cargar FXML de forma segura
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/paneljavafx/admin-view.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root, 1900, 900);

        stage.setTitle("Fund Dashboard");
        stage.setScene(scene);

        // 🖥️ Multi-monitor handling
        List<Screen> screens = Screen.getScreens();

        if (screens.size() > 1) {

            Screen secondScreen = screens.get(1);
            Rectangle2D bounds = secondScreen.getVisualBounds();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());

        } else {
            stage.centerOnScreen();
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}