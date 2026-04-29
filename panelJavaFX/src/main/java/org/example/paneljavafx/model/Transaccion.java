package org.example.paneljavafx.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaccion {
    private Integer idTransaccion;
    private Integer idPosicion;
    private String tipo;
    private double importe;
    private LocalDateTime fecha;
}