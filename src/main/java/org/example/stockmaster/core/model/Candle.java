package org.example.stockmaster.core.model;

public class Candle {

    public double open;
    public double high;
    public double low;
    public double close;

    public Candle(double price) {
        this.open = price;
        this.high = price;
        this.low = price;
        this.close = price;
    }

    public void update(double price) {
        close = price;
        if (price > high) high = price;
        if (price < low) low = price;
    }
}