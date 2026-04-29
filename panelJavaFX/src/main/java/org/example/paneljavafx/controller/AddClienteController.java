package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.service.ClienteService;

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
    private Cliente clienteActual;
    private ClienteViewController parent;

    private final ClienteService clienteService = ClienteService.getInstance();

    // =========================
    // INIT (ÚNICO PUNTO DE ENTRADA)
    // =========================
    public void init(Cliente cliente, ClienteViewController parent) {
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

    private void setupEditMode(Cliente cliente) {

        titleLabel.setText("Editar Cliente");
        deleteButton.setVisible(true);

        nombreField.setText(cliente.getNombre());
        apellidoField.setText(cliente.getApellido());
        emailField.setText(cliente.getEmail());
        dniField.setText(cliente.getDni());
        paisField.setText(cliente.getPais());
    }

    // =========================
    // ACTIONS
    // =========================
    @FXML
    private void saveCliente() {

        if (clienteActual == null) {
            // CREAR
            Cliente nuevo = new Cliente();
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
            clienteService.delete(clienteActual.getIdCliente());
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
    private void fillCliente(Cliente c) {
        c.setNombre(nombreField.getText());
        c.setApellido(apellidoField.getText());
        c.setEmail(emailField.getText());
        c.setDni(dniField.getText());
        c.setPais(paisField.getText());
    }
}