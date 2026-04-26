package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Posicion;
import org.example.paneljavafx.service.ClienteService;

public class ClienteViewController {

    // =========================
    // UI
    // =========================
    @FXML private TableView<Cliente> clientsTable;
    @FXML private TableColumn<Cliente, String> colName;
    @FXML private TableColumn<Cliente, String> colEmail;

    @FXML private TableView<Posicion> inversionesTable;
    @FXML private TableColumn<Posicion, String> colFund;
    @FXML private TableColumn<Posicion, String> colAmount;

    @FXML private TextField searchField;

    // =========================
    // SERVICE
    // =========================
    private final ClienteService clienteService = new ClienteService();

    // =========================
    // STATE
    // =========================
    private FilteredList<Cliente> filteredClientes;

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {

        System.out.println("🚀 ClienteViewController inicializado");

        // ✔ correcto: ahora carga desde DataSource interno
        clienteService.load();

        setupClientsTable();
        setupInversionesTable();
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

                    if (selected == null) {
                        inversionesTable.setItems(FXCollections.observableArrayList());
                        return;
                    }

                    var posiciones =
                            clienteService.getPosicionesByClienteId(selected.getIdCliente());

                    inversionesTable.setItems(FXCollections.observableArrayList(posiciones));
                });
    }

    // =========================
    // POSICIONES TABLE
    // =========================
    private void setupInversionesTable() {

        colFund.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getNombreFondo() +
                                " (F#" + d.getValue().getIdFondo() + ")"
                )
        );

        colAmount.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("€%.2f", d.getValue().getValorActual())
                )
        );

        inversionesTable.setItems(FXCollections.observableArrayList());
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