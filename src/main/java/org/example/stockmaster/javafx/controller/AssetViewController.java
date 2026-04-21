package org.example.stockmaster.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import org.example.stockmaster.core.chart.ChartController;
import org.example.stockmaster.core.controller.MarketEngine;
import org.example.stockmaster.core.model.Asset;
import org.example.stockmaster.core.services.DataStore;

import java.util.ArrayList;
import java.util.List;

public class AssetViewController {

    @FXML
    private Pane canvasContainer;

    @FXML
    public void initialize() {

        canvasContainer.getChildren().clear();

        // -------------------------
        // LOAD ASSETS
        // -------------------------
        List<Asset> assets = DataStore.cargarAssets();

        if (assets == null || assets.isEmpty()) {
            System.out.println("❌ No assets found");
            return;
        }

        // -------------------------
        // GRID CONFIG
        // -------------------------
        int cols = 3;          // columnas del grid
        double size = 280;     // tamaño de cada gráfico

        List<ChartController> controllers = new ArrayList<>();

        // -------------------------
        // CREATE MULTIPLE CHARTS
        // -------------------------
        for (int i = 0; i < assets.size(); i++) {

            Asset asset = assets.get(i);

            Canvas canvas = new Canvas(size, size);

            MarketEngine engine = new MarketEngine(
                    asset,
                    DataStore.cargarCandles()
            );

            List<MarketEngine> engines = List.of(engine);

            // Chart independiente por asset
            new ChartController(canvas, engines);

            // posicion en grid
            int row = i / cols;
            int col = i % cols;

            canvas.setLayoutX(col * (size + 10));
            canvas.setLayoutY(row * (size + 10));

            canvasContainer.getChildren().add(canvas);
        }

        System.out.println("📊 Loaded charts: " + assets.size());
    }
}