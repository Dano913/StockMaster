package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.example.paneljavafx.model.User;
import org.example.paneljavafx.service.UserService;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Label logoLabel;
    private User currentUser;

    private final UserService userService = UserService.getInstance();

    public void init(User user, String firstViewPath) {

        this.currentUser = user;
        navigateTo(firstViewPath);

        var resource = getClass().getResource("/images/logo-stockmaster.png");

        if (resource == null) {
            System.err.println("❌ Imagen no encontrada");
            return;
        }

        Image image = new Image(resource.toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);

        logoLabel.setGraphic(imageView);
    }

    public void navigateTo(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);

            if (resource == null) {
                System.err.println("❌ FXML no encontrado: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAware aware) {
                aware.setUser(currentUser);
                aware.setMainController(this);
            }

            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            System.err.println("❌ Error cargando: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void navigateWithTarget(String fxmlPath, User target) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) return;

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAware aware) {
                aware.setUser(currentUser);
                aware.setMainController(this);
                aware.setSelectedTarget(target);
            }

            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePerfil() {
        navigateTo("/org/example/paneljavafx/perfil.fxml");
    }

    @FXML
    private void handleLogout() {
        userService.logout();
        try {
            URL resource = getClass().getResource(
                    "/org/example/paneljavafx/login-view.fxml");
            Parent login = new FXMLLoader(resource).load();
            contentArea.getScene().setRoot(login);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}