package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.service.GestorService;

public class PersonalViewController {

    // =========================
    // SERVICE
    // =========================
    private final GestorService gestorService = new GestorService();

    // =========================
    // UI
    // =========================
    @FXML private TableView<Gestor> gestorsTable;

    @FXML private TableColumn<Gestor, String> colName;
    @FXML private TableColumn<Gestor, String> colEmail;
    @FXML private TableColumn<Gestor, String> colExperience;
    @FXML private TableColumn<Gestor, String> colRisk;

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

        // 🔥 carga datos desde datasource
        gestorService.load();

        setupTable();
        setupSearch();
    }

    // =========================
    // TABLE SETUP
    // =========================
    private void setupTable() {

        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail()));

        colExperience.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.valueOf(d.getValue().getAniosExperiencia())
                ));

        colRisk.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPerfilRiesgo()));

        filteredGestors = new FilteredList<>(gestorService.getAll());
        gestorsTable.setItems(filteredGestors);
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