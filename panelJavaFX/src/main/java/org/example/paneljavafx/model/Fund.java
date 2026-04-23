package org.example.paneljavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fund {

    private String id_fondo;
    private String id_empresa;
    private String nombre;
    private String codigo_isin;
    private String tipo;
    private String categoria;
    private String moneda_base;
    private String fecha_creacion;
}