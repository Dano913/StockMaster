package org.example.paneljavafx.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.example.paneljavafx.model.*;
import org.example.paneljavafx.service.*;
import org.example.paneljavafx.viewmodel.TransactionRowView;

import java.util.*;
import java.util.stream.Collectors;

public class ClientePrivadoViewController {

    // ========================= CLIENTE =========================
    @FXML private Label labelIdCliente;
    @FXML private Label labelNombre;
    @FXML private Label labelEmail;
    @FXML private Label labelDni;
    @FXML private Label labelPais;
    @FXML private Label labelFechaAlta;

    @FXML private Label labelGestor;
    @FXML private Label labelValorCartera;
    @FXML private Label labelRentabilidad;

    // ========================= TABLAS =========================
    @FXML private TableView<ClientFundPosition> posicionesTable;

    @FXML private TableColumn<ClientFundPosition, String> colPosFund;
    @FXML private TableColumn<ClientFundPosition, Double> colPosCantidad;
    @FXML private TableColumn<ClientFundPosition, Double> colPosValor;
    @FXML private TableColumn<ClientFundPosition, Double> colPosTotal;

    @FXML private TableView<TransactionRowView> inversionesTable;

    @FXML private TableColumn<TransactionRowView, String> colFund;
    @FXML private TableColumn<TransactionRowView, String> colTypeTransaction;
    @FXML private TableColumn<TransactionRowView, Double> colAmountTransaction;

    @FXML private PieChart portfolioPieChart;

    @FXML private StackPane overlayContainer;

    // ========================= SERVICES =========================
    private final ClientService clientService = ClientService.getInstance();
    private final FundService fundService = FundService.getInstance();
    private final GestorService gestorService = GestorService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();

    private Map<String, String> fundNames;
    private Client currentClient;

    // ========================= INIT =========================
    @FXML
    public void initialize() {

        fundService.load();
        gestorService.load();

        fundNames = fundService.getAll().stream()
                .collect(Collectors.toMap(
                        Fund::getFundId,
                        Fund::getName,
                        (a, b) -> a
                ));

        setupPosicionesTable();
        setupTransaccionesTable();
        setupPosicionSelectionListener();

        overlayContainer.setVisible(false);
        overlayContainer.setManaged(false);
    }

    // ========================= LOAD CLIENTE =========================
    public void loadCliente(Client selected) {

        if (selected == null) return;

        this.currentClient = selected;

        labelIdCliente.setText(String.valueOf(selected.getClientId()));
        labelNombre.setText(selected.getName() + " " + selected.getSurname());
        labelEmail.setText(selected.getEmail());
        labelDni.setText(selected.getNationalId());
        labelPais.setText(selected.getCountry());
        labelFechaAlta.setText(String.valueOf(selected.getJoinDate()));

        String gestor = gestorService.getById(selected.getGestorId())
                .map(g -> g.getName() + " " + g.getSurname())
                .orElse("Sin gestor");

        labelGestor.setText(gestor);

        refreshData(selected);
    }

    // ========================= REFRESH =========================
    private void refreshData(Client selected) {

        List<ClientFundPosition> posiciones =
                clientService.getPositionsByClientId(selected.getClientId());

        posicionesTable.setItems(FXCollections.observableArrayList(posiciones));

        double totalCartera = posiciones.stream()
                .mapToDouble(ClientFundPosition::getActualValue)
                .sum();

        labelValorCartera.setText(String.format("€%.2f", totalCartera));

        double invertido = totalCartera * 0.9;
        double rentabilidad = invertido == 0 ? 0 :
                ((totalCartera - invertido) / invertido) * 100;

        labelRentabilidad.setText(String.format("%.2f%%", rentabilidad));

        inversionesTable.setItems(FXCollections.observableArrayList());
    }

    // ========================= POSICIONES =========================
    private void setupPosicionSelectionListener() {

        posicionesTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selected) -> {

                    if (selected == null) {
                        inversionesTable.getItems().clear();
                        return;
                    }

                    loadTransactions(selected);
                });
    }

    private void loadTransactions(ClientFundPosition position) {

        if (position == null || position.getPositionId() == null) return;

        String fundName = fundNames.getOrDefault(
                position.getFundId(),
                position.getFundId()
        );

        List<Transaction> transacciones =
                transactionService.getTransactions(position);

        List<TransactionRowView> rows = new ArrayList<>();

        for (Transaction transaction : transacciones) {

            rows.add(new TransactionRowView(
                    fundName,
                    transaction.getType(),
                    transaction.getAmount(),
                    position.getActualValue()
            ));
        }

        inversionesTable.setItems(FXCollections.observableArrayList(rows));
    }

    // ========================= TABLA POSICIONES =========================
    private void setupPosicionesTable() {

        colPosFund.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        fundNames.getOrDefault(d.getValue().getFundId(), d.getValue().getFundId())
                )
        );

        colPosCantidad.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getQuantity())
        );

        colPosValor.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getActualValue())
        );

        colPosTotal.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getActualValue())
        );
    }

    // ========================= TABLA TRANSACCIONES =========================
    private void setupTransaccionesTable() {

        colFund.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getFundName())
        );

        colTypeTransaction.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getType())
        );

        colAmountTransaction.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getAmount())
        );

        colAmountTransaction.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText((empty || value == null) ? null : String.format("€%.2f", value));
            }
        });
    }

    // ========================= PIE CHART =========================
    private void updatePieChart(Map<String, Double> valueByFund, double total) {

        if (valueByFund == null || valueByFund.isEmpty()) {
            portfolioPieChart.setData(FXCollections.observableArrayList());
            return;
        }

        List<PieChart.Data> data = valueByFund.entrySet().stream()
                .map(e -> {
                    double percent = total == 0 ? 0 : (e.getValue() / total) * 100;
                    return new PieChart.Data(
                            e.getKey() + String.format(" (%.1f%%)", percent),
                            e.getValue()
                    );
                })
                .toList();

        portfolioPieChart.setData(FXCollections.observableArrayList(data));
    }

    // ========================= OVERLAY =========================
    @FXML
    private void openAddPosicion() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/add-position-view.fxml")
            );

            Parent form = loader.load();

            AddPositionController controller = loader.getController();
            controller.setParent(this);
            controller.setCliente(currentClient);
            controller.setFunds(fundService.getAll());
            controller.initCreate();

            overlayContainer.getChildren().setAll(form);
            overlayContainer.setVisible(true);
            overlayContainer.setManaged(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========================= CLOSE OVERLAY =========================
    public void closeOverlay() {

        overlayContainer.getChildren().clear();
        overlayContainer.setVisible(false);
        overlayContainer.setManaged(false);
    }

    public void reloadCliente() {

        if (currentClient == null) return;

        clientService.load();
        currentClient = clientService.getById(currentClient.getClientId());

        loadCliente(currentClient);
    }
}