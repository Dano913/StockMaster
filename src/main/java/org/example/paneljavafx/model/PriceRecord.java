package org.example.paneljavafx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceRecord {

    @JsonProperty("id_precio_activo")
    private String idPrecioActivo;

    @JsonProperty("id_activo")
    private String idActivo;

    @JsonProperty("precio_apertura")
    private double precioApertura;

    @JsonProperty("precio_cierre")
    private double precioCierre;

    @JsonProperty("precio_maximo")
    private double precioMaximo;

    @JsonProperty("precio_minimo")
    private double precioMinimo;

    @JsonProperty("temporalidad")
    private String temporalidad;

    @JsonProperty("fecha_creacion")
    private String fechaCreacion;

    @JsonProperty("volumen")
    private int volumen;
}