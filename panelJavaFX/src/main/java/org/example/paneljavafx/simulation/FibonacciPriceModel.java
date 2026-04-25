package org.example.paneljavafx.simulation; // Paquete de simulación del mercado
import lombok.Getter;                           // Genera getter automático para price

import java.util.HashMap;         // Estructura para cache (memoización)
import java.util.Map;            // Interfaz Map
import java.util.Random;        // Generador de aleatoriedad

public class FibonacciPriceModel {                                   // Modelo que simula precio basado en Fibonacci
    private static final Map<Integer, Long> memo = new HashMap<>(); // Cache Fibonacci (evita recálculos)
    @Getter private double price;                                  // Precio actual del activo simulado
    private int fibIndex = 1;                                     // Índice actual de Fibonacci
    private static final int MAX_FIB_INDEX = 15;                 // Límite del ciclo Fibonacci
    private final double baseUnit;                              // Unidad base para escalar movimientos de precio
    private final Random rand = new Random();                  // Generador de aleatoriedad (dirección del mercado)

    public FibonacciPriceModel(double initialPrice, double baseUnit) { // Constructor del modelo
        this.price = initialPrice;                                    // Inicializa el precio
        this.baseUnit = baseUnit;                                    // Define escala de movimiento
    }

    public static long fib(int n) {                          // Calcula Fibonacci recursivo con memoización
        if (n <= 1) return n;                               // Casos base (0, 1)
        if (memo.containsKey(n)) return memo.get(n);       // Usa cache si ya fue calculado
        long result = fib(n - 1) + fib(n - 2);            // Fórmula Fibonacci clásica
        memo.put(n, result);                             // Guarda resultado en cache
        return result;                                  // Devuelve valor calculado
    }

    public double tick() {
        long fibValue = fib(fibIndex);

        // normalizar contra el máximo posible → siempre entre 0 y 1
        double normalized = (double) fibValue / fib(MAX_FIB_INDEX);

        // volatilidad controlada — baseUnit ya es pequeño (ej: 0.18)
        // multiplicamos por un factor fino para que el movimiento sea suave
        double volatility = baseUnit * normalized * 0.002;

        double direction = (rand.nextDouble() * 2 - 1); // -1 a +1

        price *= (1.0 + direction * volatility);

        fibIndex++;
        if (fibIndex > MAX_FIB_INDEX) fibIndex = 1;

        return price;
    }
}