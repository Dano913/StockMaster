package org.example.paneljavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FundAssetPosition {

    private String idFundPosition;
    private String idFund;
    private String idAsset;

    private String currency = "EUR";
    private String addedRisk = "0";

    private double portfolioWeight;
    private double investedValue;
    private double quantity;

    private LocalDate openedDate;
    private LocalDate closedDate;

    public boolean isValid() {
        return idFund != null
                && idAsset != null
                && portfolioWeight > 0
                && quantity > 0
                && investedValue > 0;
    }

    @Override
    public String toString() {
        return String.format(
                "%s → %s (%.1f%% | %.0f uds | %.0f€)",
                idFund,
                idAsset,
                portfolioWeight,
                quantity,
                investedValue
        );
    }
}