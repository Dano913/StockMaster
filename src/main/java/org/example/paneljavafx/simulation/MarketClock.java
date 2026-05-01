package org.example.paneljavafx.simulation;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MarketClock {

    // -------------------------
    // SINGLETON
    // -------------------------
    private static MarketClock instance;

    public static MarketClock getInstance() {
        if (instance == null) instance = new MarketClock();
        return instance;
    }

    // -------------------------
    // STATE
    // -------------------------
    private final List<MarketEngine> engines = new ArrayList<>();
    private final List<Runnable> listeners = new ArrayList<>();
    private Timeline timeline;
    private boolean running = false;

    // -------------------------
    // CONSTRUCTOR (privado)
    // -------------------------
    private MarketClock() {
        timeline = new Timeline(new KeyFrame(
                Duration.millis(500),
                e -> tick()
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    // -------------------------
    // REGISTRO
    // -------------------------
    public void register(MarketEngine engine) {
        if (!engines.contains(engine)) {
            engines.add(engine);
        }
    }

    public void unregister(MarketEngine engine) {
        engines.remove(engine);
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    // -------------------------
    // TICK GLOBAL
    // -------------------------
    private void tick() {
        // todos los engines avanzan juntos
        for (MarketEngine engine : engines) {
            engine.update();
        }
        // avisar a todas las vistas
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    // -------------------------
    // CONTROL
    // -------------------------
    public void start() {
        if (!running) {
            timeline.play();
            running = true;
        }
    }

    public void stop() {
        timeline.stop();
        running = false;
    }

    public void reset() {
        stop();
        engines.clear();
        listeners.clear();
    }
}