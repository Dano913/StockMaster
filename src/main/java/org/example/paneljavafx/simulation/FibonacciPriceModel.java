package org.example.paneljavafx.simulation;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FibonacciPriceModel {

    private static final Map<Integer, Long> memo = new HashMap<>();

    @Getter private double price;
    private int fibIndex = 1;
    private static final int MAX_FIB_INDEX = 15;
    private final double baseUnit;
    private final Random rand = new Random();

    public FibonacciPriceModel(double initialPrice, double baseUnit) {
        this.price = initialPrice;
        this.baseUnit = Math.max(baseUnit, 0.1); // mínimo 0.1 si viene 0 o null
    }

    public static long fib(int n) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);
        long result = fib(n - 1) + fib(n - 2);
        memo.put(n, result);
        return result;
    }

    public double tick() {
        long fibValue = fib(fibIndex);

        double normalized = (double) fibValue / fib(MAX_FIB_INDEX);

        // mínimo de volatility efectiva para que siempre haya movimiento
        double volatility = Math.max(baseUnit * normalized * 0.002, 0.0002);

        double direction = (rand.nextDouble() * 2 - 1); // -1 a +1

        price *= (1.0 + direction * volatility);

        fibIndex++;
        if (fibIndex > MAX_FIB_INDEX) fibIndex = 1;

        return price;
    }
}