package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.paneljavafx.model.Client;
import org.example.paneljavafx.service.ClientService;

public class AddClienteController {

    // ========================= UI =========================
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private TextField emailField;
    @FXML private TextField dniField;
    @FXML private TextField paisField;

    @FXML private Button deleteButton;
    @FXML private Label titleLabel;

    // ========================= STATE =========================
    private Client clienteActual;
    private Runnable onFinish;

    private final ClientService clienteService = ClientService.getInstance();

    // ========================= INIT =========================
    public void init(Client cliente, Runnable onFinish) {
        this.clienteActual = cliente;
        this.onFinish = onFinish;

        if (cliente == null) {
            setupCreateMode();
        } else {
            setupEditMode(cliente);
        }
    }

    // ========================= MODES =========================
    private void setupCreateMode() {
        titleLabel.setText("Nuevo Cliente");
        deleteButton.setVisible(false);
        clearForm();
    }

    private void setupEditMode(Client cliente) {

        titleLabel.setText("Editar Cliente");
        deleteButton.setVisible(true);

        nombreField.setText(cliente.getName());
        apellidoField.setText(cliente.getSurname());
        emailField.setText(cliente.getEmail());
        dniField.setText(cliente.getNationalId());
        paisField.setText(cliente.getCountry());
    }

    // ========================= ACTIONS =========================
    @FXML
    private void saveCliente() {

        if (clienteActual == null) {
            Client nuevo = new Client();
            fillCliente(nuevo);
            clienteService.save(nuevo);
        } else {
            fillCliente(clienteActual);
            clienteService.update(clienteActual);
        }

        finish();
    }

    @FXML
    private void deleteCliente() {

        if (clienteActual != null) {
            clienteService.delete(clienteActual.getClientId());
        }

        finish();
    }

    @FXML
    private void cancel() {
        finish();
    }

    // ========================= FINISH =========================
    private void finish() {
        if (onFinish != null) {
            onFinish.run();
        }
    }

    // ========================= UTIL =========================
    private void fillCliente(Client c) {
        c.setName(nombreField.getText());
        c.setSurname(apellidoField.getText());
        c.setEmail(emailField.getText());
        c.setNationalId(dniField.getText());
        c.setCountry(paisField.getText());
    }

    private void clearForm() {
        nombreField.clear();
        apellidoField.clear();
        emailField.clear();
        dniField.clear();
        paisField.clear();
    }
}