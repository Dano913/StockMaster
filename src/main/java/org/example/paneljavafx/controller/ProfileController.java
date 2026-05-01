package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.paneljavafx.model.User;

public class ProfileController implements UserAware {

    @FXML private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML private Label roleLabel;

    private User user;

    @Override
    public void setUser(User user) {
        this.user = user;
        loadData();
    }

    private void loadData() {
        if (user == null) return;

        emailLabel.setText(user.getEmail());
        roleLabel.setText(user.getRole());
    }

    @Override
    public void setMainController(MainController mainController) {}
}