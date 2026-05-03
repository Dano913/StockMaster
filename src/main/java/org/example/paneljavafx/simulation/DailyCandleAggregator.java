package org.example.paneljavafx.simulation;

import org.example.paneljavafx.dao.CandleDAO;
import org.example.paneljavafx.model.Candle;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DailyCandleAggregator {

    private final CandleDAO candleDAO;

    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "db-writer");
        t.setDaemon(true);
        return t;
    });

    // Acumula la vela diaria por asset (para BD)
    private final Map<String, Candle> dailyCandle = new HashMap<>();
    private final Map<String, LocalDate> currentDay = new HashMap<>();

    public DailyCandleAggregator(CandleDAO dao) {
        this.candleDAO = dao;
    }

    // Recibe velas de 5 ticks (2.5s) y las acumula en vela diaria
    public void onTick(Candle tick) {

        LocalDate day = Instant.ofEpochMilli(tick.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String assetId = tick.getAssetId();
        Candle daily   = dailyCandle.get(assetId);
        LocalDate storedDay = currentDay.get(assetId);

        // Primera vela del asset
        if (daily == null) {
            dailyCandle.put(assetId, copy(tick));
            currentDay.put(assetId, day);
            return;
        }

        // Mismo día → acumular high/low/close en la vela diaria
        if (day.equals(storedDay)) {
            Candle updated = new Candle(
                    assetId,
                    daily.getOpen(),                          // open del día no cambia
                    Math.max(daily.getHigh(), tick.getHigh()),
                    Math.min(daily.getLow(),  tick.getLow()),
                    tick.getClose(),                          // close = última vela recibida
                    tick.getTimestamp()
            );
            dailyCandle.put(assetId, updated);
            return;
        }

        // Cambio de día → persistir la vela diaria completa
        final Candle toSave = daily;
        dbExecutor.submit(() -> {
            try {
                candleDAO.save(toSave);
            } catch (Exception e) {
                System.err.println("Error guardando candle diaria: " + e.getMessage());
            }
        });

        dailyCandle.put(assetId, copy(tick));
        currentDay.put(assetId, day);
    }

    private Candle copy(Candle c) {
        return new Candle(
                c.getAssetId(),
                c.getOpen(), c.getHigh(), c.getLow(), c.getClose(),
                c.getTimestamp()
        );
    }
}