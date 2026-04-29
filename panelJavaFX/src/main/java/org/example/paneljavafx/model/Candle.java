package org.example.paneljavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candle {

    private double open;
    private double high;
    private double low;
    private double close;
    private long timestamp;

    public void update(double price) {
        this.close = price;
        if (price > high) high = price;
        if (price < low) low = price;
    }
}