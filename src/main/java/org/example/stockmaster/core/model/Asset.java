package org.example.stockmaster.core.model;

import java.util.Random;

public class Asset {

    double precio;
    double tendencia;
    double volatilidad;
    double factorFib;

    Random rand = new Random();

    public Asset(double precio, double tendencia, double volatilidad, double factorFib) {
        this.precio = precio;
        this.tendencia = tendencia;
        this.volatilidad = volatilidad;
        this.factorFib = factorFib;
    }

    public double tick() {

        double ruido = (rand.nextDouble() * 2 - 1) * volatilidad;
        double cambio = tendencia + ruido;

        cambio = Math.max(Math.min(cambio, 0.1), -0.1);

        precio = precio * (1 + cambio);

        return precio;
    }

    public void cambiarTendencia() {
        double fase = rand.nextDouble();

        if (fase < 0.33) {
            tendencia = 0.001;
            volatilidad = 0.03;
        } else if (fase < 0.66) {
            tendencia = 0;
            volatilidad = 0.01;
        } else {
            tendencia = -0.001;
            volatilidad = 0.02;
        }
    }
}