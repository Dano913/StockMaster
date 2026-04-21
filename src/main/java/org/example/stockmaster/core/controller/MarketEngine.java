package org.example.stockmaster.core.controller;

import lombok.Getter;
import org.example.stockmaster.core.model.Candle;
import org.example.stockmaster.core.model.Asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarketEngine {

    private final FibonacciPriceModel fibModel;
    @Getter
    private final List<Candle> candles = new ArrayList<>();

    @Getter
    private double lastPrice;

    private double open, high, low;
    private int ticks;
    private static final int TICKS_PER_CANDLE = 20;

    public MarketEngine(Asset asset, List<Candle> initial) {

        this.fibModel = new FibonacciPriceModel(
                asset.getInitialPrice(),
                asset.getVolatility()
        );

        this.lastPrice = asset.getInitialPrice();

        if (initial != null) candles.addAll(initial);
    }

    public void update() {

        double price = fibModel.tick();

        if (ticks == 0) {
            open = high = low = price;
        }

        high = Math.max(high, price);
        low = Math.min(low, price);

        lastPrice = price;
        ticks++;

        if (ticks >= TICKS_PER_CANDLE) {
            candles.add(new Candle(open, high, low, price, System.currentTimeMillis()));
            ticks = 0;
        }
    }

}