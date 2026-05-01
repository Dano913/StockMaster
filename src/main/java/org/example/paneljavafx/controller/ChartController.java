package org.example.paneljavafx.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

import org.example.paneljavafx.service.ChartService;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.List;

public class ChartController {

    private final Canvas canvas;
    private final ChartService service = new ChartService();

    private double lastMouseX = 0;

    public ChartController(Canvas canvas, List<MarketEngine> engines) {

        this.canvas = canvas;

        service.setEngine(engines.get(0));

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