package org.example.paneljavafx.service;

import org.example.paneljavafx.data.MarketDataSource;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketService {

    // ========================= SINGLETON =========================
    private static final MarketService INSTANCE = new MarketService();
    public static MarketService getInstance() {
        return INSTANCE;
    }
    private MarketService() {}

    // ========================= DEPENDENCY =========================
    private final MarketDataSource dataSource = new MarketDataSource();

    // ========================= CACHE =========================
    private final Map<String, MarketEngine> engines = new HashMap<>();
    private final List<Asset> assets = new java.util.ArrayList<>();
    private boolean initialized = false;

    // ========================= BOOTSTRAP MARKET =========================
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
    }

    // ========================= GET =========================
    public MarketEngine getEngine(String assetId) {
        return engines.get(assetId);
    }

    public double getPrice(String assetId) {
        MarketEngine e = engines.get(assetId);
        return e != null ? e.getLastPrice() : 0;
    }
}