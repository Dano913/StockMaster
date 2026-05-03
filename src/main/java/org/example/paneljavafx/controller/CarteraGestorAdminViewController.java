package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.example.paneljavafx.model.Client;
import org.example.paneljavafx.service.GestorService;

public class CarteraGestorAdminViewController {

    // ========================= UI =========================
    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, Void> colActions;

    @FXML private StackPane clienteDetailContainer;
    @FXML private StackPane overlayContainer;

    @FXML private Label labelManagedWallet;
    @FXML private Label labelTotalClientes;
    @FXML private PieChart clientsPieChart;

    @FXML private TextField searchField;

    // ========================= SERVICE =========================
    private final GestorService gestorService = GestorService.getInstance();

    // ========================= STATE =========================
    private int gestorId;
    private FilteredList<Client> filteredClients;

    // ========================= INIT =========================
    @FXML
    public void initialize() {
        setupTable();
        setupSearch();    }

    // ========================= ENTRY POINT =========================
    public void initByGestorId(int gestorId) {
        this.gestorId = gestorId;

        loadClients();
        refreshUI();
    }

    // ========================= LOAD =========================
    private void loadClients() {

        var clients = gestorService.getClientsByGestorId(gestorId);

        filteredClients = new FilteredList<>(
                FXCollections.observableArrayList(clients),
                p -> true
        );

        SortedList<Client> sorted = new SortedList<>(filteredClients);
        sorted.comparatorProperty().bind(clientsTable.comparatorProperty());

        clientsTable.setItems(sorted);
    }

    // ========================= UI REFRESH =========================
    private void refreshUI() {
        updateWallet();
        updateTotalClientes();
        updateClientsPieChart();
    }

    // ========================= TABLE =========================
    private void setupTable() {

        colName.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getName() + " " + d.getValue().getSurname()
                )
        );

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail())
        );

        colActions.setCellFactory(param -> createActionsCell());

        clientsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selected) -> {
                    if (selected != null) openClientView(selected);
                });
    }

    // ========================= SEARCH =========================
    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            String filter = newVal == null ? "" : newVal.toLowerCase().trim();

            filteredClients.setPredicate(client -> {

                if (filter.isEmpty()) return true;

                return client.getName().toLowerCase().contains(filter)
                        || client.getSurname().toLowerCase().contains(filter)
                        || client.getEmail().toLowerCase().contains(filter);
            });
        });
    }

    // ========================= PIE CHART =========================
    private void updateClientsPieChart() {

        var clients = gestorService.getClientsByGestorId(gestorId);

        var data = clients.stream()
                .map(c -> new PieChart.Data(
                        c.getName() + " " + c.getSurname(),
                        gestorService.getClientTotalValue(c.getClientId())
                ))
                .toList();

        clientsPieChart.setData(FXCollections.observableArrayList(data));
    }

    // ========================= KPI =========================
    private void updateWallet() {

        double wallet = gestorService.calculateManagedWalletByGestor(gestorId);

        labelManagedWallet.setText(String.format("€%.2f", wallet));
    }

    private void updateTotalClientes() {

        int total = gestorService.getClientsByGestorId(gestorId).size();

        labelTotalClientes.setText(String.valueOf(total));
    }

    // ========================= CLIENT VIEW =========================
    private void openClientView(Client client) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/cliente-view.fxml")
            );

            Parent view = loader.load();

            ClienteViewController controller = loader.getController();
            controller.setCliente(client);

            clienteDetailContainer.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========================= ACTIONS =========================
    private TableCell<Client, Void> createActionsCell() {

        return new TableCell<>() {

            private final Button deleteBtn = new Button("🗑 Borrar");

            {
                deleteBtn.setOnAction(event -> {

                    Client client = getTableView().getItems().get(getIndex());
                    if (client == null) return;

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmar eliminación");
                    alert.setHeaderText("Eliminar cliente");
                    alert.setContentText("¿Seguro que quieres borrar a " +
                            client.getName() + " " + client.getSurname() + "?");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {

                            gestorService.deleteClient(client.getClientId());

                            loadClients();
                            refreshUI();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        };
    }

    // ========================= ADD CLIENT =========================
    @FXML
    private void openAddCliente() {
        System.out.println("Add cliente para gestor " + gestorId);
    }
}