package org.example.paneljavafx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Asset {

    // -------------------------
    // IDENTIDAD
    // -------------------------
    @Getter
    @JsonProperty("id_activo")
    private String id;

    @Getter
    @JsonProperty("precio_inicial")
    private double initialPrice;

    @Getter
    private String ticker;

    @Getter
    @JsonProperty("nombre")
    private String name;

    @Getter
    private String isin;

    // -------------------------
    // MERCADO
    // -------------------------
    @Getter
    @JsonProperty("tipo")
    private String type;

    @Getter
    private String sector;

    // -------------------------
    // MÉTRICAS
    // -------------------------
    @Getter
    @JsonProperty("market_cap")
    private double marketCap;

    @Getter
    @JsonProperty("volatilidad")
    private double volatility;

    @Getter
    @JsonProperty("riesgo")
    private String risk;

    @Getter
    @JsonProperty("liquidez")
    private String liquidity;

    @Getter
    @JsonProperty("varaicion")
    private String change;

    // -------------------------
    // CONSTRUCTOR VACÍO (OBLIGATORIO JACKSON)
    // -------------------------
    public Asset() {
    }

}