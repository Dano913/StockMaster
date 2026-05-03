package org.example.paneljavafx.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import org.example.paneljavafx.dao.CandleDAO;
import org.example.paneljavafx.model.Candle;
import org.example.paneljavafx.service.ChartService;
import org.example.paneljavafx.simulation.MarketEngine;

public class ChartController {

    private final Canvas canvas;
    private final ChartService service;
    private final MarketEngine engine;

    private double lastMouseX = 0;

    public ChartController(Canvas canvas, CandleDAO candleDAO, MarketEngine engine) {
        this.canvas  = canvas;
        this.service = new ChartService(candleDAO);
        this.engine  = engine;

        // Carga histórico inicial desde BD
        service.setEngine(engine);

        // Suscribe al engine para recibir velas nuevas
        if (engine != null) {
            engine.setCandleListener(candle -> {
                System.out.println("🔔 listener disparado: " + candle.getAssetId());
                Platform.runLater(() -> service.onNewCandle(candle));
            });
        }

        init();
    }

    private void init() {

        canvas.setOnScroll(e -> {
            if (e.isControlDown()) {
                service.zoom(e.getDeltaY());
            } else {
                service.changeCandleWidth(e.getDeltaY());
            }
        });

        canvas.setOnMousePressed(e -> lastMouseX = e.getX());

        canvas.setOnMouseDragged(e -> {
            service.scroll(e.getX() - lastMouseX);
            lastMouseX = e.getX();
        });

        // AnimationTimer solo dibuja — no toca BD ni hace cálculos pesados
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
            }
        }.start();
    }

    private void draw() {
        service.draw(
                canvas.getGraphicsContext2D(),
                canvas.getWidth(),
                canvas.getHeight()
        );
    }
}