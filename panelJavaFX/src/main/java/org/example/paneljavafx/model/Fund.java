package org.example.paneljavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fund {
    private String idFondo;
    private String idEmpresa;
    private String nombre;
    private String codigoIsin;
    private String tipo;
    private String categoria;
    private String monedaBase;
    private String fechaCreacion;
}