package org.example.paneljavafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import org.example.paneljavafx.chart.ChartController;
import org.example.paneljavafx.data.DataStore;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.List;

public class AssetViewController {

    // -------------------------
    // FXML — LABELS
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

    // -------------------------
    // FXML — LISTS / CANVAS
    // -------------------------
    @FXML private Canvas chartCanvas;
    @FXML private ListView<String> priceList;
    @FXML private ListView<String> tabla_exposicion;

    // -------------------------
    // STATE
    // -------------------------
    private MarketEngine engine;
    private ChartController chartController;
    private Runnable tickListener;
    private List<FundPosition> positions;

    private final ObservableList<String> prices = FXCollections.observableArrayList();

    private static final int MAX_PRICE_HISTORY = 50;

    // -------------------------
    // LOAD ASSET
    // -------------------------
    public void loadAsset(Asset asset) {

        if (asset == null) return;

        System.out.println("👉 LOADING ASSET VIEW: " + asset.getName());

        // reusar el engine que ya está corriendo en el clock global
        engine = DataStore.engines.get(asset.getId());

        if (engine == null) {
            System.err.println("⚠️ Engine no encontrado en DataStore para: " + asset.getId() + " — creando uno nuevo");
            engine = new MarketEngine(asset, List.of());
            MarketClock.getInstance().register(engine);
        }

        chartController = new ChartController(chartCanvas, List.of(engine));

        setupStaticInfo(asset);
        setupPriceList();

        tickListener = this::onTick;
        MarketClock.getInstance().addListener(tickListener);
    }

    // -------------------------
    // TICK — llamado por MarketClock cada 500ms
    // -------------------------
    private void onTick() {

        double price  = engine.getLastPrice();
        double change = engine.getChange();

        Platform.runLater(() -> {

            assetPrice.setText(String.format("%.2f €", price));
            assetChange.setText(String.format("%+.2f %%", change));
            assetChange.setTextFill(change >= 0 ? Color.LIMEGREEN : Color.RED);

            String arrow = change >= 0 ? "▲" : "▼";
            prices.add(String.format("%s %.2f € | %+.2f%%", arrow, price, change));

            if (prices.size() > MAX_PRICE_HISTORY) prices.remove(0);

            priceList.scrollTo(prices.size() - 1);
        });
    }

    // -------------------------
    // EXPOSURE VIEW
    // -------------------------
    public void loadAssetExposure(Asset asset, List<FundPosition> positions) {
        this.positions = positions;
        renderExposure(asset);
    }

    private void renderExposure(Asset asset) {

        if (positions == null || asset == null) {
            tabla_exposicion.setItems(FXCollections.observableArrayList("Sin datos de exposición"));
            return;
        }

        List<String> items = positions.stream()
                .filter(p -> p.getIdAsset().equals(asset.getId()))
                .map(p -> "Fondo: " + p.getIdFund()
                        + " → " + String.format("%.2f%%", p.getPesoPorcentual()))
                .toList();

        if (items.isEmpty()) {
            tabla_exposicion.setItems(FXCollections.observableArrayList("Sin exposición en fondos"));
            return;
        }

        tabla_exposicion.setItems(FXCollections.observableArrayList(items));
        tabla_exposicion.setStyle("""
            -fx-background-color: transparent;
            -fx-control-inner-background: transparent;
            -fx-background-insets: 0;
            -fx-padding: 0;
            -fx-font-size: 18px;
            -fx-text-fill: cyan;
        """);
    }

    // -------------------------
    // SETUP — INFO ESTÁTICA
    // -------------------------
    private void setupStaticInfo(Asset asset) {
        assetName.setText(asset.getName());
        assetTicker.setText(asset.getTicker());
        assetIsin.setText(asset.getIsin());
        assetType.setText(asset.getType());
        assetSector.setText(asset.getSector());
        assetRisk.setText(asset.getRisk());
        assetLiquidity.setText(asset.getLiquidity());
    }

    // -------------------------
    // SETUP — PRICE LIST
    // -------------------------
    private void setupPriceList() {

        priceList.setItems(prices);

        priceList.setStyle("""
            -fx-background-color: transparent;
            -fx-control-inner-background: transparent;
            -fx-background-insets: 0;
            -fx-padding: 0;
        """);

        priceList.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                priceList.lookupAll(".scroll-bar").forEach(node -> {
                    node.setOpacity(0);
                    node.setVisible(false);
                    node.setManaged(false);
                });
            }
        });

        priceList.setCellFactory(list -> new PriceCell());
    }

    // -------------------------
    // CLEANUP — llamar al cerrar el tab
    // -------------------------
    public void onClose() {
        // NO desregistramos el engine — sigue corriendo en background
        // solo quitamos el listener de esta vista
        if (tickListener != null) {
            MarketClock.getInstance().removeListener(tickListener);
            System.out.println("🔴 Vista cerrada: " + (engine != null ? engine.getAsset().getTicker() : "?"));
        }
    }

    // -------------------------
    // INNER CLASS — CELDA DE PRECIO
    // -------------------------
    private static class PriceCell extends ListCell<String> {

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            setText(item);
            setTextFill(item.contains("▲") ? Color.LIMEGREEN : Color.RED);
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
    }
}