package org.example.paneljavafx.service;

import org.example.paneljavafx.data.MarketDataSource;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketService {

    // =========================
    // SINGLETON
    // =========================
    private static final MarketService INSTANCE = new MarketService();

    public static MarketService getInstance() {
        return INSTANCE;
    }

    private MarketService() {}

    // =========================
    // DEPENDENCY
    // =========================
    private final MarketDataSource dataSource = new MarketDataSource();

    // =========================
    // STATE
    // =========================
    private final Map<String, MarketEngine> engines = new HashMap<>();
    private final List<Asset> assets = new java.util.ArrayList<>();

    private boolean initialized = false;

    // =========================
    // BOOTSTRAP
    // =========================
    public void bootstrapMarket() {

        if (initialized) return;
        initialized = true;

        assets.clear();
        assets.addAll(dataSource.loadAssets());

        Map<String, Double> lastPrices = dataSource.loadLastPrices();

        MarketClock clock = MarketClock.getInstance();

        for (Asset asset : assets) {

            double startPrice = lastPrices.getOrDefault(
                    asset.getId(),
                    asset.getInitialPrice()
            );

            MarketEngine engine = new MarketEngine(asset, List.of(), startPrice);

            engines.put(asset.getId(), engine);
            clock.register(engine);
        }

        clock.start();

        //System.out.println("🚀 Market inicializado: " + engines.size() + " engines");
    }

    // =========================
    // GET ENGINE
    // =========================
    public MarketEngine getEngine(String assetId) {
        return engines.get(assetId);
    }

    // =========================
    // GET PRICE
    // =========================
    public double getPrice(String assetId) {
        MarketEngine e = engines.get(assetId);
        return e != null ? e.getLastPrice() : 0;
    }

    // =========================
    // GET CHANGE (%)
    // =========================
    public double getChange(String assetId) {
        MarketEngine e = engines.get(assetId);
        return e != null ? e.getChange() : 0;
    }

    // =========================
    // GET ALL ASSETS
    // =========================
    public List<Asset> getAllAssets() {
        return assets;
    }
}