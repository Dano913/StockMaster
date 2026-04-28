package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.service.ClienteService;
import org.example.paneljavafx.service.GestorService;

import java.util.List;
import java.util.Objects;

public class PersonalViewController {

    // =========================
    // SERVICES
    // =========================
    private final GestorService gestorService = GestorService.getInstance();
    private final ClienteService clienteService = ClienteService.getInstance();

    // =========================
    // UI
    // =========================
    @FXML private TableView<Gestor> gestorsTable;
    @FXML
    private VBox gestorDetailContainer;

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

        // 🔥 IMPORTANTE: cargar datos
        gestorService.load();
        clienteService.load();

        setupGestorTable();
        setupSearch();
        setupSelection();
    }

    // =========================
    // GESTOR TABLE
    // =========================
    private void setupGestorTable() {

        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre())
        );

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail())
        );

        colExperience.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.valueOf(d.getValue().getAniosExperiencia())
                )
        );

        colRisk.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPerfilRiesgo())
        );

        filteredGestors = new FilteredList<>(gestorService.getAll());

        gestorsTable.setItems(filteredGestors);
    }

    // =========================
    // SELECTION
    // =========================
    private void setupSelection() {

        gestorsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, gestor) -> {

                    if (gestor == null) {
                        gestorDetailContainer.getChildren().clear();
                        return;
                    }

                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/org/example/paneljavafx/gestor-privado-view.fxml")
                        );

                        Parent view = loader.load();

                        GestorPrivadoViewController controller = loader.getController();
                        controller.setGestor(gestor);

                        gestorDetailContainer.getChildren().setAll(view);

                        System.out.println("👨‍💼 Gestor seleccionado: " + gestor.getNombre());

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