package org.example.paneljavafx.model;

public class Transaccion {
    private String tipo, fecha;
    private double importe;

    public Transaccion() {}

    public String getTipo() { return tipo; }
    public String getFecha() { return fecha; }
    public double getImporte() { return importe; }

    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setImporte(double importe) { this.importe = importe; }
}