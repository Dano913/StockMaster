package org.example.paneljavafx.service.dto;

public class AssetMetrics {

    private double totalExposure;   // € total expuesto
    private double exposureRatio;   // 0..1 para ProgressBar
    private long fundsExposed;      // nº de fondos
    private double globalWeight;    // peso relativo
    private double volatility;

    public AssetMetrics(double totalExposure,
                        double exposureRatio,
                        long fundsExposed,
                        double globalWeight,
                        double volatility) {
        this.totalExposure = totalExposure;
        this.exposureRatio = exposureRatio;
        this.fundsExposed = fundsExposed;
        this.globalWeight = globalWeight;
        this.volatility = volatility;
    }

    public double getTotalExposure() {
        return totalExposure;
    }

    public double getExposureRatio() {
        return exposureRatio;
    }

    public long getFundsExposed() {
        return fundsExposed;
    }

    public double getGlobalWeight() {
        return globalWeight;
    }

    public double getVolatility() { return volatility; }
}