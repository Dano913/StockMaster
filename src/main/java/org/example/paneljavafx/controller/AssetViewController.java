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
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.service.AssetService;
import org.example.paneljavafx.service.FundService;
import org.example.paneljavafx.service.MarketService;
import org.example.paneljavafx.service.dto.AssetMetrics;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetViewController implements TabDataReceiver<Asset> {

    // =========================
    // SERVICES
    // =========================
    private final AssetService assetService = AssetService.getInstance();
    private final FundService fundService = FundService.getInstance();
    private final MarketService marketService = MarketService.getInstance();

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
    @FXML private VBox assetPieChart;

    // =========================
    // STATE
    // =========================
    private Asset currentAsset;
    private List<FundAssetPosition> cachedPositions;

    private Runnable tickListener;
    private ChartController chartController;

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

        // ← ahora currentAsset ya está seteado, renderizamos
        renderExposure();
        renderExposurePieChart();
        recalculate();
    }

    // =========================
    // POSITIONS
    // =========================
    public void loadPositions(List<FundAssetPosition> positions) {
        this.cachedPositions = positions;

        if (currentAsset != null) {      // ← guardia
            renderExposure();
            renderExposurePieChart();
        }
    }

    // =========================
    // MARKET + CHART
    // =========================
    private void bindMarket() {

        if (currentAsset == null) return;

        MarketEngine engine = marketService.getEngine(currentAsset.getId());

        if (engine != null && chartCanvas != null) {
            chartController = new ChartController(
                    chartCanvas,
                    List.of(engine)
            );
        }

        tickListener = this::recalculate;
        MarketClock.getInstance().addListener(tickListener);

        recalculate();
    }

    // =========================
    // REACTIVE UPDATE
    // =========================
    private void recalculate() {
        if (currentAsset == null || cachedPositions == null) return;

        AssetMetrics metrics = assetService.calculateMetrics(
                cachedPositions,
                currentAsset.getId()
        );

        Platform.runLater(() -> updateUI(metrics)); // ← solo llama updateUI
        // ← nunca llama renderExposure() ni renderExposurePieChart()
    }

    // =========================
    // UI
    // =========================
    private void updateUI(AssetMetrics m) {

        assetName.setText(currentAsset.getName());
        assetTicker.setText(currentAsset.getTicker());

        assetMarketCap.setText(currentAsset.getMarketCap() + " €");
        assetType.setText(currentAsset.getType());
        assetSector.setText(currentAsset.getSector());

        assetRisk.setText(currentAsset.getRisk());
        assetLiquidity.setText(currentAsset.getLiquidity());

        double price = marketService.getPrice(currentAsset.getId());
        assetPrice.setText(String.format("%.2f €", price));
    }

    // =========================
    // EXPOSICIÓN TABLA
    // =========================
    private void renderExposure() {

        if (currentAsset == null || cachedPositions == null) return;

        ObservableList<Map<String, String>> items = FXCollections.observableArrayList();

        for (FundAssetPosition p : cachedPositions) {

            if (!p.getIdAsset().equals(currentAsset.getId())) continue;

            Fund fund = fundService.getById(p.getIdFund());

            String fundName = (fund != null)
                    ? fund.getName()
                    : p.getIdFund();

            Map<String, String> row = new HashMap<>();
            row.put("fund", fundName);
            row.put("value", String.format("%.2f €", p.getInvestedValue()));

            items.add(row);
        }

        tabla_exposicion.setItems(items);
    }

    // =========================
    // PIE CHART
    // =========================
    private void renderExposurePieChart() {

        if (currentAsset == null || cachedPositions == null) return;
        if (assetPieChart == null) return;

        assetPieChart.getChildren().clear();

        PieChart pieChart = new PieChart();
        pieChart.setLabelsVisible(false);
        pieChart.setLegendVisible(true);

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        for (FundAssetPosition p : cachedPositions) {

            if (!p.getIdAsset().equals(currentAsset.getId())) continue;

            Fund fund = fundService.getById(p.getIdFund());

            String name = (fund != null)
                    ? fund.getName()
                    : p.getIdFund();

            pieData.add(new PieChart.Data(name, p.getInvestedValue()));
        }

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