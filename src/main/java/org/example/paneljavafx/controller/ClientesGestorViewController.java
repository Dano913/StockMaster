package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.example.paneljavafx.model.Client;

import org.example.paneljavafx.service.GestorService;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class ClientesGestorViewController {

    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, Void> colActions;

    @FXML private StackPane clienteDetailContainer;
    @FXML private StackPane overlayContainer;
    @FXML private Label labelManagedWallet;
    @FXML private Label labelTotalClientes;

    private final GestorService gestorService = GestorService.getInstance();
    @FXML private TextField searchField;
    private FilteredList<Client> filteredClients;

    @FXML
    public void initialize() {

        setupTable();
        loadClients();
        setupSearch();
        updateWallet();
        updateTotalClientes();
    }

    // ========================= LOAD =========================
    private void loadClients() {

        filteredClients = new FilteredList<>(
                gestorService.getVisibleMyClientsObservable(),
                p -> true
        );

        SortedList<Client> sorted = new SortedList<>(filteredClients);
        sorted.comparatorProperty().bind(clientsTable.comparatorProperty());

        clientsTable.setItems(sorted);
    }

    private int gestorId;

    public void initByGestorId(int gestorId) {
        this.gestorId = gestorId;
        loadClientsByGestor();
    }

    private void loadClientsByGestor() {
        var clients = gestorService.getClientsByGestorId(gestorId);

        filteredClients = new FilteredList<>(
                FXCollections.observableArrayList(clients),
                p -> true
        );

        clientsTable.setItems(filteredClients);
    }

    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            String filter = newVal.toLowerCase().trim();

            filteredClients.setPredicate(client -> {

                if (filter.isEmpty()) return true;

                return client.getName().toLowerCase().contains(filter)
                        || client.getSurname().toLowerCase().contains(filter)
                        || client.getEmail().toLowerCase().contains(filter);
            });
        });
    }

    private void updateWallet() {

        double wallet = gestorService.calculateManagedWallet();

        labelManagedWallet.setText(String.format("€%.2f", wallet));
    }

    private void updateTotalClientes() {

        int total = gestorService.getVisibleMyClientsObservable().size();

        labelTotalClientes.setText(String.valueOf(total));
    }


    // ========================= TABLE SETUP =========================
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

    // ========================= VIEW CLIENT =========================
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

                            System.out.println("🗑 Cliente eliminado: " + client.getEmail());

                            loadClients();
                            updateTotalClientes();
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

    // ========================= ADD =========================
    @FXML
    private void openAddCliente() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/add-cliente-view.fxml")
            );

            Parent view = loader.load();

            AddClienteController controller = loader.getController();

            controller.init(null, () -> {
                overlayContainer.setVisible(false);
                overlayContainer.getChildren().clear();
                loadClients();
                updateTotalClientes();
            });

            overlayContainer.getChildren().setAll(view);

            StackPane.setAlignment(view, javafx.geometry.Pos.CENTER);

            overlayContainer.setVisible(true);
            overlayContainer.setManaged(true);

            overlayContainer.prefWidthProperty()
                    .bind(clientsTable.getScene().widthProperty());

            overlayContainer.prefHeightProperty()
                    .bind(clientsTable.getScene().heightProperty());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openEditCliente(Client client) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/add-cliente-view.fxml")
            );

            Parent view = loader.load();

            AddClienteController controller = loader.getController();

            controller.init(client, () -> {

                overlayContainer.setVisible(false);
                overlayContainer.getChildren().clear();

                loadClients();
            });

            overlayContainer.getChildren().setAll(view);
            overlayContainer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}