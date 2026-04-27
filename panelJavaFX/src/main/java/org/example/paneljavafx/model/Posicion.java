package org.example.paneljavafx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Posicion {
    @JsonProperty("id_fondo") private String idFondo;
    @JsonProperty("nombre_fondo") private String nombreFondo;
    private double cantidad;

    @JsonProperty("valor_actual")  // ← ¡FALTABA ESTO!
    private double valorActual;

    private List<Transaccion> transacciones;

    public Posicion() {}

    // Getters
    public String getIdFondo() { return idFondo; }
    public String getNombreFondo() { return nombreFondo; }
    public double getCantidad() { return cantidad; }
    public double getValorActual() { return valorActual; }
    public List<Transaccion> getTransacciones() { return transacciones; }

    // Setters
    public void setIdFondo(String idFondo) { this.idFondo = idFondo; }
    public void setNombreFondo(String nombreFondo) { this.nombreFondo = nombreFondo; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public void setValorActual(double valorActual) { this.valorActual = valorActual; }
    public void setTransacciones(List<Transaccion> transacciones) { this.transacciones = transacciones; }
}