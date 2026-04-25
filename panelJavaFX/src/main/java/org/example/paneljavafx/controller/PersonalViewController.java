package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.model.TimeRecord;
import org.example.paneljavafx.service.GestorService;
import org.example.paneljavafx.service.TimeRecordService;

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
    private final GestorService gestorService = GestorService.getInstance();
    private final TimeRecordService timeRecordService = TimeRecordService.getInstance();

    // =========================
    // TABLES
    // =========================
    @FXML private TableView<Gestor> gestorsTable;
    @FXML private TableColumn<Gestor, String> colName;
    @FXML private TableColumn<Gestor, String> colEmail;
    @FXML private TableColumn<Gestor, String> colExperience;
    @FXML private TableColumn<Gestor, String> colRisk;

    @FXML private TableView<TimeRecord> timeRecordsTable;
    @FXML private TableColumn<TimeRecord, String> colUser;
    @FXML private TableColumn<TimeRecord, String> colDateTime;
    @FXML private TableColumn<TimeRecord, String> colType;

    @FXML private TextField searchField;

    // =========================
    // DATA
    // =========================
    private FilteredList<Gestor> filteredGestors;
    private FilteredList<TimeRecord> filteredRecords;
    private javafx.collections.ObservableList<TimeRecord> globalRecords;

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {
        setupGestorsTable();
        setupTimeRecordsTable();
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
                new SimpleStringProperty(
                        String.valueOf(d.getValue().getAnios_experiencia())
                ));

        colRisk.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPerfil_riesgo()));

        filteredGestors = new FilteredList<>(gestorService.getAllGestores());
        gestorsTable.setItems(filteredGestors);

        gestorsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {

            if (selected == null) {
                filteredRecords.setPredicate(null);
                return;
            }

            filteredRecords.setPredicate(r ->
                    r.getUser().equals(selected.getNombre()));
        });
    }

    // =========================
    // TIME RECORDS TABLE
    // =========================
    private void setupTimeRecordsTable() {

        colUser.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getUser()));

        colDateTime.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getDateTime().format(formatter)
                ));

        colType.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getType()));

        refreshGlobalTable();
    }

    // =========================
    // GLOBAL RECORDS
    // =========================
    private void refreshGlobalTable() {

        var sorted = timeRecordService.getAllRecords().stream()
                .sorted(Comparator.comparing(TimeRecord::getDateTime).reversed())
                .toList();

        if (globalRecords == null) {
            globalRecords = FXCollections.observableArrayList(sorted);
            filteredRecords = new FilteredList<>(globalRecords);
            timeRecordsTable.setItems(filteredRecords);
        } else {
            globalRecords.setAll(sorted);
        }
    }

    // =========================
    // SEARCH
    // =========================
    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            // FILTER GESTORS
            filteredGestors.setPredicate(g ->
                    newVal == null || newVal.isEmpty()
                            || g.getNombre().toLowerCase().contains(newVal.toLowerCase())
                            || g.getEmail().toLowerCase().contains(newVal.toLowerCase())
            );

            // RESET FILTERS
            if (newVal == null || newVal.isEmpty()) {
                filteredRecords.setPredicate(null);
                gestorsTable.getSelectionModel().clearSelection();
                return;
            }

            // FILTER RECORDS
            filteredRecords.setPredicate(r ->
                    r.getUser().toLowerCase().contains(newVal.toLowerCase())
            );
        });
    }
}