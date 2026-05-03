package org.example.paneljavafx.service;

import lombok.Getter;
import org.example.paneljavafx.dao.*;
import org.example.paneljavafx.dao.impl.*;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.simulation.*;

import java.util.*;

public class MarketService {

    private static final MarketService INSTANCE = new MarketService();
    public static MarketService getInstance() { return INSTANCE; }

    private MarketService() {}

    private final AssetDAO assetDAO = new AssetImpl();
    @Getter
    private final CandleDAO candleDAO = new CandleImpl();
    private final FundDAO fundDAO = new FundImpl();

    private final DailyCandleAggregator aggregator = new DailyCandleAggregator(candleDAO);
    private final CandleService candleService = CandleService.getInstance();

    private final Map<String, MarketEngine> engines = new HashMap<>();
    private boolean initialized = false;

    // 🔥 FIX 1: auto-protección
    public synchronized void bootstrapMarket() {

        if (initialized) return;
        initialized = true;

        List<Asset> assets = assetDAO.findAll();
        List<Fund> funds = fundDAO.findAll();

        CandleService.getInstance().refreshAll(assets);

        MarketClock clock = MarketClock.getInstance();

        for (Asset asset : assets) {
            MarketEngine engine = new MarketEngine(asset, asset.getInitialPrice());
            engine.setCandleListener(aggregator::onTick);
            engines.put(asset.getId(), engine);
            clock.register(engine);
        }

        clock.addListener(() ->
                CandleService.getInstance().refreshAll(assets)
        );

        clock.start();
    }

    // 🔥 FIX 2: garantía de inicialización
    private void ensureInit() {
        if (!initialized) {
            throw new IllegalStateException(
                    "MarketService no inicializado. Llama a bootstrapMarket() antes de usarlo."
            );
        }
    }

    public MarketEngine getEngine(String assetId) {
        ensureInit();
        return engines.get(assetId);
    }

    public double getPrice(String assetId) {
        ensureInit();
        MarketEngine e = engines.get(assetId);
        return e != null ? e.getLastPrice() : 0;
    }
}