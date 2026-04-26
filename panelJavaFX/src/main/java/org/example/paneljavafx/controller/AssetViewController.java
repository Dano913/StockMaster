package org.example.paneljavafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

import org.example.paneljavafx.chart.ChartController;
import org.example.paneljavafx.common.TabDataReceiver;
import org.example.paneljavafx.data.DataStore;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.service.AssetService;
import org.example.paneljavafx.service.dto.AssetMetrics;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.List;

public class AssetViewController implements TabDataReceiver<Asset> {

    // =========================
    // FXML
    // =========================
    @FXML private Label assetName;
    @FXML private Label assetTicker;
    @FXML private Label assetPrice;
    @FXML private Label assetFundsCount;
    @FXML private Label assetWeight;
    @FXML private Label assetRisk;
    @FXML private Label assetLiquidity;

    @FXML private ProgressBar assetExposure;
    @FXML private ListView<String> tabla_exposicion;
    @FXML private Canvas chartCanvas;

    // =========================
    // STATE
    // =========================
    private Asset currentAsset;
    private List<FundPosition> cachedPositions;

    private Runnable tickListener;
    private ChartController chartController;

    private final AssetService assetService = new AssetService();

    // =========================
    // ENTRY POINT (TAB SYSTEM)
    // =========================
    @Override
    public void loadData(Asset asset) {

        this.currentAsset = asset;

        renderStatic();
        bindMarket();
    }

    // =========================
    // OPTIONAL EXTRA DATA
    // =========================
    public void loadPositions(List<FundPosition> positions) {
        this.cachedPositions = positions;
        recalculate();
    }

    // =========================
    // MARKET BINDING
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
    // RECALC
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
    // UI UPDATE
    // =========================
    private void updateUI(AssetMetrics m) {

        assetExposure.setProgress(m.getExposureRatio());
        assetFundsCount.setText(m.getFundsExposed() + " funds");

        assetWeight.setText(String.format("%.2f%%", m.getGlobalWeight() * 100));

        MarketEngine engine = DataStore.engines.get(currentAsset.getId());

        double price = (engine != null) ? engine.getLastPrice() : 0.0;

        assetPrice.setText(price + " €");
    }

    // =========================
    // STATIC UI
    // =========================
    private void renderStatic() {

        if (currentAsset == null) return;

        assetName.setText(currentAsset.getName());
        assetTicker.setText(currentAsset.getTicker());

        assetRisk.setText("Risk: -");
        assetLiquidity.setText("Liquidity: -");
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