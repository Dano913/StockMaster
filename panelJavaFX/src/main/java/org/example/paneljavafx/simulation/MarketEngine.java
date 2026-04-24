package org.example.paneljavafx.simulation;

import lombok.Getter;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Candle;
import org.example.paneljavafx.simulation.FibonacciPriceModel;

import java.util.ArrayList;
import java.util.List;

public class MarketEngine {

    private final FibonacciPriceModel fibModel;

    @Getter
    private final List<Candle> candles = new ArrayList<>();

    @Getter
    private final Asset asset;

    @Getter
    private double lastPrice;

    @Getter
    private double change;

    private double open, high, low;

    private int ticks;

    private static final int TICKS_PER_CANDLE = 20;

    // -------------------------
    // FACTORES DERIVADOS DEL ASSET
    // -------------------------
    private final double riskFactor;
    private final double liquidityFactor;
    private final double volatilityFactor;

    public MarketEngine(Asset asset, List<Candle> initial) {

        this.asset = asset;

        // motor base (Fibonacci)
        this.fibModel = new FibonacciPriceModel(
                asset.getInitialPrice(),
                asset.getVolatility()
        );

        this.lastPrice = asset.getInitialPrice();

        // -------------------------
        // derivación de comportamiento desde Asset
        // -------------------------
        this.volatilityFactor = asset.getVolatility();
        this.riskFactor = parseRisk(asset.getRisk());
        this.liquidityFactor = parseLiquidity(asset.getLiquidity());

        if (initial != null) {
            candles.addAll(initial);
        }
    }

    public void update() {

        double basePrice = fibModel.tick();

        // 🔧 normalizamos el impacto del mercado respecto al precio inicial
        double normalizedVolatility = volatilityFactor * 0.01; // control global

        double noise = behaviorMultiplier() - 1.0;

        double newPrice = basePrice * (1.0 + noise * normalizedVolatility);

        // -------------------------
        // CHANGE REAL
        // -------------------------
        this.change = (lastPrice == 0)
                ? 0
                : ((newPrice - lastPrice) / lastPrice) * 100;

        // 🔧 FIX: evitar expansión progresiva de amplitud
        double wickFactor = 0.25; // constante estable

        double adjustedPrice = (ticks == 0)
                ? newPrice
                : lastPrice + (newPrice - lastPrice) * wickFactor;

        lastPrice = newPrice;

        if (ticks == 0) {
            open = high = low = adjustedPrice;
        }

        high = Math.max(high, adjustedPrice);
        low = Math.min(low, adjustedPrice);

        ticks++;

        if (ticks >= TICKS_PER_CANDLE) {
            candles.add(new Candle(
                    open,
                    high,
                    low,
                    newPrice,
                    System.currentTimeMillis()
            ));
            ticks = 0;
        }
    }

    // -------------------------
    // comportamiento del mercado según Asset
    // -------------------------
    private double behaviorMultiplier() {

        double multiplier = 1.0;

        multiplier += (Math.random() - 0.5) * riskFactor * 0.12;

        if (liquidityFactor < 0.5 && Math.random() < 0.05) {
            multiplier += (Math.random() - 0.5) * 0.05;
        }

        switch (asset.getSector()) {

            case "shadow_liquidity":
                multiplier += (Math.random() - 0.5) * 0.25;
                break;

            case "ai_economies":
                multiplier *= 1.02; // ligera tendencia alcista
                break;

            case "time_arbitrage":
                multiplier += (Math.random() - 0.5) * 0.2;
                break;
        }

        return multiplier;
    }

    // -------------------------
    // parsers simples (IMPORTANTE)
    // -------------------------
    private double parseRisk(String risk) {

        return switch (risk.toLowerCase()) {
            case "low" -> 0.5;
            case "medium" -> 1.0;
            case "high" -> 1.5;
            case "extreme" -> 2.2;
            default -> 1.0;
        };
    }

    private double parseLiquidity(String liquidity) {

        return switch (liquidity.toLowerCase()) {
            case "low" -> 0.5;
            case "medium" -> 1.0;
            case "high" -> 1.3;
            case "very_high" -> 1.6;
            default -> 1.0;
        };
    }
}