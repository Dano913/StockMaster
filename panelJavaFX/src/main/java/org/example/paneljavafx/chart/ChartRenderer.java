package org.example.paneljavafx.chart;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.paneljavafx.model.Candle;

import java.util.List;

public class ChartRenderer {

    public void draw(GraphicsContext g,
                     List<Candle> candles,
                     ChartState.View v,
                     double canvasW,
                     double canvasH,
                     double padLeft,
                     double padTop,
                     double candleWidth) {

        double cH = canvasH - padTop;

        for (int i = v.startIndex; i < v.endIndex; i++) {

            Candle c = candles.get(i);

            double x      = padLeft + (i - v.startIndex) * candleWidth;
            double openY  = priceToY(c.getOpen(),  v, cH, padTop);
            double closeY = priceToY(c.getClose(), v, cH, padTop);
            double highY  = priceToY(c.getHigh(),  v, cH, padTop);
            double lowY   = priceToY(c.getLow(),   v, cH, padTop);

            // mecha
            g.setStroke(Color.GRAY);
            g.strokeLine(x, highY, x, lowY);

            // cuerpo
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

    private double priceToY(double price, ChartState.View v, double height, double padTop) {
        return padTop + height - ((price - v.priceMin) / v.priceRange) * height;
    }
}