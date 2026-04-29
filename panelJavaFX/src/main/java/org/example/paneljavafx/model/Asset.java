package org.example.paneljavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    private String id;
    private double initialPrice;
    private String ticker;
    private String name;
    private String isin;
    private String type;
    private String sector;
    private double marketCap;
    private double volatility;
    private String risk;
    private String liquidity;
    private String change;
}