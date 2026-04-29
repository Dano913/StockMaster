package org.example.paneljavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundPosition {

    private String idFundPosition;
    private String idFund;
    private String idAsset;
    private double pesoPorcentual;
    private double investedValue;
    private double quantity;

    private String currency = "EUR";
    private String addedRisk = "0";

    private LocalDate startDate;
    private LocalDate finishDate;

    // =========================
    // UTILIDADES
    // =========================

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return startDate != null &&
                (finishDate == null || finishDate.isAfter(now));
    }

    public boolean isValid() {
        return idFund != null
                && idAsset != null
                && pesoPorcentual > 0
                && quantity > 0
                && investedValue > 0;
    }

    @Override
    public String toString() {
        return String.format(
                "%s → %s (%.1f%% | %.0f uds | %.0f€)",
                idFund,
                idAsset,
                pesoPorcentual,
                quantity,
                investedValue
        );
    }
}