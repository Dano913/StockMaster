package org.example.paneljavafx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fund {

    @JsonProperty("id_fondo")
    private String idFondo;  // ← String (ej: "FND-001")

    @JsonProperty("id_empresa")
    private String idEmpresa;  // ← String (ej: "EMP-5001")

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("codigo_isin")
    private String codigoIsin;

    @JsonProperty("tipo")
    private String tipo;

    @JsonProperty("categoria")
    private String categoria;

    @JsonProperty("moneda_base")
    private String monedaBase;

    @JsonProperty("fecha_creacion")
    private String fechaCreacion;
}