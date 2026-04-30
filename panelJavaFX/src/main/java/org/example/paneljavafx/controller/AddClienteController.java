package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.paneljavafx.model.Client;
import org.example.paneljavafx.service.ClientService;

public class AddClienteController {

    // =========================
    // UI
    // =========================
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private TextField emailField;
    @FXML private TextField dniField;
    @FXML private TextField paisField;

    @FXML private Button deleteButton;
    @FXML private Label titleLabel;

    // =========================
    // STATE
    // =========================
    private Client clienteActual;
    private ClienteViewController parent;

    private final ClientService clienteService = ClientService.getInstance();

    // =========================
    // INIT (ÚNICO PUNTO DE ENTRADA)
    // =========================
    public void init(Client cliente, ClienteViewController parent) {
        this.parent = parent;
        this.clienteActual = cliente;

        if (cliente == null) {
            setupCreateMode();
        } else {
            setupEditMode(cliente);
        }
    }

    // =========================
    // MODES
    // =========================
    private void setupCreateMode() {
        titleLabel.setText("Nuevo Cliente");
        deleteButton.setVisible(false);
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

    // =========================
    // ACTIONS
    // =========================
    @FXML
    private void saveCliente() {

        if (clienteActual == null) {
            // CREAR
            Client nuevo = new Client();
            fillCliente(nuevo);
            clienteService.save(nuevo);

        } else {
            // EDITAR
            fillCliente(clienteActual);
            clienteService.update(clienteActual);
        }

        parent.refreshTable();
        parent.closeAddClienteForm();
    }

    @FXML
    private void deleteCliente() {

        if (clienteActual != null) {
            clienteService.delete(clienteActual.getClientId());
            parent.refreshTable();
            parent.closeAddClienteForm();
        }
    }

    @FXML
    private void cancel() {
        parent.closeAddClienteForm();
    }

    // =========================
    // UTIL
    // =========================
    private void fillCliente(Client c) {
        c.setName(nombreField.getText());
        c.setSurname(apellidoField.getText());
        c.setEmail(emailField.getText());
        c.setNationalId(dniField.getText());
        c.setCountry(paisField.getText());
    }
}