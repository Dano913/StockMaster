package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.service.ClienteService;
import org.example.paneljavafx.service.GestorService;

import java.util.List;

public class PersonalViewController {

    // =========================
    // SERVICES
    // =========================
    private final GestorService gestorService = new GestorService();
    private final ClienteService clienteService = new ClienteService();

    // =========================
    // UI
    // =========================
    @FXML private TableView<Gestor> gestorsTable;
    @FXML private TableView<Cliente> clientsTable;

    @FXML private TableColumn<Gestor, String> colName;
    @FXML private TableColumn<Gestor, String> colEmail;
    @FXML private TableColumn<Gestor, String> colExperience;
    @FXML private TableColumn<Gestor, String> colRisk;

    @FXML private TableColumn<Cliente, String> colClientName;
    @FXML private TableColumn<Cliente, String> colClientEmail;

    @FXML private TextField searchField;

    // =========================
    // STATE
    // =========================
    private FilteredList<Gestor> filteredGestors;

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {

        gestorService.load();
        clienteService.load();

        setupGestorTable();
        setupClientTable();
        setupSearch();
        setupSelection();
    }

    // =========================
    // GESTOR TABLE
    // =========================
    private void setupGestorTable() {

        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail()));

        colExperience.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getAniosExperiencia())));

        colRisk.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPerfilRiesgo()));

        filteredGestors = new FilteredList<>(gestorService.getAll());
        gestorsTable.setItems(filteredGestors);
    }

    // =========================
    // CLIENT TABLE
    // =========================
    private void setupClientTable() {

        colClientName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre() + " " + d.getValue().getApellido()));

        colClientEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail()));

        clientsTable.setItems(javafx.collections.FXCollections.observableArrayList());
    }

    // =========================
    // SELECTION LOGIC (🔥 LO IMPORTANTE)
    // =========================
    private void setupSelection() {

        gestorsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, gestor) -> {

                    if (gestor == null) {
                        clientsTable.getItems().clear();
                        return;
                    }

                    List<Cliente> clientes = clienteService.getAll().stream()
                            .filter(c -> c.getGestor() == gestor.getIdGestor())
                            .toList();

                    clientsTable.getItems().setAll(clientes);

                    System.out.println("👨‍💼 Gestor seleccionado: " + gestor.getNombre());
                    System.out.println("   └── Clientes encontrados: " + clientes.size());
                });
    }

    // =========================
    // SEARCH
    // =========================
    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            String filter = (newVal == null) ? "" : newVal.toLowerCase();

            filteredGestors.setPredicate(g -> {

                if (filter.isEmpty()) return true;

                return g.getNombre().toLowerCase().contains(filter)
                        || g.getEmail().toLowerCase().contains(filter)
                        || g.getPerfilRiesgo().toLowerCase().contains(filter)
                        || String.valueOf(g.getAniosExperiencia()).contains(filter);
            });
        });
    }
}