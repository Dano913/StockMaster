package org.example.paneljavafx.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.example.paneljavafx.helper.AlertUtils;
import org.example.paneljavafx.helper.FormatUtils;
import org.example.paneljavafx.helper.TransactionMapper;
import org.example.paneljavafx.model.*;
import org.example.paneljavafx.service.*;
import org.example.paneljavafx.service.dto.PortfolioSummary;
import org.example.paneljavafx.viewmodel.TransactionRowView;

import java.util.*;
import java.util.stream.Collectors;

public class ClienteViewController {

    // ========================= LABELS DATOS =========================
    @FXML private Label labelIdCliente;
    @FXML private Label labelNombre;
    @FXML private Label labelEmail;
    @FXML private Label labelDni;
    @FXML private Label labelPais;
    @FXML private Label labelFechaAlta;

    // ========================= LABELS INFO ADICIONAL =========================
    @FXML private Label labelGestor;
    @FXML private Label labelValorCartera;
    @FXML private Label labelRentabilidad;

    // ========================= TABLA POSICION =========================
    @FXML private TableView<ClientFundPosition> posicionesTable;

    @FXML private TableColumn<ClientFundPosition, String> colPosFund;
    @FXML private TableColumn<ClientFundPosition, Double> colPosCantidad;
    @FXML private TableColumn<ClientFundPosition, Double> colPosValor;
    @FXML private TableColumn<ClientFundPosition, Double> colPosTotal;

    // ========================= TABLA TRANSACCION =========================
    @FXML private TableView<TransactionRowView> inversionesTable;

    @FXML private TableColumn<TransactionRowView, String> colFund;
    @FXML private TableColumn<TransactionRowView, String> colTypeTransaction;
    @FXML private TableColumn<TransactionRowView, Double> colAmountTransaction;
    @FXML private TableColumn<TransactionRowView, String> colFecha;
    @FXML private TableColumn<TransactionRowView, Void> colAcciones;


    // ========================= ELEMENTOS =========================
    @FXML private PieChart portfolioPieChart;
    @FXML private StackPane overlayContainer;
    @FXML private Button btnVenderPosicion;
    private Map<String, String> fundNames;

    // ========================= SERVICES =========================
    private final ClientService clientService = ClientService.getInstance();
    private final FundService fundService = FundService.getInstance();
    private final GestorService gestorService = GestorService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();
    private final ClientFundPositionService clientfundpositionService = ClientFundPositionService.getInstance();

    // ========================= CURRENT CLIENT =========================
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

        loadLoggedClient();
    }

    // ========================= ENTRY POINTS =========================

    public void setCliente(Client client) {
        this.currentClient = client;
        initClient();
    }

    private void loadLoggedClient() {

        currentClient = clientService.getLoggedClient().orElse(null);

        if (currentClient == null) return;

        initClient();
    }

    // ========================= SINGLE FLOW =========================
    private void initClient() {
        bindClientToView();
        refreshData();
    }

    // ========================= BIND =========================
    private void bindClientToView() {

        labelIdCliente.setText(String.valueOf(currentClient.getClientId()));
        labelNombre.setText(currentClient.getName() + " " + currentClient.getSurname());
        labelEmail.setText(currentClient.getEmail());
        labelDni.setText(currentClient.getNationalId());
        labelPais.setText(currentClient.getCountry());
        labelFechaAlta.setText(String.valueOf(currentClient.getJoinDate()));
        labelGestor.setText(gestorService.getGestorFullName(String.valueOf(currentClient.getGestorId())));
    }

    // ========================= REFRESH =========================
    private void refreshData() {

        if (currentClient == null) return;

        List<ClientFundPosition> posiciones =
                clientfundpositionService.getByClientId(currentClient.getClientId());

        posicionesTable.setItems(FXCollections.observableArrayList(posiciones));

        PortfolioSummary summary =
                clientfundpositionService.calculatePortfolio(posiciones);

        labelValorCartera.setText(FormatUtils.euro(summary.getTotal()));
        labelRentabilidad.setText(FormatUtils.percent(summary.getRentabilidad()));

        inversionesTable.setItems(FXCollections.observableArrayList());

        portfolioPieChart.setData(
                FXCollections.observableArrayList(
                        clientfundpositionService.buildPortfolioChart(posiciones, fundNames)
                )
        );

        portfolioPieChart.setLabelsVisible(true);
        portfolioPieChart.setLegendVisible(true);
    }

    public void refresh() {
        refreshData();
    }

    // ========================= POSITIONS =========================
    private void setupPosicionSelectionListener() {

        posicionesTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selected) -> {

                    if (selected == null) {
                        inversionesTable.getItems().clear();
                        btnVenderPosicion.setDisable(true);
                        return;
                    }

                    btnVenderPosicion.setDisable(false);
                    loadTransactions(selected);
                });

        btnVenderPosicion.setDisable(true);
    }

    // ========================= TRANSACTIONS =========================
    private void loadTransactions(ClientFundPosition position) {

        if (position == null) return;

        Integer positionId = position.getPositionId();

        if (positionId == null) {
            System.out.println("PositionId es null → no se pueden cargar transacciones");
            inversionesTable.getItems().clear();
            return;
        }

        List<Transaction> tx =
                transactionService.getTransactions(position);

        if (tx == null || tx.isEmpty()) {
            inversionesTable.getItems().clear();
            return;
        }

        String fundName = fundService.getFundName(position.getFundId());

        List<TransactionRowView> rows =
                TransactionMapper.toRowView(
                        tx,
                        fundName != null ? fundName : position.getFundId(),
                        position.getActualValue()
                );

        inversionesTable.setItems(FXCollections.observableArrayList(rows));
    }

    // ========================= SELL POSITION =========================
    @FXML
    private void venderPosicion() {

        ClientFundPosition selected =
                posicionesTable.getSelectionModel().getSelectedItem();

        if (selected == null) return;

        boolean confirm = AlertUtils.confirm(
                "Confirmar venta",
                "¿Liquidar esta posición?"
        );

        if (!confirm) return;

        clientfundpositionService.sellPosition(selected);
        refreshData();
    }

    // ========================= TABLAS =========================
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

        colFecha.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getFecha())
        );

        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnLiquidar = new Button("🗑 Liquidar");

            {
                btnLiquidar.setOnAction(e -> {

                    TransactionRowView row = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmar");
                    confirm.setHeaderText("¿Liquidar esta transacción?");
                    confirm.setContentText("Importe: " + String.format("€%.2f", row.getAmount()));

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            transactionService.deleteById(row.getTransactionId());

                            ClientFundPosition selected =
                                    posicionesTable.getSelectionModel().getSelectedItem();
                            loadTransactions(selected);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnLiquidar);
            }
        });
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

    public void closeOverlay() {

        overlayContainer.getChildren().clear();
        overlayContainer.setVisible(false);
        overlayContainer.setManaged(false);
    }
}