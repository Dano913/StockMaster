package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.example.paneljavafx.model.User;

public class GestorViewController implements UserAware {

    @FXML private StackPane contentContainer;

    private User user;

    @Override
    public void setUser(User user) {
        this.user = user;
        loadClientesView();
    }

    @Override
    public void setMainController(MainController mainController) {}

    private void loadClientesView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/clientesGestion-view.fxml")
            );

            Parent view = loader.load();
            contentContainer.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}