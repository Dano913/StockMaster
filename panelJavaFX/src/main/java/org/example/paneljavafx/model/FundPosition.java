package org.example.paneljavafx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundPosition {

    @JsonProperty("id_posicion")
    private String idFundPosition;

    @JsonProperty("id_fund")
    private String idFund;

    @JsonProperty("id_asset")
    private String idAsset;

    @JsonProperty("peso_porcentual")
    private double pesoPorcentual;

    @JsonProperty("invested_value")
    private double investedValue;

    @JsonProperty("quantity")
    private double quantity;

    @JsonProperty("currency")
    private String currency = "EUR";

    @JsonProperty("added_risk")
    private String addedRisk = "0";

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("finish_date")
    private LocalDate finishDate;

    // =========================
    // UTILIDADES (solo estado)
    // =========================

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return startDate != null
                && (finishDate == null || finishDate.isAfter(now));
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