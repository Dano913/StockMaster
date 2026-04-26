package org.example.paneljavafx.service;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.paneljavafx.model.Candle;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.List;

public class ChartService {

    // ===== STATE =====
    private double candleWidth = 8;
    private double scrollX = 0;
    private double priceZoom = 1.0;

    private MarketEngine activeEngine;

    // ===== PUBLIC API =====
    public void setEngine(MarketEngine engine) {
        this.activeEngine = engine;
    }

    public void zoom(double delta) {
        priceZoom = clamp(priceZoom * (1 + delta * 0.002), 0.1, 20);
    }

    public void scroll(double delta) {
        scrollX += delta;
    }

    public void changeCandleWidth(double delta) {
        candleWidth = clamp(candleWidth - delta * 0.03, 3, 30);
    }

    public void drawFundEquity(GraphicsContext g,
                               List<Double> values,
                               double w,
                               double h) {

        if (values == null || values.size() < 2) return;

        double max = values.stream().mapToDouble(v -> v).max().orElse(1);
        double min = values.stream().mapToDouble(v -> v).min().orElse(0);

        double range = max - min;
        if (range == 0) range = 1;

        double stepX = w / (values.size() - 1);

        g.setStroke(Color.LIMEGREEN);

        for (int i = 1; i < values.size(); i++) {

            double x1 = (i - 1) * stepX;
            double y1 = h - ((values.get(i - 1) - min) / range) * h;

            double x2 = i * stepX;
            double y2 = h - ((values.get(i) - min) / range) * h;

            g.strokeLine(x1, y1, x2, y2);
        }
    }

    // ===== MAIN DRAW =====
    public void draw(GraphicsContext g, double W, double H) {

        g.clearRect(0, 0, W, H);
        g.setFill(Color.web("#111111"));
        g.fillRect(0, 0, W, H);

        var candles = activeEngine.getCandles();

        if (candles == null || candles.size() < 2) return;

        var view = buildView(candles, W, H);

        renderCandles(g, candles, view, W, H);
    }

    // ===== VIEWPORT =====
    private View buildView(List<Candle> candles, double W, double H) {

        View v = new View();

        int maxVisible = (int)(W / candleWidth) + 2;

        v.startIndex = Math.max(
                0,
                candles.size() - maxVisible + (int)(scrollX / candleWidth)
        );

        v.endIndex = Math.min(
                candles.size(),
                v.startIndex + maxVisible + 2
        );

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (int i = v.startIndex; i < v.endIndex; i++) {
            Candle c = candles.get(i);
            max = Math.max(max, c.getHigh());
            min = Math.min(min, c.getLow());
        }

        if (max == Double.MIN_VALUE || min == Double.MAX_VALUE || max == min) {
            v.priceMin = 0;
            v.priceRange = 1;
            return v;
        }

        double range = (max - min) / priceZoom;
        double center = (max + min) / 2.0;

        v.priceMin = center - range / 2.0;
        v.priceRange = range;

        return v;
    }

    // ===== RENDER =====
    private void renderCandles(GraphicsContext g,
                               List<Candle> candles,
                               View v,
                               double W,
                               double H) {

        double padLeft = 65;
        double padTop = 12;
        double cH = H - padTop;

        for (int i = v.startIndex; i < v.endIndex; i++) {

            Candle c = candles.get(i);

            double x = padLeft + (i - v.startIndex) * candleWidth;

            double openY  = priceToY(c.getOpen(), v, cH, padTop);
            double closeY = priceToY(c.getClose(), v, cH, padTop);
            double highY  = priceToY(c.getHigh(), v, cH, padTop);
            double lowY   = priceToY(c.getLow(), v, cH, padTop);

            g.setStroke(Color.GRAY);
            g.strokeLine(x, highY, x, lowY);

            boolean bullish = c.getClose() >= c.getOpen();
            g.setFill(bullish ? Color.LIMEGREEN : Color.RED);

            double bw = Math.max(2, candleWidth - 2);

            g.fillRect(
                    x - bw / 2,
                    Math.min(openY, closeY),
                    bw,
                    Math.abs(openY - closeY)
            );
        }
    }

    private double priceToY(double price, View v, double H, double padTop) {
        return padTop + H - ((price - v.priceMin) / v.priceRange) * H;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    // ===== INNER VIEW =====
    public static class View {
        int startIndex;
        int endIndex;
        double priceMin;
        double priceRange;
    }
}