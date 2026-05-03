package org.example.paneljavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Candle {

    private String assetId;
    private double open;
    private double high;
    private double low;
    private double close;
    private long timestamp;

    public void update(double newClose) {
        this.close = newClose;
        if (newClose > this.high) this.high = newClose;
        if (newClose < this.low)  this.low  = newClose;
    }
}