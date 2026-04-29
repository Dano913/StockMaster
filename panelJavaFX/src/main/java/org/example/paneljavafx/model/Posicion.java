package org.example.paneljavafx.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Posicion {
    private Integer idPosicion;
    private Integer clienteId;
    private String idFondo;
    private double cantidad;
    private double valorActual;
    private List<Transaccion> transacciones;
}