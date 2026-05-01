package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
            System.err.println("Imagen no encontrada");
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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );

            Parent view = loader.load();

            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfile() {
        openProfileModal();
    }

    public void openProfileModal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/profile-view.fxml")
            );

            Parent view = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(contentArea.getScene().getWindow());
            dialog.setTitle("Perfil");

            Scene scene = new Scene(view);

            dialog.setScene(scene);
            dialog.centerOnScreen();
            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
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