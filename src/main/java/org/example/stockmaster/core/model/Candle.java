package org.example.stockmaster.core.model;

public class Candle {

    private double open;
    private double high;
    private double low;
    private double close;
    private long timestamp;

    // -------------------------
    // CONSTRUCTOR
    // -------------------------
    public Candle(double open, double high, double low, double close, long timestamp) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.timestamp = timestamp;
    }

    // -------------------------
    // UPDATE (opcional para MarketEngine)
    // -------------------------
    public void update(double price) {
        close = price;
        if (price > high) high = price;
        if (price < low) low = price;
    }

    // -------------------------
    // GETTERS
    // -------------------------
    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
    public long getTimestamp() { return timestamp; }
}