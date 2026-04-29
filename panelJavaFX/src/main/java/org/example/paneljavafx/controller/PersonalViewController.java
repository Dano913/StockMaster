package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.service.ClienteService;
import org.example.paneljavafx.service.GestorService;

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
    @FXML private TableColumn<Gestor, String> colName;
    @FXML private TableColumn<Gestor, String> colEmail;
    @FXML private TableColumn<Gestor, String> colExperience;
    @FXML private TableColumn<Gestor, String> colRisk;

    @FXML private TextField searchField;

    // 🔥 OVERLAY SYSTEM (IGUAL QUE CLIENTE)
    @FXML private StackPane rootContainer;
    @FXML private VBox overlayContainer;

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

        setupTable();
        setupSearch();

        overlayContainer.setVisible(false);
        overlayContainer.setManaged(false);
    }

    // =========================
    // TABLE
    // =========================
    private void setupTable() {

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

        colRisk.setCellValueFactory(d -> {
            if (d.getValue().getPerfilRiesgo() == null) {
                return new SimpleStringProperty("SIN PERFIL");
            }
            return new SimpleStringProperty(d.getValue().getPerfilRiesgo().name());
        });

        filteredGestors = new FilteredList<>(
                FXCollections.observableArrayList(gestorService.getAll())
        );

        gestorsTable.setItems(filteredGestors);

        addActionColumn();
    }

    // =========================
    // ACTION COLUMN
    // =========================
    private void addActionColumn() {

        TableColumn<Gestor, Void> colAction = new TableColumn<>("Acciones");

        colAction.setCellFactory(param -> new TableCell<>() {

            private final Button btn = new Button("Ver / Editar");

            {
                btn.setOnAction(e -> {
                    Gestor g = getTableView().getItems().get(getIndex());
                    openEditGestor(g);
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
                        || (g.getPerfilRiesgo() != null &&
                        g.getPerfilRiesgo().name().toLowerCase().contains(filter))
                        || String.valueOf(g.getAniosExperiencia()).contains(filter);
            });
        });
    }

    // =========================
    // OPEN CREATE
    // =========================
    @FXML
    private void openAddGestor() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/gestor-form-view.fxml")
            );

            Parent form = loader.load();

            GestorFormController controller = loader.getController();
            controller.init(new Gestor(), true, this::closeOverlay);

            showOverlay(form);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // OPEN EDIT
    // =========================
    private void openEditGestor(Gestor gestor) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/gestor-form-view.fxml")
            );

            Parent form = loader.load();

            GestorFormController controller = loader.getController();
            controller.init(gestor, true, this::closeOverlay);

            showOverlay(form);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SHOW OVERLAY
    // =========================
    private void showOverlay(Parent form) {

        overlayContainer.getChildren().setAll(form);
        overlayContainer.setVisible(true);
        overlayContainer.setManaged(true);
    }

    // =========================
    // CLOSE OVERLAY
    // =========================
    public void closeOverlay() {

        overlayContainer.getChildren().clear();
        overlayContainer.setVisible(false);
        overlayContainer.setManaged(false);

        refreshTable();
    }

    // =========================
    // REFRESH TABLE
    // =========================
    public void refreshTable() {

        filteredGestors.setPredicate(filteredGestors.getPredicate());

        gestorsTable.refresh();
    }
}