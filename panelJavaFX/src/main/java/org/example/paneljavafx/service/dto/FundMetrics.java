package org.example.paneljavafx.service.dto;

import java.util.List;

public class FundMetrics {
    private double totalValue;
    private double totalChange;
    private double changePct;
    private List<String> topMovers;

    public FundMetrics(double totalValue, double totalChange, double changePct, List<String> topMovers) {
        this.totalValue = totalValue;
        this.totalChange = totalChange;
        this.changePct = changePct;
        this.topMovers = topMovers;
    }

    public double getTotalValue() { return totalValue; }
    public double getTotalChange() { return totalChange; }
    public double getChangePct() { return changePct; }
    public List<String> getTopMovers() { return topMovers; }
}