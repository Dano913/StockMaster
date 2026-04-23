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

    public double tick() {                                           // Ejecuta un paso de simulación de mercado
        long fibValue = fib(fibIndex);                              // Obtiene valor Fibonacci actual
        double increment = fibValue * baseUnit;                    // Convierte Fibonacci en movimiento de precio
        double direction = rand.nextDouble() < 0.52 ? 1.0 : -1.0; // Sesgo leve alcista (52% vs 48%)
        price += direction * increment;                          // Actualiza precio aplicando dirección
        fibIndex++;                                             // Avanza índice Fibonacci
        if (fibIndex > MAX_FIB_INDEX) {                        // Si llega al límite
            fibIndex = 1;                                     // Reinicia ciclo
        }
        return price;                                       // Devuelve nuevo precio
    }
}