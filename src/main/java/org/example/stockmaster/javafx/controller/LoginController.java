package org.example.stockmaster.javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import lombok.Setter;
import org.example.stockmaster.javafx.MainApp;
import org.example.stockmaster.javafx.model.User;
import org.example.stockmaster.javafx.service.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    // =========================
    // FXML FIELDS
    // =========================
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @Setter
    private MainController mainController;

    private final UserService userService = UserService.getInstance();

    // =========================
    // INIT
    // =========================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordField.setOnAction(event -> handleLogin(null));
    }

    // =========================
    // LOGIN HANDLER
    // =========================
    @FXML
    private void handleLogin(ActionEvent event) {

        if (mainController == null) {
            System.out.println("⚠️ mainController not set yet");
            return;
        }

        String email = emailField.getText();
        String password = passwordField.getText();

        User user = userService.login(email, password);

        if (user == null) {
            showError("Login failed", "Invalid email or password");
            return;
        }

        String profile = user.getProfile();

        if (profile == null || profile.isBlank()) {
            showError("Error", "User has no profile assigned");
            return;
        }

        String view;

        switch (profile.toLowerCase()) {
            case "admin" -> view = "/org/example/stockmaster/admin-view.fxml";
            case "user", "usuario" -> view = "/org/example/stockmaster/user-view.fxml";
            default -> {
                showError("Error", "Unknown profile: " + profile);
                return;
            }
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource(view)
            );

            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof AdminController adminController) {
                adminController.setUser(user);
                adminController.setMainController(mainController);
            } else if (controller instanceof UserController userController) {
                userController.setUser(user);
                userController.setMainController(mainController);
            }

            mainController.setContent(root);

        } catch (IOException e) {
            System.out.println("❌ Error loading view");
            e.printStackTrace();
        }
    }

    // =========================
    // UTILS
    // =========================
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}