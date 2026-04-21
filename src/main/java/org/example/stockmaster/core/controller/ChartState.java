package org.example.stockmaster.core.controller;          // Paquete donde se encuentra la clase
import org.example.stockmaster.core.model.Candle;        // Importa el modelo de vela (OHLC)
import java.util.List;                                  // Lista genérica para manejar candles

// 1. Calcula cuántas velas caben en el ancho del gráfico
// 2. Determina desde qué vela empezar (aplicando scroll)
// 3. Determina hasta qué vela mostrar (limitado por tamaño total)
// 4. Recorre velas visibles y obtiene precio mínimo y máximo
// 5. Añade margen visual (8%) al rango de precios
// 6. Calcula el punto central entre min y max
// 7. Ajusta el rango de precios según el zoom vertical
// 8. Calcula el rango final (priceMax - priceMin)
// 9. Construye el objeto final con todos los datos listos


public class ChartState {                              // Clase que gestiona el estado del gráfico
    public double candleWidth = 8;                    // Ancho de cada vela en píxeles
    public double scrollX = 0;                       // Desplazamiento horizontal del gráfico
    public double priceZoom = 1.0;                  // Zoom vertical del precio (1 = normal)
                                                   //
    public static class View {                    // Representa lo que se va a renderizar en pantalla
        public int startIndex;                   // Primera vela visible
        public int endIndex;                    // Última vela visible
        public double priceMin;                // Precio mínimo visible
        public double priceMax;               // Precio máximo visible
        public double priceRange;            // Rango total de precios visibles
    }

    public View build(List<Candle> candles, double chartWidth, double chartHeight) { // Genera la vista del gráfico
        View v = new View();                                                        // Crea objeto de salida
        int maxVisible = (int)(chartWidth / candleWidth) + 2;                      // Número de velas que caben en pantalla
        v.startIndex = Math.max(                                                  // Calcula inicio del rango visible
                0,                                                               // Nunca puede ser menor que 0
                candles.size() - maxVisible + (int)(scrollX / candleWidth)      // Ajuste por scroll
        );

        v.endIndex = Math.min(                                       // Calcula fin del rango visible
                candles.size(),                                     // Nunca superar tamaño total
                v.startIndex + maxVisible + 2                      // Añade margen extra
        );

        double max = Double.MIN_VALUE;                          // Inicializa máximo
        double min = Double.MAX_VALUE;                         // Inicializa mínimo

        for (int i = v.startIndex; i < v.endIndex; i++) {    // Recorre solo velas visibles
            Candle c = candles.get(i);                      // Obtiene vela actual
            max = Math.max(max, c.high);                   // Actualiza máximo
            min = Math.min(min, c.low);                   // Actualiza mínimo
        }

        double range = (max - min);                    // Calcula rango de precios
        double padding = range * 0.08;                // 8% de margen visual

        max += padding;                             // Añade margen superior
        min -= padding;                            // Añade margen inferior

        double center = (max + min) / 2;                 // Calcula centro del rango
        double adjustedRange = (max - min) / priceZoom; // Aplica zoom vertical

        v.priceMin = center - adjustedRange / 2;       // Ajusta mínimo con zoom centrado
        v.priceMax = center + adjustedRange / 2;      // Ajusta máximo con zoom centrado

        v.priceRange = v.priceMax - v.priceMin;     // Guarda rango final

        return v;                                 // Devuelve la vista lista para renderizar
    }
}