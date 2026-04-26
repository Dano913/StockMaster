package org.example.paneljavafx.service;

import org.example.paneljavafx.data.MarketDataSource;
import org.example.paneljavafx.data.PriceRecordReader;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.List;
import java.util.Map;

public class MarketService {

    private final MarketDataSource dataSource = new MarketDataSource();

    /**
     * Inicializa todo el mercado:
     * - carga assets
     * - crea engines
     * - registra en clock
     * - arranca simulación
     */
    public List<MarketEngine> bootstrapMarket() {

        List<Asset> assets = dataSource.loadAssets();

        Map<String, Double> lastPrices = dataSource.loadLastPrices();

        MarketClock clock = MarketClock.getInstance();

        List<MarketEngine> engines = assets.stream()
                .map(asset -> {

                    double startPrice = lastPrices.getOrDefault(
                            asset.getId(),
                            asset.getInitialPrice()
                    );

                    MarketEngine engine = new MarketEngine(asset, List.of(), startPrice);

                    clock.register(engine);

                    return engine;
                })
                .toList();

        clock.start();

        System.out.println("🚀 MarketService: " + engines.size() + " engines inicializados");

        return engines;
    }
}