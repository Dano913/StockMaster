package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.service.GestorJsonService;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class PersonalViewController {

    // =========================
    // FORMAT
    // =========================
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =========================
    // SERVICES
    // =========================
    private final GestorJsonService gestorJsonService = GestorJsonService.getInstance();


    // =========================
    // TABLES
    // =========================
    @FXML private TableView<Gestor> gestorsTable;
    @FXML private TableColumn<Gestor, String> colName;
    @FXML private TableColumn<Gestor, String> colEmail;
    @FXML private TableColumn<Gestor, String> colExperience;
    @FXML private TableColumn<Gestor, String> colRisk;


    @FXML private TextField searchField;

    // =========================
    // DATA
    // =========================
    private FilteredList<Gestor> filteredGestors;

    // =========================
    // INIT - OPTIMIZADO
    // =========================
    @FXML
    public void initialize() {
        System.out.println("\n🚀 === PersonalViewController INICIALIZANDO ===");

        // 🔥 CARGA JSON UNA SOLA VEZ (imprime automáticamente)
        gestorJsonService.loadFromJson("data/gestores.json");
        System.out.println("✅ Carga JSON completada. Configurando tablas...\n");

        setupGestorsTable();
        setupSearch();
    }

    // =========================
    // GESTORS TABLE
    // =========================
    private void setupGestorsTable() {
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail()));

        colExperience.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getAniosExperiencia())));

        colRisk.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPerfilRiesgo()));

        // 🔥 Usa los datos ya cargados
        ObservableList<Gestor> source = gestorJsonService.getLastLoadedGestores();

        if (source == null || source.isEmpty()) {
            source = FXCollections.observableArrayList();
            System.out.println("⚠️  No hay datos de gestores - tabla vacía");
        }

        filteredGestors = new FilteredList<>(source);
        gestorsTable.setItems(filteredGestors);
    }


    // =========================
    // GLOBAL RECORDS
    // =========================

    // =========================
    // SEARCH
    // =========================
    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = (newVal == null) ? "" : newVal.toLowerCase();

            // Filtrar gestores
            if (filteredGestors != null) {
                filteredGestors.setPredicate(g ->
                        filter.isEmpty() ||
                                g.getNombre().toLowerCase().contains(filter) ||
                                g.getEmail().toLowerCase().contains(filter));
            }

        });
    }
}