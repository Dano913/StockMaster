package org.example.stockmaster.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.stockmaster.javafx.model.User;
import org.example.stockmaster.javafx.service.UserService;

public class ProfileController {

    // =========================
    // STATE
    // =========================
    private User user;
    private final UserService userService = UserService.getInstance();

    // =========================
    // UI (FXML)
    // =========================
    @FXML private Label idLabel;

    @FXML private Label nombreLabel;
    @FXML private Label emailLabel;
    @FXML private Label edadLabel;
    @FXML private Label generoLabel;
    @FXML private Label telefonoLabel;
    @FXML private Label rolLabel;
    @FXML private Label dniLabel;
    @FXML private Label passwordLabel;

    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private TextField edadField;
    @FXML private TextField generoField;
    @FXML private TextField telefonoField;
    @FXML private TextField rolField;
    @FXML private TextField dniField;
    @FXML private TextField passwordField;

    @FXML private Button btnEdit;
    @FXML private Button btnSave;
    @FXML private Label statusLabel;

    // =========================
    // PUBLIC METHODS
    // =========================
    public void setUser(User user) {
        this.user = user;
        if (user == null) return;
        updateView();
        setEditMode(false);
    }

    // =========================
    // PRIVATE METHODS
    // =========================
    private void updateView() {
        if (idLabel == null) return;

        idLabel.setText(user.getId());

        nombreLabel.setText(user.getName());
        emailLabel.setText(user.getEmail());
        edadLabel.setText(user.getAge());
        generoLabel.setText(user.getGender());
        telefonoLabel.setText(user.getPhone());
        rolLabel.setText(user.getRole());
        dniLabel.setText(user.getDni());
        passwordLabel.setText(user.getPassword());

        idLabel.setText(user.getId());
        nombreField.setText(user.getName());
        emailField.setText(user.getEmail());
        edadField.setText(user.getAge());
        generoField.setText(user.getGender());
        telefonoField.setText(user.getPhone());
        rolField.setText(user.getRole());
        dniField.setText(user.getDni());
        statusLabel.setText("");
        passwordField.setText(user.getPassword());
    }

    private void setEditMode(boolean editing) {
        nombreLabel.setVisible(!editing);   nombreLabel.setManaged(!editing);
        emailLabel.setVisible(!editing);    emailLabel.setManaged(!editing);
        edadLabel.setVisible(!editing);     edadLabel.setManaged(!editing);
        generoLabel.setVisible(!editing);   generoLabel.setManaged(!editing);
        telefonoLabel.setVisible(!editing); telefonoLabel.setManaged(!editing);
        rolLabel.setVisible(!editing);      rolLabel.setManaged(!editing);
        dniLabel.setVisible(!editing);      dniLabel.setManaged(!editing);
        passwordLabel.setVisible(!editing); passwordLabel.setManaged(!editing);

        nombreField.setVisible(editing);    nombreField.setManaged(editing);
        emailField.setVisible(editing);     emailField.setManaged(editing);
        edadField.setVisible(editing);      edadField.setManaged(editing);
        generoField.setVisible(editing);    generoField.setManaged(editing);
        telefonoField.setVisible(editing);  telefonoField.setManaged(editing);
        rolField.setVisible(editing);       rolField.setManaged(editing);
        dniField.setVisible(editing);       dniField.setManaged(editing);
        passwordField.setVisible(editing);  passwordField.setManaged(editing);

        btnEdit.setVisible(!editing);  btnEdit.setManaged(!editing);
        btnSave.setVisible(editing);   btnSave.setManaged(editing);
    }

    // =========================
    // HANDLERS
    // =========================
    @FXML
    private void handleEdit() {
        nombreField.setText(nombreLabel.getText());
        emailField.setText(emailLabel.getText());
        edadField.setText(edadLabel.getText());
        generoField.setText(generoLabel.getText());
        telefonoField.setText(telefonoLabel.getText());
        rolField.setText(rolLabel.getText());
        dniField.setText(dniLabel.getText());
        passwordField.setText(passwordLabel.getText());
        statusLabel.setText("");
        setEditMode(true);
    }

    @FXML
    private void handleSave() {
        if (user == null) return;

        user.setName(nombreField.getText());
        user.setEmail(emailField.getText());
        user.setAge(edadField.getText());
        user.setGender(generoField.getText());
        user.setPhone(telefonoField.getText());
        user.setRole(rolField.getText());
        user.setProfile(rolField.getText());
        user.setDni(dniField.getText());
        user.setPassword(passwordField.getText());

        userService.save();

        updateView();
        setEditMode(false);
        statusLabel.setText("✔ Guardado");
    }
}