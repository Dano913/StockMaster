package org.example.paneljavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.IOException;
import java.util.List;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("hello-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1300, 850);

        stage.setTitle("Hello!");
        stage.setScene(scene);

        // 🖥️ Obtener pantallas
        List<Screen> screens = Screen.getScreens();

        if (screens.size() > 1) {
            // 👉 Segunda pantalla
            Screen secondScreen = screens.get(1);
            Rectangle2D bounds = secondScreen.getVisualBounds();

            // 🎯 Centrar en segunda pantalla
            stage.setX(bounds.getMinX() + (bounds.getWidth() - 1200) / 2);
            stage.setY(bounds.getMinY() + (bounds.getHeight() - 890) / 2);
        } else {
            // 👉 Solo una pantalla → centrar normal
            stage.centerOnScreen();
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}