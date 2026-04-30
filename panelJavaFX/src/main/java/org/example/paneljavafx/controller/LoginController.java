package org.example.paneljavafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.paneljavafx.MainApp;
import org.example.paneljavafx.model.User;
import org.example.paneljavafx.service.MainSessionHolder;
import org.example.paneljavafx.service.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         lblError;

    private final UserService userService = UserService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordField.setOnAction(event -> handleLogin(null));
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            showError("Introduce email y contraseña.");
            return;
        }

        User user = userService.login(email, password);

        if (user == null) {
            showError("Email o contraseña incorrectos.");
            return;
        }

        // Guardar sesión
        MainSessionHolder.getInstance().setCurrentUser(user);

        // Navegar según rol
        String view = resolveView(user.getRole());
        if (view == null) return;

        loadView(view, user);
    }

    private String resolveView(String role) {
        if (role == null || role.isBlank()) {
            showError("El usuario no tiene rol asignado.");
            return null;
        }
        return switch (role.toLowerCase()) {
            case "admin", "administrador" ->
                    "/org/example/paneljavafx/admin-view.fxml";
            case "gestor", "manager" ->
                    "/org/example/paneljavafx/gestor-view.fxml";
            case "cliente", "client" ->
                    "/org/example/paneljavafx/cliente-view.fxml";
            default -> {
                showError("Rol desconocido: " + role);
                yield null;
            }
        };
    }

    private void loadView(String fxmlPath, User user) {
        try {
            URL mainViewResource = getClass().getResource(
                    "/org/example/paneljavafx/main-view.fxml");

            FXMLLoader loader = new FXMLLoader(mainViewResource);
            Parent root = loader.load();

            // Inyectar usuario y primera vista en MainController
            MainController mainController = loader.getController();
            mainController.init(user, fxmlPath);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showError("No se pudo cargar la pantalla.");
        }
    }

    private void showError(String message) {
        lblError.setText(message);
    }
}