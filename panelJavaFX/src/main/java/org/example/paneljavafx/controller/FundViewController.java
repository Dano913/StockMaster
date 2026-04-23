package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.paneljavafx.model.Fund;

public class FundViewController {

    @FXML
    private Label fundName;

    @FXML
    private Label fundType;

    private Fund currentFund;

    public void loadFund(Fund fund) {
        this.currentFund = fund;

        render();
    }

    private void render() {
        if (currentFund == null) return;

        fundName.setText(currentFund.getNombre());
        fundType.setText(currentFund.getTipo());
    }
}