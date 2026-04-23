package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import org.example.paneljavafx.chart.ChartController;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.List;

public class AssetViewController {

    // -------------------------
    // LABELS
    // -------------------------
    @FXML private Label assetName;
    @FXML private Label assetTicker;
    @FXML private Label assetIsin;
    @FXML private Label assetType;
    @FXML private Label assetSector;
    @FXML private Label assetRisk;
    @FXML private Label assetLiquidity;
    @FXML private Label assetPrice;

    // -------------------------
    // CHART
    // -------------------------
    @FXML private Canvas chartCanvas;

    private ChartController chartController;
    private MarketEngine engine;

    // -------------------------
    // LOAD DATA
    // -------------------------
    public void loadAsset(Asset asset) {

        if (asset == null) return;

        System.out.println("👉 LOADING ASSET: " + asset.getName());

        // -------------------------
        // LABELS
        // -------------------------
        assetName.setText(asset.getName());
        assetTicker.setText(asset.getTicker());
        assetIsin.setText(asset.getIsin());
        assetType.setText(asset.getType());
        assetSector.setText(asset.getSector());
        assetRisk.setText(asset.getRisk());
        assetLiquidity.setText(asset.getLiquidity());
        assetPrice.setText(String.valueOf(asset.getInitialPrice()));

        // -------------------------
        // CHART ENGINE
        // -------------------------
        engine = new MarketEngine(asset, null);

        chartController = new ChartController(chartCanvas, List.of(engine));
    }
}