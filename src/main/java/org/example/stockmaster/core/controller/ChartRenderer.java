package org.example.stockmaster.core.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.stockmaster.core.model.Candle;

import java.util.List;

public class ChartRenderer {                            // Clase encargada de dibujar el gráfico

    public void draw(GraphicsContext g,               // Contexto gráfico de JavaFX
                     List<Candle> candles, // Lista de velas
                     ChartState.View v, // Vista calculada (qué mostrar)
                     double canvasW, // Ancho del canvas
                     double canvasH, // Alto del canvas
                     double padLeft, // Padding izquierdo
                     double padTop, // Padding superior
                     double candleWidth) { // Ancho de cada vela

        double cH = canvasH - padTop; // Alto útil del gráfico (restando padding superior)

        for (int i = v.startIndex; i < v.endIndex; i++) { // Recorre solo velas visibles

            Candle c = candles.get(i); // Obtiene vela actual

            double x = padLeft + (i - v.startIndex) * candleWidth; // Calcula posición X de la vela

            double openY  = priceToY(c.open,  v, cH, padTop); // Convierte precio open a coordenada Y
            double closeY = priceToY(c.close, v, cH, padTop); // Convierte precio close a coordenada Y
            double highY  = priceToY(c.high,  v, cH, padTop); // Convierte precio high a coordenada Y
            double lowY   = priceToY(c.low,   v, cH, padTop); // Convierte precio low a coordenada Y

            g.setStroke(Color.GRAY); // Define color de la mecha
            g.strokeLine(x, highY, x, lowY); // Dibuja línea vertical (high → low)

            g.setFill(c.close >= c.open ? Color.LIMEGREEN : Color.RED); // Color verde si sube, rojo si baja

            double bw = Math.max(2, candleWidth - 2); // Calcula ancho del cuerpo (mínimo 2px)

            g.fillRect( // Dibuja el cuerpo de la vela
                    x - bw / 2, // Centra el cuerpo en X
                    Math.min(openY, closeY), // Parte superior del cuerpo
                    bw, // Ancho del cuerpo
                    Math.abs(openY - closeY) // Altura del cuerpo
            );
        }
    }

    private double priceToY(double price, ChartState.View v, double height, double padTop) { // Convierte precio a coordenada Y
        return padTop + height - ((price - v.priceMin) / v.priceRange) * height; // Mapea precio dentro del rango visible a píxeles
    }
}