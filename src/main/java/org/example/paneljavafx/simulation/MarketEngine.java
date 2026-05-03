package org.example.paneljavafx.simulation;

import lombok.Getter;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Candle;

import java.util.ArrayList;
import java.util.List;

public class MarketEngine {

    @Getter private final Asset asset;
    private final FibonacciPriceModel fibModel;

    private final double riskFactor;
    private final double liquidityFactor;
    private final double volatilityFactor;

    @Getter private double lastPrice;

    private double open, high, low;
    private int ticks;

    private static final int TICKS_PER_CANDLE = 5; // 5 × 500ms = 2.5s por vela
    private static final double WICK_FACTOR = 0.6;

    @FunctionalInterface
    public interface CandleListener {
        void onCandle(Candle candle);
    }

    // Lista en vez de un solo listener
    private final List<CandleListener> listeners = new ArrayList<>();

    public void setCandleListener(CandleListener listener) {
        this.listeners.add(listener); // añade, no reemplaza
    }

    public void removeCandleListener(CandleListener listener) {
        this.listeners.remove(listener);
    }

    public MarketEngine(Asset asset, double startPrice) {
        this.asset = asset;
        this.fibModel = new FibonacciPriceModel(startPrice, asset.getVolatility());
        this.lastPrice = startPrice;
        this.volatilityFactor = asset.getVolatility();
        this.riskFactor = parseRisk(asset.getRisk());
        this.liquidityFactor = parseLiquidity(asset.getLiquidity());
    }

    public void update() {

        double basePrice = fibModel.tick();
        double noise = behaviorMultiplier() - 1.0;
        double newPrice = basePrice * (1.0 + noise * volatilityFactor * 0.01);

        double adjustedPrice = (ticks == 0)
                ? newPrice
                : lastPrice + (newPrice - lastPrice) * WICK_FACTOR;

        lastPrice = newPrice;

        if (ticks == 0) {
            open = high = low = adjustedPrice;
        }

        high = Math.max(high, adjustedPrice);
        low  = Math.min(low, adjustedPrice);

        ticks++;

        if (ticks >= TICKS_PER_CANDLE) {
            Candle candle = new Candle(
                    asset.getId(),
                    open, high, low,
                    newPrice,
                    System.currentTimeMillis()
            );

            // Notifica a TODOS los listeners (aggregator + chart)
            for (CandleListener l : listeners) {
                l.onCandle(candle);
            }

            ticks = 0;
        }
    }

    private double behaviorMultiplier() {
        double m = 1.0;
        m += (Math.random() - 0.5) * riskFactor * 0.2;
        return m;
    }

    private double parseRisk(String risk) {
        return switch (risk.toLowerCase()) {
            case "low"    -> 0.5;
            case "medium" -> 1.0;
            case "high"   -> 1.5;
            default       -> 1.0;
        };
    }

    private double parseLiquidity(String liquidity) { return 1.0; }
}