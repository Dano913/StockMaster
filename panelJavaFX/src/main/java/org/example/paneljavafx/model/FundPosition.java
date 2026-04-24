package org.example.paneljavafx.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FundPosition {

    private String idFundPosition;
    private String idFund;
    private String idAsset;

    private double pesoPorcentual;
    private double investedValue;
    private double quantity;

    private String currency;
    private String addedRisk;

    private LocalDate startDate;
    private LocalDate finishDate;

    // -------------------------
    // CONSTRUCTOR
    // -------------------------

}