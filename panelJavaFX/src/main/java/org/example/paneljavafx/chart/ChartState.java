package org.example.paneljavafx.chart;

import org.example.paneljavafx.model.Candle;

import java.util.List;

public class ChartState {

    public double candleWidth = 8;
    public double scrollX    = 0;
    public double priceZoom  = 1.0;

    public static class View {
        public int    startIndex;
        public int    endIndex;
        public double priceMin;
        public double priceMax;
        public double priceRange;
    }

    public View build(List<Candle> candles, double chartWidth, double chartHeight) {

        View v = new View();

        int maxVisible = (int)(chartWidth / candleWidth) + 2;

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

        // guard: si no hay velas válidas en el rango
        if (max == Double.MIN_VALUE || min == Double.MAX_VALUE || max == min) {
            v.priceMin   = 0;
            v.priceMax   = 1;
            v.priceRange = 1;
            return v;
        }

        double range   = max - min;
        double padding = range * 0.08;

        max += padding;
        min -= padding;

        double center        = (max + min) / 2.0;
        double adjustedRange = (max - min) / priceZoom;

        v.priceMin   = center - adjustedRange / 2.0;
        v.priceMax   = center + adjustedRange / 2.0;
        v.priceRange = v.priceMax - v.priceMin;

        return v;
    }
}