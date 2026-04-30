package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.paneljavafx.model.Client;
import org.example.paneljavafx.service.ClientService;

public class ClienteViewController {

    private final ClientService clienteService = ClientService.getInstance();

    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, Void> colActions;

    @FXML private TextField searchField;
    @FXML private Label titleLabel;

    @FXML private VBox clienteDetailContainer;

    @FXML private StackPane rootContainer;
    @FXML private VBox overlayContainer;

    private FilteredList<Client> filteredClientes;

    @FXML
    public void initialize() {

        try {
            clienteService.load(); // 🔥 ahora protegido
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error cargando clientes desde BD");
        }

        setupClientsTable();
        setupSearch();

        if (overlayContainer != null) {
            overlayContainer.setVisible(false);
            overlayContainer.setManaged(false);
        }
    }

    private void setupClientsTable() {

        colName.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getName() + " " + d.getValue().getSurname()
                )
        );

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail())
        );

        colActions.setCellFactory(param -> new TableCell<>() {

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
        });

        filteredClientes = new FilteredList<>(clienteService.getAll());
        clientsTable.setItems(filteredClientes);

        clientsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selected) -> {

                    if (selected == null) return;

                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource(
                                        "/org/example/paneljavafx/cliente-privado-view.fxml"
                                )
                        );

                        Parent view = loader.load();

                        ClientePrivadoViewController controller =
                                loader.getController();

                        controller.loadCliente(selected);

                        clienteDetailContainer.getChildren().setAll(view);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            String filter = (newVal == null) ? "" : newVal.toLowerCase();

            filteredClientes.setPredicate(c -> {

                if (filter.isEmpty()) return true;

                return c.getName().toLowerCase().contains(filter)
                        || c.getSurname().toLowerCase().contains(filter)
                        || c.getEmail().toLowerCase().contains(filter);
            });
        });
    }

    @FXML
    private void openAddCliente() {
        openForm(null);
    }

    private void openEditCliente(Client cliente) {
        openForm(cliente);
    }

    private void openForm(Client cliente) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/add-cliente-view.fxml")
            );

            Parent form = loader.load();

            AddClienteController controller = loader.getController();
            controller.init(cliente, this);

            overlayContainer.getChildren().setAll(form);
            overlayContainer.setVisible(true);
            overlayContainer.setManaged(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshTable() {
        clientsTable.refresh();
    }

    public void closeAddClienteForm() {

        overlayContainer.getChildren().clear();
        overlayContainer.setVisible(false);
        overlayContainer.setManaged(false);
    }
}