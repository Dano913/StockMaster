package org.example.paneljavafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import org.example.paneljavafx.common.TabDataReceiver;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.service.FundService;
import org.example.paneljavafx.service.dto.FundMetrics;
import org.example.paneljavafx.simulation.MarketClock;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FundViewController implements TabDataReceiver<Fund> {

    // =========================
    // FXML
    // =========================
    @FXML private Label fundName, fundTicker, fundIsin, fundType, fundCategory;
    @FXML private Label fundValue, fundChange, fundDailyReturn, totalAUM, exposureRisk;
    @FXML private ProgressBar performanceBar;
    @FXML private ListView<String> topAssetsList;

    // =========================
    // STATE
    // =========================
    private Fund currentFund;
    private List<FundPosition> fundPositions = new ArrayList<>();
    private Runnable tickListener;

    private double previousFundValue = 0;

    private final DecimalFormat DF = new DecimalFormat("#,###.##");
    private final ObservableList<String> topAssets = FXCollections.observableArrayList();

    private final FundService fundService = new FundService();

    // =========================
    // ENTRY POINT (TAB SYSTEM)
    // =========================
    @Override
    public void loadData(Fund fund) {

        this.currentFund = fund;

        renderStaticInfo();
        setupTopAssets();

        bindMarket();

        recalculate();
    }

    // =========================
    // OPTIONAL DATA (positions)
    // =========================
    public void loadPositions(List<FundPosition> positions) {
        this.fundPositions = (positions != null) ? positions : new ArrayList<>();
        recalculate();
    }

    // =========================
    // MARKET BINDING
    // =========================
    private void bindMarket() {

        tickListener = this::recalculate;
        MarketClock.getInstance().addListener(tickListener);
    }

    // =========================
    // CORE
    // =========================
    private void recalculate() {

        if (currentFund == null) return;

        FundMetrics metrics = fundService.calculateMetrics(
                currentFund,
                fundPositions,
                previousFundValue
        );

        previousFundValue = metrics.getTotalValue();

        Platform.runLater(() ->
                updateUI(
                        metrics.getTotalValue(),
                        metrics.getChangePct(),
                        metrics.getTotalChange(),
                        metrics.getTopMovers()
                )
        );
    }

    // =========================
    // UI UPDATE
    // =========================
    private void updateUI(double totalValue,
                          double changePct,
                          double totalChange,
                          List<String> topMovers) {

        fundValue.setText(DF.format(totalValue) + " €");

        fundChange.setText(String.format("%+.2f%%", changePct));
        fundChange.setTextFill(changePct >= 0 ? Color.LIMEGREEN : Color.RED);

        fundDailyReturn.setText(String.format("%+.0f €", totalChange));
        fundDailyReturn.setTextFill(totalChange >= 0 ? Color.LIMEGREEN : Color.RED);

        double normalized = Math.max(-1, Math.min(1, changePct / 15.0));
        performanceBar.setProgress(normalized);
        performanceBar.setStyle(changePct >= 0
                ? "-fx-accent: limegreen;"
                : "-fx-accent: orangered;");

        totalAUM.setText(DF.format(totalValue) + " AUM");

        topAssets.setAll(topMovers);
        topAssetsList.scrollTo(0);
    }

    // =========================
    // STATIC UI
    // =========================
    private void renderStaticInfo() {

        if (currentFund == null) return;

        fundName.setText(currentFund.getNombre());
        fundTicker.setText(currentFund.getIdFondo());
        fundIsin.setText(
                currentFund.getCodigoIsin() != null
                        ? currentFund.getCodigoIsin()
                        : "N/A"
        );

        fundType.setText(currentFund.getTipo());
        fundCategory.setText(currentFund.getCategoria());

        exposureRisk.setText("Riesgo: --");
    }

    // =========================
    // LIST
    // =========================
    private void setupTopAssets() {
        topAssetsList.setItems(topAssets);
    }

    // =========================
    // LIFECYCLE
    // =========================
    public void onClose() {

        if (tickListener != null) {
            MarketClock.getInstance().removeListener(tickListener);
            tickListener = null;
        }
    }
}