package org.example.paneljavafx.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import org.example.paneljavafx.common.TabDataReceiver;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.service.*;
import org.example.paneljavafx.simulation.MarketClock;

import java.text.DecimalFormat;
import java.time.YearMonth;
import java.util.*;

public class FundViewController implements TabDataReceiver<Fund> {

    // ========================= TABLE MODEL =========================
    public static class AssetRow {

        private final SimpleStringProperty nombre;
        private final SimpleDoubleProperty peso;
        private final SimpleDoubleProperty retorno;

        public AssetRow(String nombre, double peso, double retorno) {
            this.nombre = new SimpleStringProperty(nombre);
            this.peso = new SimpleDoubleProperty(peso);
            this.retorno = new SimpleDoubleProperty(retorno);
        }

        public String getNombre() { return nombre.get(); }
        public double getPeso() { return peso.get(); }
        public double getRetorno() { return retorno.get(); }

        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleDoubleProperty pesoProperty() { return peso; }
        public SimpleDoubleProperty retornoProperty() { return retorno; }
    }

    // ========================= FXML =========================
    @FXML private Label fundName, fundTicker, fundIsin, fundType, fundCategory;
    @FXML private Label fundValue;
    private boolean positionsChanged = true;

    @FXML private TableView<AssetRow> topAssetsTable;
    @FXML private TableColumn<AssetRow, String> colNombre;
    @FXML private TableColumn<AssetRow, Number> colPeso;
    @FXML private TableColumn<AssetRow, Number> colRetorno;

    @FXML private PieChart fundPieChart;
    @FXML private BarChart<String, Number> monthlyReturnChart;

    // ========================= STATE =========================
    private Fund currentFund;
    private List<FundAssetPosition> fundPositions = new ArrayList<>();

    private final ObservableList<AssetRow> tableData = FXCollections.observableArrayList();
    private final DecimalFormat DF = new DecimalFormat("#,###.##");

    private final FundService fundService = FundService.getInstance();
    private final CandleService candleService = CandleService.getInstance();

    private Runnable tickListener;

    // ========================= INIT =========================
    @Override
    public void loadData(Fund fund) {
        this.currentFund = fund;

        setupTable();
        renderStaticInfo();
        bindMarket();
        recalculate();

        recalculate();
    }

    public void loadPositions(List<FundAssetPosition> positions) {
        this.fundPositions = (positions != null) ? positions : new ArrayList<>();

        positionsChanged = true;

        recalculate();
        refreshHeavyCharts();
        recalculate();

    }

    // ========================= TABLE =========================
    private void setupTable() {

        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colPeso.setCellValueFactory(c -> c.getValue().pesoProperty());
        colRetorno.setCellValueFactory(c -> c.getValue().retornoProperty());

        colPeso.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("%.2f%%", value.doubleValue()));
            }
        });

        colRetorno.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    double v = value.doubleValue();
                    setText(String.format("%+.2f%%", v));
                    setTextFill(v >= 0 ? Color.web("#00D09C") : Color.web("#FF5C7A"));
                }
            }
        });

        topAssetsTable.setItems(tableData);
    }

    // ========================= CORE UPDATE =========================
    private void recalculate() {

        if (currentFund == null) return;

        List<FundService.PositionSummary> summary =
                fundService.getSummaryByFund(fundPositions);

        double totalValue = summary.stream()
                .mapToDouble(FundService.PositionSummary::valorActual)
                .sum();

        fundValueHistory.add(totalValue);

        Platform.runLater(() -> {
            updateUI(summary, totalValue);
        });
    }

    // ========================= UI =========================
    private void updateUI(List<FundService.PositionSummary> summary, double totalValue) {

        fundValue.setText(DF.format(totalValue) + " €");
        refreshTable(summary);
    }

    // ========================= TABLE DATA =========================
    private void refreshTable(List<FundService.PositionSummary> summary) {

        tableData.clear();

        double total = summary.stream()
                .mapToDouble(FundService.PositionSummary::valorActual)
                .sum();


        for (FundService.PositionSummary s : summary) {

            double weight = total > 0
                    ? (s.valorActual() / total) * 100
                    : 0;

            tableData.add(new AssetRow(
                    s.idAsset(),
                    weight,
                    s.returnPct()
            ));
        }
    }

    private void refreshHeavyCharts() {

        if (!positionsChanged) return;

        Platform.runLater(() -> {
            renderPieChart();
            render6MonthBarChart();
        });

        positionsChanged = false;
    }

    // ========================= PIE CHART =========================
    private void renderPieChart() {

        if (fundPieChart == null) return;

        List<FundService.PositionSummary> summary =
                fundService.getSummaryByFund(fundPositions);

        double total = summary.stream()
                .mapToDouble(FundService.PositionSummary::valorActual)
                .sum();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        for (FundService.PositionSummary s : summary) {

            double weight = total > 0
                    ? (s.valorActual() / total) * 100
                    : 0;

            data.add(new PieChart.Data(
                    s.idAsset(),
                    s.valorActual()
            ));
        }

        fundPieChart.setData(data);
    }

    // ========================= BAR CHART (6M) =========================
    private void render6MonthBarChart() {

        if (currentFund == null || monthlyReturnChart == null) return;

        Map<YearMonth, Double> data =
                candleService.rentabilidadUltimos6MesesFondo(currentFund);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("6M Return");

        for (Map.Entry<YearMonth, Double> e : data.entrySet()) {

            String label = e.getKey().getMonth().toString().substring(0, 3);

            series.getData().add(
                    new XYChart.Data<>(label, e.getValue())
            );
        }

        monthlyReturnChart.getData().clear();
        monthlyReturnChart.getData().add(series);
    }

    // ========================= NAV CHART =========================
    private final List<Double> fundValueHistory = new ArrayList<>();


    // ========================= STATIC INFO =========================
    private void renderStaticInfo() {

        if (currentFund == null) return;

        fundName.setText(currentFund.getName());
        fundTicker.setText(currentFund.getFundId());
        fundIsin.setText(currentFund.getIsinCode() != null
                ? currentFund.getIsinCode() : "N/A");
        fundType.setText(currentFund.getType());
        fundCategory.setText(currentFund.getCategory());
    }

    // ========================= MARKET BIND =========================
    private void bindMarket() {
        tickListener = this::recalculate;
        MarketClock.getInstance().addListener(tickListener);
    }

    public void onClose() {
        if (tickListener != null) {
            MarketClock.getInstance().removeListener(tickListener);
        }
    }
}