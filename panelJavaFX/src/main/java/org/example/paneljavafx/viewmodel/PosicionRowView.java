package org.example.paneljavafx.viewmodel;

public class PosicionRowView {

    private final String fundName;
    private final double cantidad;
    private final double valorActual;
    private final double pnl;

    public PosicionRowView(String fundName, double cantidad, double valorActual, double pnl) {
        this.fundName = fundName;
        this.cantidad = cantidad;
        this.valorActual = valorActual;
        this.pnl = pnl;
    }

    public String getFundName() { return fundName; }
    public double getCantidad() { return cantidad; }
    public double getValorActual() { return valorActual; }
    public double getPnl() { return pnl; }
}