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
    @FXML private TableView<Posicion> posicionesTable;

    @FXML private TableColumn<Posicion, String> colPosFund;
    @FXML private TableColumn<Posicion, Double> colPosCantidad;
    @FXML private TableColumn<Posicion, Double> colPosValor;
    @FXML private TableColumn<Posicion, Double> colPosTotal;

    @FXML private TableView<TransactionRowView> inversionesTable;

    @FXML private TableColumn<TransactionRowView, String> colFund;
    @FXML private TableColumn<TransactionRowView, String> colTypeTransaction;
    @FXML private TableColumn<TransactionRowView, Double> colAmountTransaction;

    @FXML private PieChart portfolioPieChart;

    @FXML private StackPane overlayContainer;

    // ========================= SERVICES =========================
    private final ClienteService clienteService = ClienteService.getInstance();
    private final FundService fundService = FundService.getInstance();
    private final GestorService gestorService = GestorService.getInstance();

    private Map<String, String> fundNames;
    private Cliente currentCliente;

    // ========================= INIT =========================
    @FXML
    public void initialize() {

        fundService.load();
        gestorService.load();

        fundNames = fundService.getAll().stream()
                .collect(Collectors.toMap(
                        Fund::getIdFondo,
                        Fund::getNombre,
                        (a, b) -> a
                ));

        setupPosicionesTable();
        setupTransaccionesTable();
        setupPosicionSelectionListener();

        overlayContainer.setVisible(false);
        overlayContainer.setManaged(false);
    }

    // ========================= LOAD CLIENTE =========================
    public void loadCliente(Cliente selected) {

        if (selected == null) return;

        this.currentCliente = selected;

        labelIdCliente.setText(String.valueOf(selected.getIdCliente()));
        labelNombre.setText(selected.getNombre() + " " + selected.getApellido());
        labelEmail.setText(selected.getEmail());
        labelDni.setText(selected.getDni());
        labelPais.setText(selected.getPais());
        labelFechaAlta.setText(String.valueOf(selected.getFechaAlta()));

        String gestor = Optional.ofNullable(
                        gestorService.getById(selected.getIdGestor())
                ).map(g -> g.getNombre() + " " + g.getApellidos())
                .orElse("Sin gestor");

        labelGestor.setText(gestor);

        refreshData(selected);
    }

    // ========================= REFRESH =========================
    private void refreshData(Cliente selected) {

        List<Posicion> posiciones =
                clienteService.getPosicionesByClienteId(selected.getIdCliente());

        posicionesTable.setItems(FXCollections.observableArrayList(posiciones));

        // ========================= CARTERA =========================
        Map<String, Double> valueByFund = new HashMap<>();
        double totalCartera = 0;

        for (Posicion p : posiciones) {

            double totalPosition = p.getValorActual();
            totalCartera += totalPosition;

            String fundName = fundNames.getOrDefault(p.getIdFondo(), p.getIdFondo());

            valueByFund.merge(fundName, totalPosition, Double::sum);
        }

        updatePieChart(valueByFund, totalCartera);

        labelValorCartera.setText(String.format("€%.2f", totalCartera));

        double invertido = totalCartera * 0.9;
        double rentabilidad = invertido == 0 ? 0 :
                ((totalCartera - invertido) / invertido) * 100;

        labelRentabilidad.setText(String.format("%.2f%%", rentabilidad));

        inversionesTable.setItems(FXCollections.observableArrayList());
    }

    // ========================= SELECCIÓN =========================
    private void setupPosicionSelectionListener() {

        posicionesTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selected) -> {

                    if (selected == null) {
                        inversionesTable.getItems().clear();
                        return;
                    }

                    loadTransacciones(selected);
                });
    }

    private void loadTransacciones(Posicion p) {

        String fundName = fundNames.getOrDefault(p.getIdFondo(), p.getIdFondo());

        // 🔥 SIEMPRE desde BD (no cache)
        List<Transaccion> transacciones =
                TransactionService.getInstance().getTransacciones(p);

        List<TransactionRowView> rows = new ArrayList<>();

        for (Transaccion t : transacciones) {

            rows.add(new TransactionRowView(
                    fundName,
                    t.getTipo(),
                    t.getImporte(),
                    p.getValorActual()
            ));
        }

        inversionesTable.setItems(FXCollections.observableArrayList(rows));
    }

    // ========================= TABLA POSICIONES =========================
    private void setupPosicionesTable() {

        colPosFund.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        fundNames.getOrDefault(d.getValue().getIdFondo(), d.getValue().getIdFondo())
                )
        );

        colPosCantidad.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getCantidad())
        );

        colPosValor.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getValorActual())
        );

        colPosTotal.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getValorActual())
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
            controller.setCliente(currentCliente);
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

        if (currentCliente == null) return;

        // 🔥 recarga todo desde BD (importante)
        clienteService.load();

        currentCliente = clienteService.getById(currentCliente.getIdCliente());

        loadCliente(currentCliente);
    }
}