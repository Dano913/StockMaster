package org.example.paneljavafx.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.example.paneljavafx.common.TabDataReceiver;
import org.example.paneljavafx.data.DataStore;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.service.AssetService;
import org.example.paneljavafx.service.dto.AssetMetrics;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetViewController implements TabDataReceiver<Asset> {

    // =========================
    // FXML
    // =========================
    @FXML private Label assetName;
    @FXML private Label assetTicker;
    @FXML private Label assetPrice;
    @FXML private Label assetRisk;
    @FXML private Label assetLiquidity;
    @FXML private Label assetMarketCap;
    @FXML private Label assetType;
    @FXML private Label assetSector;

    @FXML private TableView<Map<String, String>> tabla_exposicion;
    @FXML private TableColumn<Map<String, String>, String> colFund;
    @FXML private TableColumn<Map<String, String>, String> colValue;
    @FXML private Canvas chartCanvas;
    @FXML
    private VBox assetPieChart;

    // =========================
    // STATE
    // =========================
    private Asset currentAsset;
    private List<FundPosition> cachedPositions;

    private Runnable tickListener;
    private ChartController chartController;

    private final AssetService assetService = new AssetService();

    // =========================
    // ENTRY POINT
    // =========================
    @Override
    public void loadData(Asset asset) {
        this.currentAsset = asset;

        colFund.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().get("fund"))
        );

        colValue.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().get("value"))
        );

        bindMarket();
        renderExposure();
        recalculate();
    }

    // =========================
    // POSITIONS
    // =========================
    public void loadPositions(List<FundPosition> positions) {
        this.cachedPositions = positions;
        renderExposure();
        renderExposurePieChart();
    }

    // =========================
    // MARKET
    // =========================
    private void bindMarket() {

        MarketEngine engine = DataStore.engines.get(currentAsset.getId());

        if (engine != null && chartCanvas != null) {
            chartController = new ChartController(chartCanvas, List.of(engine));
        }

        tickListener = this::recalculate;
        MarketClock.getInstance().addListener(tickListener);

        recalculate();
    }

    // =========================
    // REACTIVE PRICE UPDATE
    // =========================
    private void recalculate() {

        if (currentAsset == null) return;
        if (cachedPositions == null) return;

        AssetMetrics metrics = assetService.calculateMetrics(
                cachedPositions,
                currentAsset.getId()
        );

        Platform.runLater(() -> updateUI(metrics));
    }

    // =========================
    // UI (DINÁMICO)
    // =========================
    private void updateUI(AssetMetrics m) {

        if (currentAsset == null) return;

        assetName.setText(currentAsset.getName());
        assetTicker.setText(currentAsset.getTicker());

        assetMarketCap.setText(currentAsset.getMarketCap() + " €");
        assetType.setText(currentAsset.getType());
        assetSector.setText(currentAsset.getSector());

        assetRisk.setText(currentAsset.getRisk());
        assetLiquidity.setText(currentAsset.getLiquidity());

        MarketEngine engine = DataStore.engines.get(currentAsset.getId());
        double price = (engine != null) ? engine.getLastPrice() : 0.0;
        assetPrice.setText(String.format("%.2f €", price));
    }

    // =========================
    // 🔥 EXPOSICIÓN (ESTÁTICA)
    // =========================
    private void renderExposure() {

        if (currentAsset == null || cachedPositions == null) return;

        tabla_exposicion.getItems().clear();

        tabla_exposicion.setStyle("""
            -fx-background-color: transparent;
            -fx-control-inner-background: transparent;
            -fx-table-cell-border-color: transparent;
        """);

        ObservableList<Map<String, String>> items = FXCollections.observableArrayList();

        cachedPositions.stream()
                .filter(p -> p.getIdAsset().equals(currentAsset.getId()))
                .forEach(p -> {

                    Fund fund = null;

                    // 🔥 MISMA LÓGICA QUE TU FUNCIÓN QUE FUNCIONA
                    for (Fund f : DataStore.funds) {
                        if (f.getIdFondo().equals(p.getIdFund())) {
                            fund = f;
                            break;
                        }
                    }

                    String fundName = (fund != null)
                            ? fund.getNombre()
                            : p.getIdFund();

                    Map<String, String> row = new HashMap<>();
                    row.put("fund", fundName);
                    row.put("value", String.format("%.2f €", p.getInvestedValue()));

                    items.add(row);
                });

        tabla_exposicion.setItems(items);
    }

    private void renderExposurePieChart() {

        if (currentAsset == null || cachedPositions == null) return;
        if (assetPieChart == null) return;

        assetPieChart.getChildren().clear();

        PieChart pieChart = new PieChart();
        pieChart.setLabelsVisible(false); // ✅ quita labels y líneas
        pieChart.setLegendVisible(true);  // ✅ mantiene la leyenda de abajo

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        cachedPositions.stream()
                .filter(p -> p.getIdAsset().equals(currentAsset.getId()))
                .forEach(p -> {

                    Fund fund = null;
                    for (Fund f : DataStore.funds) {
                        if (f.getIdFondo().equals(p.getIdFund())) {
                            fund = f;
                            break;
                        }
                    }

                    String name = (fund != null) ? fund.getNombre() : p.getIdFund();
                    pieData.add(new PieChart.Data(name, p.getInvestedValue()));
                });

        pieChart.setData(pieData);
        assetPieChart.getChildren().add(pieChart);
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