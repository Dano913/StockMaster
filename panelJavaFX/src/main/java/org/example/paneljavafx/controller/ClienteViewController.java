package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.service.ClienteService;

public class ClienteViewController {

    @FXML private TableView<Cliente> clientsTable;
    @FXML private TableColumn<Cliente, String> colName;
    @FXML private TableColumn<Cliente, String> colEmail;

    @FXML private TextField searchField;

    // contenedor donde se inserta la vista del cliente privado
    @FXML private javafx.scene.layout.VBox clienteDetailContainer;

    private final ClienteService clienteService = ClienteService.getInstance();
    private FilteredList<Cliente> filteredClientes;

    @FXML
    public void initialize() {

        clienteService.load();

        setupClientsTable();
        setupSearch();
    }

    // =========================
    // CLIENTES TABLE
    // =========================
    private void setupClientsTable() {

        colName.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getNombre() + " " + d.getValue().getApellido()
                )
        );

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail())
        );

        filteredClientes = new FilteredList<>(clienteService.getAll());
        clientsTable.setItems(filteredClientes);

        clientsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selected) -> {

                    if (selected == null) return;

                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource(
                                        "/org/example/paneljavafx/cliente-privado-view.fxml"
                                )
                        );

                        Parent view = loader.load();

                        ClientePrivadoViewController controller =
                                loader.getController();

                        controller.loadCliente(selected);

                        // insertamos la vista en el panel derecho
                        clienteDetailContainer.getChildren().setAll(view);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    // =========================
    // SEARCH
    // =========================
    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            String filter = (newVal == null) ? "" : newVal.toLowerCase();

            filteredClientes.setPredicate(c -> {

                if (filter.isEmpty()) return true;

                return c.getNombre().toLowerCase().contains(filter)
                        || c.getApellido().toLowerCase().contains(filter)
                        || c.getEmail().toLowerCase().contains(filter);
            });
        });
    }
}