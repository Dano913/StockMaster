package org.example.paneljavafx.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Asset {

    private String id;
    private String ticker;
    private String name;
    private String isinCode;
    private String type;
    private String sector;

    private String risk;
    private String liquidity;
    private String change;

    private double initialPrice;
    private double marketCap;
    private double volatility;
}



