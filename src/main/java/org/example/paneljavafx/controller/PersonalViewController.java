package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.service.GestorService;

public class PersonalViewController {

    // ========================= SERVICES =========================
    private final GestorService gestorService = GestorService.getInstance();

    // ========================= UI =========================
    @FXML private TableView<Gestor> gestorsTable;
    @FXML private TableColumn<Gestor, String> colName;
    @FXML private TableColumn<Gestor, String> colEmail;
    @FXML private TableColumn<Gestor, String> colExperience;
    @FXML private TableColumn<Gestor, String> colRisk;

    @FXML private TextField searchField;

    @FXML private StackPane gestorDetailContainer;

    // ========================= STATE =========================
    private FilteredList<Gestor> filteredGestors;

    // ========================= INIT =========================
    @FXML
    public void initialize() {
        setupTable();
        setupSearch();
        loadGestors();
    }

    // ========================= LOAD =========================
    private void loadGestors() {

        filteredGestors = new FilteredList<>(
                FXCollections.observableArrayList(gestorService.getAll()),
                p -> true
        );

        gestorsTable.setItems(filteredGestors);
    }

    // ========================= TABLE =========================
    private void setupTable() {

        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getName())
        );

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail())
        );

        colExperience.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.valueOf(d.getValue().getYearsOfExperience())
                )
        );

        colRisk.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getRiskProfile() == null
                                ? "SIN PERFIL"
                                : d.getValue().getRiskProfile().name()
                )
        );

        addActionColumn();

        gestorsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selected) -> {
                    if (selected != null) {
                        openGestionGestores(selected.getGestorId());
                    }
                });
    }

    // ========================= ACTION COLUMN =========================
    private void addActionColumn() {

        TableColumn<Gestor, Void> colAction = new TableColumn<>("Acciones");

        colAction.setCellFactory(param -> new TableCell<>() {

            private final Button btn = new Button("Abrir");

            {
                btn.setOnAction(e -> {
                    Gestor g = getTableView().getItems().get(getIndex());
                    openGestionGestores(g.getGestorId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        gestorsTable.getColumns().add(colAction);
    }

    // ========================= CORE RIGHT PANEL =========================
    private void openGestionGestores(int gestorId) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/gestionGestores-view.fxml")
            );

            Parent view = loader.load();

            GestionGestoresController controller = loader.getController();
            controller.initByGestorId(gestorId);

            gestorDetailContainer.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openAddGestor() {
        System.out.println("Abrir modal añadir gestor");
    }

    // ========================= SEARCH =========================
    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            String filter = newVal == null ? "" : newVal.toLowerCase();

            filteredGestors.setPredicate(g -> {

                if (filter.isEmpty()) return true;

                return g.getName().toLowerCase().contains(filter)
                        || g.getEmail().toLowerCase().contains(filter);
            });
        });
    }
}