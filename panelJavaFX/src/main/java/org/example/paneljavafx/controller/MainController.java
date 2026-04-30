package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.example.paneljavafx.MainApp;
import org.example.paneljavafx.model.User;
import org.example.paneljavafx.service.MainSessionHolder;
import org.example.paneljavafx.service.UserService;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML private StackPane contentArea;

    private User currentUser;

    private final UserService userService = UserService.getInstance();

    // =========================
    // LLAMADO DESDE LoginController
    // tras autenticar, le pasa el usuario y la primera vista
    // =========================
    public void init(User user, String firstViewPath) {
        this.currentUser = user;
        navigateTo(firstViewPath);
    }

    // =========================
    // NAVEGACIÓN — cualquier controlador hijo llama a esto
    // =========================
    public void navigateTo(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);

            if (resource == null) {
                System.err.println("❌ FXML no encontrado: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            // Inyectar usuario y referencia al MainController
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

    // Navegación con target (lista → detalle)
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

    // =========================
    // CABECERA — PERFIL
    // =========================
    @FXML
    private void handlePerfil() {
        String path = switch (currentUser.getRole().toLowerCase()) {
            case "admin", "administrador" ->
                    "/org/example/paneljavafx/admin-perfil.fxml";
            case "gestor" ->
                    "/org/example/paneljavafx/gestor-perfil.fxml";
            case "cliente" ->
                    "/org/example/paneljavafx/cliente-perfil.fxml";
            default -> null;
        };
        if (path != null) navigateTo(path);
    }

    // =========================
    // CABECERA — LOGOUT
    // =========================
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