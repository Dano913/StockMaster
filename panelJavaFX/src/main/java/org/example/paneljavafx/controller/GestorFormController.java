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
    @FXML private ComboBox<Gestor.PerfilRiesgo> perfilRiesgoField;

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

        perfilRiesgoField.setItems(FXCollections.observableArrayList(Gestor.PerfilRiesgo.values()));

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
                FXCollections.observableArrayList(Gestor.PerfilRiesgo.values())
        );

        loadData();

        applyMode(editable, gestor.getIdGestor() == 0);

        deleteButton.setVisible(gestor.getIdGestor() != 0 && editable);
    }

    // =========================
    // LOAD
    // =========================
    private void loadData() {

        nombreField.setText(gestor.getNombre());
        apellidosField.setText(gestor.getApellidos());
        emailField.setText(gestor.getEmail());
        dniField.setText(gestor.getDni());
        telefonoField.setText(gestor.getTelefono());

        experienciaField.setText(
                gestor.getAniosExperiencia() == 0 ? "" : String.valueOf(gestor.getAniosExperiencia())
        );

        perfilRiesgoField.setValue(gestor.getPerfilRiesgo());
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

        gestor.setNombre(nombreField.getText());
        gestor.setApellidos(apellidosField.getText());
        gestor.setEmail(emailField.getText());
        gestor.setDni(dniField.getText());
        gestor.setTelefono(telefonoField.getText());

        String exp = experienciaField.getText();
        gestor.setAniosExperiencia(
                (exp == null || exp.isBlank()) ? 0 : Integer.parseInt(exp)
        );

        gestor.setPerfilRiesgo(perfilRiesgoField.getValue());

        if (gestor.getIdGestor() == 0) {
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

        Integer id = gestor.getIdGestor();

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