package org.example.paneljavafx.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.service.GestorService;

public class GestorFormController {

    @FXML private TextField nombreField;
    @FXML private TextField apellidosField;
    @FXML private TextField emailField;
    @FXML private TextField dniField;
    @FXML private TextField telefonoField;
    @FXML private TextField experienciaField;
    @FXML private ComboBox<Gestor.RiskProfile> perfilRiesgoField;

    @FXML private Button deleteButton; // 🔥 AÑADIDO

    private Gestor gestor;
    private Runnable onClose;

    private final GestorService gestorService = GestorService.getInstance();

    // =========================
    // CREATE
    // =========================
    public void initCreate(Runnable onClose) {

        this.gestor = new Gestor();
        this.onClose = onClose;

        perfilRiesgoField.setItems(FXCollections.observableArrayList(Gestor.RiskProfile.values()));

        applyMode(true, true); // editable + sin delete
        clearFields();
        deleteButton.setVisible(false);
    }

    // =========================
    // VIEW / EDIT
    // =========================
    public void init(Gestor gestor, boolean editable, Runnable onClose) {

        this.gestor = gestor;
        this.onClose = onClose;

        perfilRiesgoField.setItems(
                FXCollections.observableArrayList(Gestor.RiskProfile.values())
        );

        loadData();

        applyMode(editable, gestor.getGestorId() == 0);

        deleteButton.setVisible(gestor.getGestorId() != 0 && editable);
    }

    // =========================
    // LOAD
    // =========================
    private void loadData() {

        nombreField.setText(gestor.getName());
        apellidosField.setText(gestor.getSurname());
        emailField.setText(gestor.getEmail());
        dniField.setText(gestor.getNationalId());
        telefonoField.setText(gestor.getPhone());

        experienciaField.setText(
                gestor.getYearsOfExperience() == 0 ? "" : String.valueOf(gestor.getYearsOfExperience())
        );

        perfilRiesgoField.setValue(gestor.getRiskProfile());
    }

    // =========================
    // MODE FIX (CLAVE)
    // =========================
    private void applyMode(boolean editable, boolean isNew) {

        nombreField.setDisable(!editable);
        apellidosField.setDisable(!editable);
        emailField.setDisable(!editable);
        dniField.setDisable(!editable);
        telefonoField.setDisable(!editable);
        experienciaField.setDisable(!editable);

        perfilRiesgoField.setDisable(!editable);
    }

    // =========================
    // SAVE
    // =========================
    @FXML
    private void save() {

        gestor.setName(nombreField.getText());
        gestor.setSurname(apellidosField.getText());
        gestor.setEmail(emailField.getText());
        gestor.setNationalId(dniField.getText());
        gestor.setPhone(telefonoField.getText());

        String exp = experienciaField.getText();
        gestor.setYearsOfExperience(
                (exp == null || exp.isBlank()) ? 0 : Integer.parseInt(exp)
        );

        gestor.setRiskProfile(perfilRiesgoField.getValue());

        if (gestor.getGestorId() == 0) {
            gestorService.save(gestor);
        } else {
            gestorService.update(gestor);
        }

        close();
    }

    // =========================
    // DELETE 🔥 NUEVO
    // =========================
    @FXML
    private void delete() {

        Integer id = gestor.getGestorId();

        if (id == null || id == 0) return;

        gestorService.delete(id);
        close();
    }

    @FXML
    private void cancel() {
        close();
    }

    private void close() {
        if (onClose != null) onClose.run();
    }

    private void clearFields() {
        nombreField.clear();
        apellidosField.clear();
        emailField.clear();
        dniField.clear();
        telefonoField.clear();
        experienciaField.clear();
    }
}