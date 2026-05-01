package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.paneljavafx.model.User;
import org.example.paneljavafx.service.MainSessionHolder;
import org.example.paneljavafx.service.ProfileService;
import org.example.paneljavafx.service.dto.ClientProfileDTO;
import org.example.paneljavafx.service.dto.GestorProfileDTO;

public class ProfileController {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;

    private final ProfileService profileService = new ProfileService();

    @FXML
    public void initialize() {
        loadData();
    }

    private void loadData() {

        User user = MainSessionHolder.getInstance().getCurrentUser();
        if (user == null) return;

        emailLabel.setText(user.getEmail());
        roleLabel.setText(user.getRole());

        Object profile = profileService.getProfile(user);

        if (profile instanceof ClientProfileDTO c) {
            nameLabel.setText(c.getName() + " " + c.getSurname());
        }

        if (profile instanceof GestorProfileDTO g) {
            nameLabel.setText(g.getName() + " " + g.getSurname());
        }
    }
}