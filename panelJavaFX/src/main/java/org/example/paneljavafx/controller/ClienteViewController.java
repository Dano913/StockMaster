package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Posicion;
import org.example.paneljavafx.service.ClienteJsonService;

import java.util.Comparator;

public class ClienteViewController {

    // =========================
    // SERVICES
    // =========================
    private final ClienteJsonService clienteJsonService = ClienteJsonService.getInstance();

    // =========================
    // TABLES (FXML)
    // =========================
    @FXML private TableView<Cliente> clientsTable;
    @FXML private TableColumn<Cliente, String> colName;
    @FXML private TableColumn<Cliente, String> colEmail;

    @FXML private TableView<Posicion> inversionesTable;
    @FXML private TableColumn<Posicion, String> colFund;
    @FXML private TableColumn<Posicion, String> colAmount;

    @FXML private TextField searchField;

    // =========================
    // DATA
    // =========================
    private FilteredList<Cliente> filteredClientes;
    private FilteredList<Posicion> filteredPosiciones;
    private ObservableList<Posicion> globalPosiciones;

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {
        System.out.println("\n🚀 === ClienteViewController INICIALIZANDO ===");

        // 🔥 Carga y debug automático
        clienteJsonService.loadFromJson("data/clientes.json");
        System.out.println("✅ Clientes cargados. Configurando tablas...\n");

        setupClientsTable();
        setupInversionesTable();
        setupSearch();
    }

    // =========================
    // CLIENTES TABLE
    // =========================
    private void setupClientsTable() {
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre() + " " + d.getValue().getApellido()));

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail()));

        // 🔥 Datos desde servicio
        ObservableList<Cliente> source = clienteJsonService.getLastLoadedClientes();
        if (source == null || source.isEmpty()) {
            source = FXCollections.observableArrayList();
            System.out.println("⚠️ Tabla clientes vacía");
        }

        filteredClientes = new FilteredList<>(source);
        clientsTable.setItems(filteredClientes);

        // 🔥 Filtrado por selección cliente → sus posiciones
        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (filteredPosiciones == null) return;
            if (selected == null) {
                filteredPosiciones.setPredicate(null);
                return;
            }
            // Muestra SOLO posiciones del cliente seleccionado
            filteredPosiciones.setPredicate(p ->
                    clienteJsonService.getPosicionesByClienteId(selected.getIdCliente()).contains(p));
        });
    }

    // =========================
    // INVERSIONES TABLE
    // =========================
    private void setupInversionesTable() {
        colFund.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombreFondo() +
                        " (F#" + d.getValue().getIdFondo() + ")"));

        colAmount.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("€%.2f", d.getValue().getValorActual())));

        refreshGlobalPosiciones();
    }

    // =========================
    // GLOBAL POSICIONES
    // =========================
    private void refreshGlobalPosiciones() {
        globalPosiciones = clienteJsonService.getAllPosiciones();

        if (filteredPosiciones == null) {
            filteredPosiciones = new FilteredList<>(globalPosiciones);
            inversionesTable.setItems(filteredPosiciones);
        }
    }

    // =========================
    // SEARCH GLOBAL
    // =========================
    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = (newVal == null) ? "" : newVal.toLowerCase();

            // 🔥 Filtra CLIENTES
            if (filteredClientes != null) {
                filteredClientes.setPredicate(c ->
                        filter.isEmpty() ||
                                (c.getNombre() + " " + c.getApellido()).toLowerCase().contains(filter) ||
                                c.getEmail().toLowerCase().contains(filter));
            }

            // 🔥 Filtra POSICIONES
            if (filteredPosiciones != null) {
                if (filter.isEmpty()) {
                    filteredPosiciones.setPredicate(null);
                    clientsTable.getSelectionModel().clearSelection();
                } else {
                    filteredPosiciones.setPredicate(p ->
                            p.getNombreFondo().toLowerCase().contains(filter) ||
                                    String.valueOf(p.getIdFondo()).contains(filter));
                }
            }
        });
    }
}