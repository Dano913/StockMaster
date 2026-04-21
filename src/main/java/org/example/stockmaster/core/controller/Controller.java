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
    // MARKET
    // -------------------------
    private Asset activo = new Asset(30000, 0.003, 0.02, 1.5);
    private ArrayList<Candle> candles = new ArrayList<>();
    private Candle currentCandle;

    private double candleWidth = 8;
    private int step = 0;
    private int timeframeSteps = 10;
    private int counter = 0;
    private static final int SECONDS_PER_CANDLE = 1;

    // -------------------------
    // CAMERA / VIEW STATE
    // -------------------------
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

        // Detectar zona del mouse
        canvas.setOnMouseMoved(e -> updateHoverZone(e.getX(), e.getY()));

        // Scroll = zoom contextual
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

        // Drag horizontal (pan)
        canvas.setOnMousePressed(e -> {
            dragStartX = e.getX();
            dragStartScrollX = scrollX;
        });

        canvas.setOnMouseDragged(e -> {
            updateHoverZone(e.getX(), e.getY());
            scrollX = dragStartScrollX + (e.getX() - dragStartX);
        });

        // Reset zoom Y
        canvas.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                yManual = false;
                priceZoom = 1.0;
                priceOffset = 0;
            }
        });

        // Loop principal
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                actualizar();
                dibujar();

                counter++;

                if (counter % 40 == 0) activo.cambiarTendencia();
                if (counter % 200 == 0) DataStore.guardar(candles);
            }
        };

        timer.start();
    }

    // -------------------------
    // MARKET UPDATE
    // -------------------------
    private void actualizar() {
        double price = activo.tick();

        if (step == 0) currentCandle = new Candle(price);

        currentCandle.update(price);
        step++;

        if (step >= timeframeSteps) {
            candles.add(currentCandle);
            step = 0;
        }
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

        // background
        g.clearRect(0, 0, W, H);
        g.setFill(Color.web("#111111"));
        g.fillRect(0, 0, W, H);

        if (candles.size() < 2) return;

        // -------------------------
        // VISIBLE RANGE
        // -------------------------
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

        double priceMin, priceMax;

        if (yManual) {
            double center = (rawMax + rawMin) / 2 + priceOffset;
            double halfSpan = (rawRange / 2 + padding) / priceZoom;
            priceMin = center - halfSpan;
            priceMax = center + halfSpan;
        } else {
            priceMin = rawMin;
            priceMax = rawMax;
        }

        double priceRange = priceMax - priceMin;

        // -------------------------
        // GRID Y
        // -------------------------
        double[] yTicks = niceTicks(priceMin, priceMax, 7);

        g.setStroke(Color.web("#2a2a2a"));
        g.setFill(Color.web("#888888"));
        g.setFont(javafx.scene.text.Font.font("Monospaced", 10));

        for (double tick : yTicks) {
            double y = priceToY(tick, priceMin, priceRange, cH);
            double ry = PAD_TOP + y;

            if (ry < PAD_TOP || ry > PAD_TOP + cH) continue;

            // línea grid
            g.setStroke(Color.web("#2a2a2a"));
            g.strokeLine(PAD_LEFT, ry, PAD_LEFT + cW, ry);

            // precio alineado a la derecha del eje
            g.setFill(Color.web("#aaaaaa"));
            g.fillText(
                    String.format("%.2f", tick),
                    PAD_LEFT - 8,   // más pegado al borde
                    ry + 3
            );
        }

        // -------------------------
        // GRID X
        // -------------------------
        int xStep = Math.max(1, (int)(50 / candleWidth));

        for (int i = startIdx; i < endIdx; i += xStep) {

            double x = PAD_LEFT + (i - startIdx) * candleWidth;

            int seconds = i * SECONDS_PER_CANDLE;

            int minutes = seconds / 60;
            int secs = seconds % 60;

            String label = minutes + ":" + String.format("%02d", secs);

            g.setFill(Color.web("#777777"));
            g.fillText(label, x - 10, PAD_TOP + cH + 18);

            g.setStroke(Color.web("#222222"));
            g.strokeLine(x, PAD_TOP, x, PAD_TOP + cH);
        }

        // -------------------------
        // AXIS
        // -------------------------
        g.setStroke(Color.web("#555555"));
        g.strokeLine(PAD_LEFT, PAD_TOP, PAD_LEFT, PAD_TOP + cH);
        g.strokeLine(PAD_LEFT, PAD_TOP + cH, PAD_LEFT + cW, PAD_TOP + cH);

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

        // -------------------------
        // CURSOR FEEDBACK
        // -------------------------
        switch (hoverZone) {
            case Y_AXIS -> canvas.setCursor(javafx.scene.Cursor.N_RESIZE);
            case CHART -> canvas.setCursor(javafx.scene.Cursor.CROSSHAIR);
            case X_AXIS -> canvas.setCursor(javafx.scene.Cursor.E_RESIZE);
        }

        // -------------------------
        // CLIP
        // -------------------------
        g.setFill(Color.web("#111111"));
        g.fillRect(0, 0, PAD_LEFT, H);
        g.fillRect(PAD_LEFT + cW, 0, W - PAD_LEFT - cW, H);
        g.fillRect(0, PAD_TOP + cH, W, PAD_BOTTOM);
        g.fillRect(0, 0, W, PAD_TOP);
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

    private double[] niceTicks(double min, double max, int count) {
        double raw = (max - min) / count;
        double mag = Math.pow(10, Math.floor(Math.log10(raw)));

        double[] candidates = {1, 2, 2.5, 5, 10};

        double step = mag;

        for (double c : candidates) {
            if (c * mag >= raw) {
                step = c * mag;
                break;
            }
        }

        double first = Math.ceil(min / step) * step;

        ArrayList<Double> ticks = new ArrayList<>();

        for (double v = first; v <= max; v += step) {
            ticks.add(v);
        }

        return ticks.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    // -------------------------
    // CONTROLS
    // -------------------------
    public void zoomIn()  { candleWidth = Math.min(candleWidth + 1, 30); }
    public void zoomOut() { candleWidth = Math.max(candleWidth - 1, 3); }
    public void save()    { DataStore.guardar(candles); }
}