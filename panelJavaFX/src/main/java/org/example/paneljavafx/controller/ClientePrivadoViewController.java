package org.example.paneljavafx.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import org.example.paneljavafx.model.*;
import org.example.paneljavafx.service.ClienteService;
import org.example.paneljavafx.service.FundService;
import org.example.paneljavafx.service.GestorService;
import org.example.paneljavafx.viewmodel.TransactionRowView;

import java.util.*;
import java.util.stream.Collectors;

public class ClientePrivadoViewController {

    @FXML private Label labelIdCliente;
    @FXML private Label labelNombre;
    @FXML private Label labelEmail;
    @FXML private Label labelDni;
    @FXML private Label labelPais;
    @FXML private Label labelFechaAlta;

    @FXML private Label labelGestor;
    @FXML private Label labelValorCartera;
    @FXML private Label labelRentabilidad;

    @FXML private TableView<TransactionRowView> inversionesTable;

    @FXML private TableColumn<TransactionRowView, String> colFund;
    @FXML private TableColumn<TransactionRowView, String> colTypeTransaction;
    @FXML private TableColumn<TransactionRowView, String> colAmountTransaction;
    @FXML private TableColumn<TransactionRowView, String> colTotalPosition;

    @FXML private PieChart portfolioPieChart;

    private final ClienteService clienteService = ClienteService.getInstance();
    private final FundService fundService = FundService.getInstance();
    private final GestorService gestorService = GestorService.getInstance();

    private Map<String, String> fundNames;

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

        setupTable();
    }

    // =========================
    // LOAD CLIENTE
    // =========================
    public void loadCliente(Cliente selected) {

        if (selected == null) return;

        // =========================
        // DATOS BÁSICOS
        // =========================
        labelIdCliente.setText(String.valueOf(selected.getIdCliente()));
        labelNombre.setText(selected.getNombre() + " " + selected.getApellido());
        labelEmail.setText(selected.getEmail());
        labelDni.setText(selected.getDni());
        labelPais.setText(selected.getPais());
        labelFechaAlta.setText(String.valueOf(selected.getFechaAlta()));

        // =========================
        // GESTOR
        // =========================
        String nombreGestor = Optional.ofNullable(
                        gestorService.getById(selected.getIdGestor())
                )
                .map(g -> g.getNombre() + " " + g.getApellidos())
                .orElse("Sin gestor");

        labelGestor.setText(nombreGestor);

        // =========================
        // POSICIONES SEGURAS
        // =========================
        List<Posicion> posiciones =
                clienteService.getPosicionesByClienteId(selected.getIdCliente());

        if (posiciones == null) posiciones = List.of();

        List<TransactionRowView> rows = new ArrayList<>();

        Map<String, Double> valueByFund = new HashMap<>();
        double totalCartera = 0;

        for (Posicion p : posiciones) {

            List<Transaccion> transacciones =
                    p.getTransacciones() != null ? p.getTransacciones() : List.of();

            double totalPosition = transacciones.stream()
                    .mapToDouble(Transaccion::getImporte)
                    .sum();

            totalCartera += totalPosition;

            String fundName = fundNames.getOrDefault(
                    p.getIdFondo(),
                    p.getIdFondo()
            );

            valueByFund.merge(fundName, totalPosition, Double::sum);

            for (Transaccion t : transacciones) {

                rows.add(new TransactionRowView(
                        fundName,
                        t.getTipo(),
                        t.getImporte(),
                        totalPosition
                ));
            }
        }

        // =========================
        // TABLA
        // =========================
        inversionesTable.setItems(FXCollections.observableArrayList(rows));

        // =========================
        // PIE CHART
        // =========================
        updatePieChart(valueByFund, totalCartera);

        // =========================
        // KPIs
        // =========================
        labelValorCartera.setText(String.format("€%.2f", totalCartera));

        double invertido = totalCartera * 0.9; // placeholder si no tienes dato real
        double rentabilidad = invertido == 0 ? 0 :
                ((totalCartera - invertido) / invertido) * 100;

        labelRentabilidad.setText(String.format("%.2f%%", rentabilidad));
    }

    // =========================
    // PIE CHART
    // =========================
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

    // =========================
    // TABLE
    // =========================
    private void setupTable() {

        colFund.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getFundName()
                )
        );

        colTypeTransaction.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getType()
                )
        );

        colAmountTransaction.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("€%.2f", d.getValue().getAmount())
                )
        );

        colTotalPosition.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("€%.2f", d.getValue().getTotalPosition())
                )
        );
    }
}