package org.example.paneljavafx.controller;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.example.paneljavafx.model.Client;
import org.example.paneljavafx.service.ClientService;

public class GestorClientesController {

    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, Void> colActions;

    @FXML private StackPane clienteDetailContainer;

    private FilteredList<Client> filteredClientes;

    private final ClientService clienteService = ClientService.getInstance();

    // ========================= INIT =========================
    @FXML
    public void initialize() {
        setupClientsTable();
    }

    // ========================= TABLE SETUP =========================
    private void setupClientsTable() {

        colName.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getName() + " " + d.getValue().getSurname()
                )
        );

        colEmail.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getEmail())
        );

        colActions.setCellFactory(param -> createActionsCell());

        filteredClientes = new FilteredList<>(clienteService.getAll());
        clientsTable.setItems(filteredClientes);

        clientsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selected) -> onClientSelected(selected));
    }

    // ========================= SELECTION HANDLER =========================
    private void onClientSelected(Client selected) {

        if (selected == null) return;

        openClientView(selected);
    }

    // ========================= NAVIGATION =========================
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

    // ========================= ACTIONS CELL =========================
    private TableCell<Client, Void> createActionsCell() {

        return new TableCell<>() {

            private final Button btn = new Button("Ver / Editar");

            {
                btn.setOnAction(event -> {

                    Client cliente = getTableView().getItems().get(getIndex());

                    if (cliente != null) {
                        openEditCliente(cliente);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
    }

    @FXML
    private void openAddCliente() {
        System.out.println("Abrir formulario cliente");
    }

    // ========================= EDIT =========================
    private void openEditCliente(Client cliente) {
        System.out.println("Editar cliente: " + cliente.getName());
        // aquí tu lógica de edición
    }
}