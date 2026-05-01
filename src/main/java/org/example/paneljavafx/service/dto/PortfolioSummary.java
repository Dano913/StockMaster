package org.example.paneljavafx.service.dto;

public class PortfolioSummary {

    private final double total;
    private final double rentabilidad;

    public PortfolioSummary(double total, double rentabilidad) {
        this.total = total;
        this.rentabilidad = rentabilidad;
    }

    public double getTotal() {
        return total;
    }

    public double getRentabilidad() {
        return rentabilidad;
    }
}