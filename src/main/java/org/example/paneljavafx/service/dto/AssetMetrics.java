package org.example.paneljavafx.service.dto;

public class AssetMetrics {

    private double totalExposure;   // € total expuesto
    private double exposureRatio;   // 0..1 para ProgressBar
    private long fundsExposed;      // nº de fondos
    private double globalWeight;    // peso relativo

    public AssetMetrics(double totalExposure,
                        double exposureRatio,
                        long fundsExposed,
                        double globalWeight) {
        this.totalExposure = totalExposure;
        this.exposureRatio = exposureRatio;
        this.fundsExposed = fundsExposed;
        this.globalWeight = globalWeight;
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
}