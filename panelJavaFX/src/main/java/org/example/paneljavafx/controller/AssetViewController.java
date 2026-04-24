package org.example.paneljavafx.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.Duration;
import org.example.paneljavafx.chart.ChartController;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.List;
import java.util.Map;

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
    @FXML private Label assetChange;
    @FXML private ListView tabla_exposicion;

    // -------------------------
    // UI
    // -------------------------
    @FXML private Canvas chartCanvas;
    @FXML private ListView<String> priceList;

    private List<FundPosition> positions;
    private ChartController chartController;
    private MarketEngine engine;

    private final ObservableList<String> prices = FXCollections.observableArrayList();

    // -------------------------
    // LOAD ASSET SIMPLE
    // -------------------------
    public void loadAsset(Asset asset) {

        if (asset == null) return;

        System.out.println("👉 LOADING ASSET: " + asset.getName());

        engine = new MarketEngine(asset, List.of());
        chartController = new ChartController(chartCanvas, List.of(engine));

        // -------------------------
        // LISTVIEW SETUP
        // -------------------------
        ObservableList<String> prices = FXCollections.observableArrayList();
        priceList.setItems(prices);

        priceList.setStyle("""
        -fx-background-color: transparent;
        -fx-control-inner-background: transparent;
        -fx-scroll-bar-policy: never;
        -fx-background-insets: 0;
        -fx-padding: 0;
    """);

        // quitar scrollbars visuales
        priceList.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            priceList.lookupAll(".scroll-bar").forEach(node -> {
                node.setOpacity(0);
                node.setVisible(false);
                node.setManaged(false);
            });
        });

        priceList.setCellFactory(list -> new javafx.scene.control.ListCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(item);

                setTextFill(item.contains("▲")
                        ? javafx.scene.paint.Color.LIMEGREEN
                        : javafx.scene.paint.Color.RED);

                setStyle("""
                -fx-background-color: transparent;
                -fx-alignment: center;
                -fx-font-size: 20px;
                -fx-padding: 6;
            """);
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(false);
            }
        });

        // -------------------------
        // TIMELINE (SIMULACIÓN)
        // -------------------------
        Timeline timeline = new Timeline(new javafx.animation.KeyFrame(
                javafx.util.Duration.millis(500),
                e -> {

                    engine.update();

                    double price = engine.getLastPrice();
                    double change = engine.getChange();

                    assetPrice.setText(String.format("%.2f €", price));
                    assetChange.setText(String.format("%+.2f %%", change));

                    // dirección
                    String arrow = (change >= 0) ? "▲" : "▼";

                    prices.add(String.format("%s %.2f € | %+.2f%%", arrow, price, change));

                    // auto scroll al final
                    priceList.scrollTo(prices.size() - 1);

                    // limitar historial
                    if (prices.size() > 50) {
                        prices.remove(0);
                    }
                }
        ));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // -------------------------
        // STATIC INFO
        // -------------------------
        assetName.setText(asset.getName());
        assetTicker.setText(asset.getTicker());
        assetIsin.setText(asset.getIsin());
        assetType.setText(asset.getType());
        assetSector.setText(asset.getSector());
        assetRisk.setText(asset.getRisk());
        assetLiquidity.setText(asset.getLiquidity());
    }

    // -------------------------
    // EXPOSURE VIEW (FONDO → ACTIVO)
    // -------------------------
    public void loadAssetExposure(Asset asset, List<FundPosition> positions) {

        this.positions = positions;

        renderExposure(asset);
    }

    void renderExposure(Asset asset) {

        if (positions == null) {
            tabla_exposicion.setItems(
                    javafx.collections.FXCollections.observableArrayList(
                            "Sin datos de exposición"
                    )
            );
            return;
        }

        List<FundPosition> assetPositions = positions.stream()
                .filter(p -> p.getIdAsset().equals(asset.getId()))
                .toList();

        if (assetPositions.isEmpty()) {
            tabla_exposicion.setItems(
                    javafx.collections.FXCollections.observableArrayList(
                            "Sin exposición en fondos"
                    )
            );
            return;
        }

        var items = assetPositions.stream()
                .map(p ->
                        "Fondo: " + p.getIdFund()
                                + " → " + String.format("%.2f%%", p.getPesoPorcentual())
                )
                .toList();

        tabla_exposicion.setItems(
                javafx.collections.FXCollections.observableArrayList(items)
        );

        tabla_exposicion.setStyle("""
            -fx-background-color: transparent;
            -fx-control-inner-background: transparent;
            -fx-background-insets: 0;
            -fx-padding: 0;
            -fx-font-size: 18px;
            -fx-text-fill: cyan;
        """);
    }
}