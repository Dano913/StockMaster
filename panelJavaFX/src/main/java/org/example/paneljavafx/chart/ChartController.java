package org.example.paneljavafx.chart;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import org.example.paneljavafx.simulation.MarketEngine;
import org.example.paneljavafx.simulation.MarketPrinter;

import java.util.List;

public class ChartController {

    private final Canvas canvas;

    // -------------------------
    // MULTI ASSET SUPPORT
    // -------------------------
    private final List<MarketEngine> engines;
    private MarketEngine activeEngine;

    // -------------------------
    // GRAPH STATE
    // -------------------------
    private final ChartState state = new ChartState();
    private final ChartRenderer renderer = new ChartRenderer();

    private static final double PAD_LEFT = 65;
    private static final double PAD_TOP = 12;

    private double lastMouseX = 0;

    public ChartController(Canvas canvas, List<MarketEngine> engines) {

        this.canvas = canvas;

        List<MarketEngine> safeEngines = engines;

        if (engines == null || engines.isEmpty()) {
            throw new IllegalStateException(
                    "❌ No assets loaded. Market cannot start without real data."
            );
        }

        this.engines = safeEngines;
        this.activeEngine = safeEngines.get(0);

        init();
    }

    // -------------------------
    // INIT
    // -------------------------
    private void init() {

        // -------------------------
        // ZOOM + SCROLL
        // -------------------------
        canvas.setOnScroll(e -> {

            if (e.isControlDown()) {

                state.priceZoom = clamp(
                        state.priceZoom * (1 + e.getDeltaY() * 0.002),
                        0.1,
                        20
                );

            } else {

                state.candleWidth = clamp(
                        state.candleWidth - e.getDeltaY() * 0.03,
                        3,
                        30
                );
            }
        });

        // -------------------------
        // DRAG (correcto)
        // -------------------------
        canvas.setOnMousePressed(e -> {
            lastMouseX = e.getX();
        });

        canvas.setOnMouseDragged(e -> {

            double delta = e.getX() - lastMouseX;
            state.scrollX += delta;
            lastMouseX = e.getX();
        });

        // -------------------------
        // MAIN LOOP
        // -------------------------
        new AnimationTimer() {

            @Override
            public void handle(long now) {

                // actualizar TODOS los mercados (background)
                for (MarketEngine engine : engines) {
                    engine.update();
                }

                // render SOLO el activo seleccionado
                draw();

                //MarketPrinter.print(engines);
            }

        }.start();
    }

    // -------------------------
    // SWITCH ACTIVE ASSET
    // -------------------------
    public void setActiveEngine(int index) {
        if (index >= 0 && index < engines.size()) {
            this.activeEngine = engines.get(index);
        }
    }

    // -------------------------
    // RENDER
    // -------------------------
    private void draw() {

        GraphicsContext g = canvas.getGraphicsContext2D();

        double W = canvas.getWidth();
        double H = canvas.getHeight();

        // background
        g.clearRect(0, 0, W, H);
        g.setFill(Color.web("#111111"));
        g.fillRect(0, 0, W, H);

        var candles = activeEngine.getCandles();

        if (candles == null || candles.size() < 2) {
            return;
        }

        var view = state.build(candles, W - PAD_LEFT, H - PAD_TOP);

        renderer.draw(
                g,
                candles,
                view,
                W,
                H,
                PAD_LEFT,
                PAD_TOP,
                state.candleWidth
        );
    }

    // -------------------------
    // UTILS
    // -------------------------
    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}