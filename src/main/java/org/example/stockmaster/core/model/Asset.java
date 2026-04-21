package org.example.stockmaster.core.model;

import lombok.Getter;
import lombok.Setter;

public class Asset {

    // -------------------------
    // GETTERS
    // -------------------------
    // -------------------------
    // IDENTIDAD
    // -------------------------
    @Getter
    private int id;
    @Getter
    private String ticker;
    @Getter
    private String name;
    @Getter
    private String isin;

    // -------------------------
    // MERCADO
    // -------------------------
    @Getter
    private String type;
    @Getter
    private String sector;
    private double marketCap;

    // -------------------------
    // COMPORTAMIENTO
    // -------------------------
    @Setter
    private double volatility;

    @Setter
    private double initialPrice;

    @Getter
    @Setter
    private String risk;

    @Getter
    @Setter
    private String liquidity;

    // -------------------------
    // CONSTRUCTOR
    // -------------------------
    public Asset(int id,
                 String ticker,
                 String name,
                 String isin,
                 String type,
                 String sector,
                 double marketCap,
                 double initialPrice,
                 double volatility,
                 String risk,
                 String liquidity) {

        this.id = id;
        this.ticker = ticker;
        this.name = name;
        this.isin = isin;

        this.type = type;
        this.sector = sector;
        this.marketCap = marketCap;
        this.initialPrice = initialPrice;
        this.volatility = volatility;
        this.risk = risk;
        this.liquidity = liquidity;
    }

    public double getVolatility() { return volatility; }

    public double getInitialPrice() {
        return 0;
    }

}