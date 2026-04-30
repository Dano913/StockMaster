package org.example.paneljavafx.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import org.example.paneljavafx.common.TabDataReceiver;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.service.FundService;
import org.example.paneljavafx.service.dto.FundMetrics;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.service.ChartService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FundViewController implements TabDataReceiver<Fund> {

    // =========================
    // MODELO INTERNO TABLA
    // =========================
    public static class AssetRow {

        private final SimpleStringProperty nombre;
        private final SimpleDoubleProperty porcentaje;

        public AssetRow(String nombre, double porcentaje) {
            this.nombre     = new SimpleStringProperty(nombre);
            this.porcentaje = new SimpleDoubleProperty(porcentaje);
        }

        public SimpleStringProperty nombreProperty()     { return nombre; }
        public SimpleDoubleProperty porcentajeProperty() { return porcentaje; }

        public String getNombre()     { return nombre.get(); }
        public double getPorcentaje() { return porcentaje.get(); }
    }

    // =========================
    // FXML - INFO PANEL
    // =========================
    @FXML private Label fundName, fundTicker, fundIsin, fundType, fundCategory;
    @FXML private Label fundValue, fundChange, fundDailyReturn, totalAUM, exposureRisk;

    // =========================
    // FXML - TABLA POSICIONES
    // =========================
    @FXML private TableView<AssetRow>           topAssetsTable;
    @FXML private TableColumn<AssetRow, String> colNombre;
    @FXML private TableColumn<AssetRow, Number> colPorcentaje;

    // =========================
    // FXML - CHART
    // =========================
    @FXML private Canvas   fundChartCanvas;
    @FXML private PieChart fundPieChart;

    // =========================
    // STATE
    // =========================
    private Fund currentFund;
    private List<FundAssetPosition> fundPositions = new ArrayList<>();

    private Runnable tickListener;
    private double previousFundValue = 0;

    private final DecimalFormat DF = new DecimalFormat("#,###.##");
    private final ObservableList<AssetRow> tableData = FXCollections.observableArrayList();

    FundService fundService = FundService.getInstance();
    private final ChartService chartService = new ChartService();

    private final List<Double> fundValueHistory = new ArrayList<>();

    // =========================
    // ENTRY POINT
    // =========================
    @Override
    public void loadData(Fund fund) {
        this.currentFund = fund;
        renderStaticInfo();
        setupTable();
        bindMarket();
        recalculate();

    }

    // =========================
    // POSITIONS
    // =========================
    public void loadPositions(List<FundAssetPosition> positions) {
        this.fundPositions = (positions != null) ? positions : new ArrayList<>();
        recalculate();
        renderFundPieChart();
    }

    // =========================
    // TABLA — SETUP (se llama una sola vez)
    // =========================
    private void setupTable() {
        if (topAssetsTable == null) return;

        // Columna Nombre — bind a la property
        colNombre.setCellValueFactory(
                cell -> cell.getValue().nombreProperty()
        );

        // Columna Porcentaje — bind a la property + cell factory con color
        colPorcentaje.setCellValueFactory(
                cell -> cell.getValue().porcentajeProperty()
        );

        colPorcentaje.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                    setStyle("");
                } else {
                    double pct = value.doubleValue();
                    setText(String.format("%.2f%%", pct));
                    setStyle(pct >= 0
                            ? "-fx-text-fill: #00D09C; -fx-font-weight: bold;"
                            : "-fx-text-fill: #FF5C7A; -fx-font-weight: bold;");
                }
            }
        });

        topAssetsTable.setItems(tableData);
        topAssetsTable.setPlaceholder(new Label("Sin posiciones"));
    }

    // =========================
    // TABLA — REFRESCO
    // Usa FundService.getSummaryByFund para obtener valor real de cada posición
    // en lugar de parsear los strings de topMovers
    // =========================
    private void refreshTable(List<String> topMovers) {
        if (topAssetsTable == null) return;

        tableData.clear();

        // getSummaryByFund devuelve TODAS las posiciones ordenadas por valor,
        // con el peso real calculado sobre el NAV total del fondo
        List<FundService.PositionSummary> summaries =
                fundService.getSummaryByFund(fundPositions);

        summaries.forEach(s ->
                tableData.add(new AssetRow(s.idAsset(), s.returnPct()))
        );
    }

    // =========================
    // MARKET
    // =========================
    private void bindMarket() {
        tickListener = this::recalculate;
        MarketClock.getInstance().addListener(tickListener);
    }

    // =========================
    // CORE LOOP
    // =========================
    private void recalculate() {
        if (currentFund == null) return;

        FundMetrics metrics = fundService.calculateMetrics(
                currentFund,
                fundPositions,
                previousFundValue
        );

        previousFundValue = metrics.getTotalValue();
        fundValueHistory.add(metrics.getTotalValue());

        Platform.runLater(() -> {
            updateUI(metrics);
            drawChart();
        });
    }

    // =========================
    // UI UPDATE
    // =========================
    private void updateUI(FundMetrics m) {
        if (fundValue == null || fundChange == null) return;

        // NAV total real (todas las posiciones, no solo top 5)
        double navTotal = fundService.calculateTotalNAV(fundPositions);

        fundValue.setText(DF.format(navTotal) + " €");

        fundChange.setText(String.format("%+.2f%%", m.getChangePct()));
        fundChange.setTextFill(m.getChangePct() >= 0
                ? Color.web("#00D09C")
                : Color.web("#FF5C7A"));

        if (fundDailyReturn != null) {
            fundDailyReturn.setText(String.format("(%+.0f € hoy)", m.getTotalChange()));
            fundDailyReturn.setTextFill(m.getTotalChange() >= 0
                    ? Color.web("#00D09C")
                    : Color.web("#FF5C7A"));
        }

        if (totalAUM != null) {
            totalAUM.setText(DF.format(navTotal) + " AUM");
        }

        // Log del valor total del fondo en cada tick
        System.out.printf("💰 [%s] NAV total: %s € | Cambio: %+.2f%% | Δ: %+.0f €%n",
                currentFund != null ? currentFund.getFundId() : "?",
                DF.format(navTotal),
                m.getChangePct(),
                m.getTotalChange()
        );

        refreshTable(m.getTopMovers());
    }

    // =========================
    // STATIC INFO
    // =========================
    private void renderStaticInfo() {
        if (currentFund == null) return;

        fundName.setText(currentFund.getName());
        fundTicker.setText(currentFund.getFundId());
        fundIsin.setText(currentFund.getIsinCode() != null
                ? currentFund.getIsinCode() : "N/A");
        fundType.setText(currentFund.getType());
        fundCategory.setText(currentFund.getCategory());

        if (exposureRisk != null) exposureRisk.setText("Riesgo: --");
    }

    // =========================
    // CHART RENDER
    // =========================
    private void drawChart() {
        if (fundChartCanvas == null) return;
        if (fundChartCanvas.getWidth() == 0) return;
        if (fundValueHistory.size() < 2) return;

        GraphicsContext g = fundChartCanvas.getGraphicsContext2D();
        g.clearRect(0, 0, fundChartCanvas.getWidth(), fundChartCanvas.getHeight());

        chartService.drawFundEquity(
                g,
                fundValueHistory,
                fundChartCanvas.getWidth(),
                fundChartCanvas.getHeight()
        );
    }

    // =========================
    // PIE CHART
    // =========================
    private void renderFundPieChart() {
        if (currentFund == null || fundPositions == null) return;
        if (fundPieChart == null) return;

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        fundPositions.forEach(p ->
                pieData.add(new PieChart.Data(p.getIdAsset(), p.getInvestedValue()))
        );
        fundPieChart.setData(pieData);
    }

    // =========================
    // LIFECYCLE
    // =========================
    public void onClose() {
        if (tickListener != null) {
            MarketClock.getInstance().removeListener(tickListener);
            tickListener = null;
        }
    }
}