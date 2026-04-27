package org.example.paneljavafx.simulation;

import lombok.Getter;
import org.example.paneljavafx.data.PriceRecordWriter;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Candle;
import org.example.paneljavafx.model.PriceRecord;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketEngine {

    // -------------------------
    // STATE DEL PRECIO
    // -------------------------
    @Getter
    public static class PriceState {
        public double raw;
        public double smooth;
        public double trend;
        public double change;
    }

    @Getter
    private final PriceState price = new PriceState();

    // -------------------------
    // MARKET DATA
    // -------------------------
    @Getter
    private final List<Candle> candles = new ArrayList<>();

    @Getter
    private final Asset asset;

    // -------------------------
    // MODELO BASE
    // -------------------------
    private final FibonacciPriceModel fibModel;

    // -------------------------
    // FACTORES DEL ASSET
    // -------------------------
    private final double riskFactor;
    private final double liquidityFactor;
    private final double volatilityFactor;

    // -------------------------
    // CANDLE STATE
    // -------------------------
    private double lastPrice;
    private double open, high, low;
    private int ticks;
    private int tickVolume;

    private static final int    TICKS_PER_CANDLE = 5;
    private static final double WICK_FACTOR      = 0.6;
    private static final String TEMPORALIDAD     = "30S";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    // -------------------------
    // CONSTRUCTOR ORIGINAL
    // Usa asset.getInitialPrice() — se llama cuando no hay historial persistido.
    // -------------------------
    public MarketEngine(Asset asset, List<Candle> initial) {
        this(asset, initial, asset.getInitialPrice());
    }

    // -------------------------
    // CONSTRUCTOR CON PRECIO RESTAURADO
    // startPrice viene de PriceRecordReader — el último cierre guardado en disco.
    // FibonacciPriceModel arranca desde ese precio en lugar del inicial del asset,
    // así la simulación continúa desde donde quedó.
    // -------------------------
    public MarketEngine(Asset asset, List<Candle> initial, double startPrice) {

        this.asset = asset;

        this.fibModel = new FibonacciPriceModel(
                startPrice,          // precio restaurado, no el hardcoded del asset
                asset.getVolatility()
        );

        this.lastPrice    = startPrice;
        this.price.raw    = startPrice;
        this.price.smooth = startPrice;
        this.price.trend  = startPrice;

        this.volatilityFactor = asset.getVolatility();
        this.riskFactor       = parseRisk(asset.getRisk());
        this.liquidityFactor  = parseLiquidity(asset.getLiquidity());

        if (initial != null) {
            candles.addAll(initial);
        }

//        System.out.printf("🚀 MarketEngine [%s] iniciado en %.2f%s%n",
//                asset.getTicker(),
//                startPrice,
//                startPrice != asset.getInitialPrice() ? " (restaurado)" : " (precio inicial)");
    }

    // -------------------------
    // MAIN LOOP (TICK)
    // -------------------------
    public void update() {

        // 1. precio base del modelo Fibonacci
        double basePrice = fibModel.tick();

        // 2. volatilidad normalizada — evita expansión progresiva de amplitud
        double normalizedVolatility = volatilityFactor * 0.01;

        // 3. ruido de mercado centrado en 0
        double noise = behaviorMultiplier() - 1.0;

        // 4. precio nuevo
        double newPrice = basePrice * (1.0 + noise * normalizedVolatility);

        // 5. cambio porcentual real
        price.change = (lastPrice == 0)
                ? 0
                : ((newPrice - lastPrice) / lastPrice) * 100;

        // 6. precio suavizado para UI
        price.smooth = (price.smooth == 0)
                ? newPrice
                : price.smooth + (newPrice - price.smooth) * 1;

        price.raw   = newPrice;
        price.trend = basePrice;

        // 7. precio ajustado para las mechas — evita saltos bruscos
        double adjustedPrice = (ticks == 0)
                ? newPrice
                : lastPrice + (newPrice - lastPrice) * WICK_FACTOR;

        lastPrice = newPrice;

        // 8. actualizar OHLC
        if (ticks == 0) {
            open = high = low = adjustedPrice;
            tickVolume = 0;
        }

        high = Math.max(high, adjustedPrice);
        low  = Math.min(low,  adjustedPrice);
        tickVolume++;
        ticks++;

        // 9. cerrar vela
        if (ticks >= TICKS_PER_CANDLE) {

            long now = System.currentTimeMillis();

            candles.add(new Candle(open, high, low, newPrice, now));

//            System.out.println("🕯️ CANDLE [" + asset.getTicker() + "]"
//                    + " | open="  + round(open)
//                    + " | close=" + round(newPrice)
//                    + " | high="  + round(high)
//                    + " | low="   + round(low)
//                    + " | total=" + candles.size());

            persistCandle(open, high, low, newPrice, now, tickVolume);

            ticks = 0;
        }
    }

    // -------------------------
    // PERSISTENCIA
    // -------------------------
    private void persistCandle(double o, double h, double l, double c, long timestamp, int volume) {

        PriceRecord record = new PriceRecord(
                UUID.randomUUID().toString(),
                asset.getId(),
                round(o),
                round(c),
                round(h),
                round(l),
                TEMPORALIDAD,
                DATE_FMT.format(Instant.ofEpochMilli(timestamp)),
                volume
        );

        PriceRecordWriter.append(record);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // -------------------------
    // MARKET BEHAVIOR MODEL
    // -------------------------
    private double behaviorMultiplier() {

        double m = 1.0;

        m += (Math.random() - 0.5) * riskFactor * 0.25;

        if (liquidityFactor < 0.5 && Math.random() < 0.05) {
            m += (Math.random() - 0.5) * 0.05;
        }

        switch (asset.getSector()) {
            case "shadow_liquidity" -> m += (Math.random() - 0.5) * 0.25;
            case "ai_economies"     -> m *= 1.02;
            case "time_arbitrage"   -> m += (Math.random() - 0.5) * 0.2;
        }

        return m;
    }

    // -------------------------
    // PARSERS
    // -------------------------
    private double parseRisk(String risk) {
        return switch (risk.toLowerCase()) {
            case "low"     -> 0.5;
            case "medium"  -> 1.0;
            case "high"    -> 1.5;
            case "extreme" -> 2.2;
            default        -> 1.0;
        };
    }

    private double parseLiquidity(String liquidity) {
        return switch (liquidity.toLowerCase()) {
            case "low"       -> 0.5;
            case "medium"    -> 1.0;
            case "high"      -> 1.3;
            case "very_high" -> 1.6;
            default          -> 1.0;
        };
    }

    // -------------------------
    // GETTERS DE CONVENIENCIA
    // -------------------------
    public double getLastPrice()   { return price.raw;    }
    public double getSmoothPrice() { return price.smooth; }
    public double getTrendPrice()  { return price.trend;  }
    public double getChange()      { return price.change; }
}