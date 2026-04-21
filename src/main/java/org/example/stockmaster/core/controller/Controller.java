package org.example.stockmaster.core.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;

import org.example.stockmaster.core.model.Asset;
import org.example.stockmaster.core.model.Candle;
import org.example.stockmaster.core.services.DataStore;

import java.util.ArrayList;

public class Controller {

    @FXML private Canvas canvas;

    // -------------------------
    // MARKET ENGINE (NUEVO)
    // -------------------------
    private MarketEngine market;
    private ArrayList<Candle> candles;

    // -------------------------
    // VISUAL STATE
    // -------------------------
    private double candleWidth = 8;
    private double priceZoom = 1.0;
    private double priceOffset = 0;
    private boolean yManual = false;

    private double scrollX = 0;

    private double dragStartX = 0;
    private double dragStartScrollX = 0;

    // -------------------------
    // ZONES
    // -------------------------
    private enum HoverZone {
        Y_AXIS,
        CHART,
        X_AXIS
    }

    private HoverZone hoverZone = HoverZone.CHART;

    // -------------------------
    // LAYOUT
    // -------------------------
    private static final double PAD_LEFT   = 65;
    private static final double PAD_RIGHT  = 10;
    private static final double PAD_TOP    = 12;
    private static final double PAD_BOTTOM = 28;

    // -------------------------
    // INIT
    // -------------------------
    @FXML
    public void initialize() {

        candles = DataStore.cargar();

        market = new MarketEngine(
                new Asset(30000, 0.003, 0.02, 1.5),
                candles
        );

        canvas.setOnMouseMoved(e -> updateHoverZone(e.getX(), e.getY()));

        canvas.setOnScroll(e -> {
            switch (hoverZone) {

                case Y_AXIS -> {
                    yManual = true;
                    double factor = 1 + e.getDeltaY() * 0.002;
                    priceZoom = clamp(priceZoom * factor, 0.1, 20);
                }

                case CHART -> {
                    candleWidth = clamp(candleWidth - e.getDeltaY() * 0.03, 3, 30);
                }

                case X_AXIS -> {
                    scrollX += e.getDeltaY() * 5;
                }
            }
        });

        canvas.setOnMousePressed(e -> {
            dragStartX = e.getX();
            dragStartScrollX = scrollX;
        });

        canvas.setOnMouseDragged(e -> {
            updateHoverZone(e.getX(), e.getY());
            scrollX = dragStartScrollX + (e.getX() - dragStartX);
        });

        canvas.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                yManual = false;
                priceZoom = 1.0;
                priceOffset = 0;
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                // 🔥 SOLO LÓGICA DE MERCADO AQUÍ YA NO EXISTE
                market.update();

                if (Math.random() < 0.01) {
                    market.changeTrend();
                }

                dibujar();

            }
        };

        timer.start();
    }

    // -------------------------
    // DRAW
    // -------------------------
    private void dibujar() {

        GraphicsContext g = canvas.getGraphicsContext2D();

        double W = canvas.getWidth();
        double H = canvas.getHeight();

        double cW = W - PAD_LEFT - PAD_RIGHT;
        double cH = H - PAD_TOP - PAD_BOTTOM;

        g.clearRect(0, 0, W, H);
        g.setFill(Color.web("#111111"));
        g.fillRect(0, 0, W, H);
        g.setFill(Color.web("#00ffcc"));
        g.setFont(javafx.scene.text.Font.font("Monospaced", 14));

        g.fillText(
                "PRICE: " + String.format("%.2f", market.getLastPrice()),
                canvas.getWidth() - 140,
                20
        );

        candles = market.getCandles();

        if (candles.size() < 2) return;

        int maxVisible = (int)(cW / candleWidth) + 2;
        int startIdx = Math.max(0, candles.size() - maxVisible + (int)(scrollX / candleWidth));
        int endIdx = Math.min(candles.size(), startIdx + maxVisible + 2);

        double rawMax = Double.MIN_VALUE;
        double rawMin = Double.MAX_VALUE;

        for (int i = startIdx; i < endIdx; i++) {
            Candle c = candles.get(i);
            rawMax = Math.max(rawMax, c.high);
            rawMin = Math.min(rawMin, c.low);
        }

        double rawRange = (rawMax == rawMin) ? 1 : rawMax - rawMin;
        double padding = rawRange * 0.08;

        rawMax += padding;
        rawMin -= padding;

        double priceMin = rawMin;
        double priceMax = rawMax;

        double priceRange = priceMax - priceMin;

        // -------------------------
        // CANDLES
        // -------------------------
        for (int i = startIdx; i < endIdx; i++) {

            Candle c = candles.get(i);

            double x = PAD_LEFT + (i - startIdx) * candleWidth;

            double openY  = PAD_TOP + priceToY(c.open, priceMin, priceRange, cH);
            double closeY = PAD_TOP + priceToY(c.close, priceMin, priceRange, cH);
            double highY  = PAD_TOP + priceToY(c.high, priceMin, priceRange, cH);
            double lowY   = PAD_TOP + priceToY(c.low, priceMin, priceRange, cH);

            g.setStroke(Color.GRAY);
            g.strokeLine(x, highY, x, lowY);

            boolean bull = c.close >= c.open;

            g.setFill(bull ? Color.LIMEGREEN : Color.RED);

            double bw = Math.max(2, candleWidth - 2);

            g.fillRect(
                    x - bw / 2,
                    Math.min(openY, closeY),
                    bw,
                    Math.max(1.5, Math.abs(openY - closeY))
            );
        }

        switch (hoverZone) {
            case Y_AXIS -> canvas.setCursor(javafx.scene.Cursor.N_RESIZE);
            case CHART -> canvas.setCursor(javafx.scene.Cursor.CROSSHAIR);
            case X_AXIS -> canvas.setCursor(javafx.scene.Cursor.E_RESIZE);
        }
    }

    // -------------------------
    // ZONE DETECTION
    // -------------------------
    private void updateHoverZone(double x, double y) {

        double W = canvas.getWidth();
        double H = canvas.getHeight();

        if (x < PAD_LEFT) {
            hoverZone = HoverZone.Y_AXIS;
        } else if (y > PAD_TOP + (H - PAD_TOP - PAD_BOTTOM)) {
            hoverZone = HoverZone.X_AXIS;
        } else {
            hoverZone = HoverZone.CHART;
        }
    }

    // -------------------------
    // HELPERS
    // -------------------------
    private double priceToY(double price, double min, double range, double height) {
        return height - ((price - min) / range) * height;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}