package org.example.paneljavafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import org.example.paneljavafx.data.DataStore;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.simulation.MarketEngine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FundViewController {

    // FXML
    @FXML private Label fundName, fundTicker, fundIsin, fundType, fundCategory;
    @FXML private Label fundValue, fundChange, fundDailyReturn, totalAUM, exposureRisk;
    @FXML private ProgressBar performanceBar;
    @FXML private ListView<String> topAssetsList;

    // State
    private Fund currentFund;
    private Runnable tickListener;
    private List<FundPosition> fundPositions;
    private double previousFundValue = 0;
    private final DecimalFormat DF = new DecimalFormat("#,###.##");
    private final ObservableList<String> topAssets = FXCollections.observableArrayList();

    public void loadFund(Fund fund, List<FundPosition> positions) {
        System.out.println("💰 === FUND VIEW: " + fund.getNombre() + " (" + fund.getIdFondo() + ") ===");

        this.currentFund = fund;
        this.fundPositions = positions != null ? positions : new ArrayList<>();

        System.out.println("📊 Posiciones recibidas: " + fundPositions.size());

        renderStaticInfo();
        setupTopAssets();

        tickListener = this::recalculateFundValue;
        MarketClock.getInstance().addListener(tickListener);

        recalculateFundValue();  // Inicial
    }

    private void recalculateFundValue() {
        System.out.println("\n🔄 === RECALCULANDO " + currentFund.getIdFondo() + " ===");

        double totalValue = 0;
        double totalChange = 0;
        List<String> impactReport = new ArrayList<>();

        // 1. Obtener TOP MOVERS (ordenados por impacto absoluto del retorno diario)
        List<FundPosition> sortedPositions = fundPositions.stream()
                .filter(FundPosition::isValid)
                .sorted(Comparator.comparingDouble((FundPosition pos) -> Math.abs(pos.getDailyReturn())).reversed())
                .limit(5)
                .collect(Collectors.toList());

        for (FundPosition pos : sortedPositions) {
            double positionValue = pos.getValorPosicion();
            double dailyReturn = pos.getDailyReturn();
            double returnPct = pos.getReturnPct();

            totalValue += positionValue;
            totalChange += dailyReturn;

            // ✅ CORRECCIÓN: El método generado por Lombok para 'precioActual' es 'getPrecioActual()'
            double currentPrice = 0;
            MarketEngine engine = DataStore.engines.get(pos.getIdAsset());
            if (engine != null) {
                currentPrice = engine.getLastPrice();
            }

            System.out.printf("📈 %s | %.0f€ | Δ%.0f€ (%.1f%%) | Precio: %.2f€%n",
                    pos.getIdAsset(), positionValue, dailyReturn, returnPct, currentPrice);

            // 2. Añadir al reporte de impacto incluyendo el precio actual
            impactReport.add(String.format("📊 %s → %s%% (Δ%s€) [P: %s€]",
                    pos.getIdAsset(),
                    DF.format(returnPct),
                    DF.format(dailyReturn),
                    DF.format(currentPrice)));
        }

        double changePct = previousFundValue > 0 ?
                (totalValue - previousFundValue) / previousFundValue * 100 : 0;

        System.out.printf("💎 TOTAL: %s€ | Δ%+.2f%% | Cambio absoluto: %.0f€%n",
                DF.format(totalValue), changePct, totalChange);

        double finalTotalValue = totalValue;
        double finalTotalChange = totalChange;
        Platform.runLater(() -> updateUI(finalTotalValue, changePct, finalTotalChange, impactReport));
        previousFundValue = totalValue;
    }




    // ✅ ELIMINADO - Ya no necesario, FundPosition lo hace
    // private double getAssetCurrentPrice(String assetId) { ... }

    private void updateUI(double totalValue, double changePct, double totalChange, List<String> topMovers) {
        fundValue.setText(DF.format(totalValue) + " €");
        fundChange.setText(String.format("%+.2f%%", changePct));
        fundChange.setTextFill(changePct >= 0 ? Color.LIMEGREEN : Color.RED);

        fundDailyReturn.setText(String.format("%+.0f €", totalChange));
        fundDailyReturn.setTextFill(totalChange >= 0 ? Color.LIMEGREEN : Color.RED);

        // Barra de performance mejorada
        double normalizedChange = Math.max(-1, Math.min(1, changePct / 15.0));
        performanceBar.setProgress(normalizedChange);
        performanceBar.setStyle(changePct >= 0 ?
                "-fx-accent: limegreen;" : "-fx-accent: orangered;");

        totalAUM.setText(DF.format(totalValue) + " AUM");

        // ✅ TOP ASSETS ORDENADOS POR IMPACTO
        topAssets.clear();
        topAssets.addAll(topMovers);
        topAssetsList.scrollTo(0);
    }

    private void renderStaticInfo() {
        System.out.println("📋 === INFO ESTÁTICA ===");
        System.out.println("Nombre: " + currentFund.getNombre());
        System.out.println("Ticker: " + currentFund.getIdFondo());
        System.out.println("Tipo: " + currentFund.getTipo());
        System.out.println("Categoría: " + currentFund.getCategoria());
        System.out.println("======================\n");

        fundName.setText(currentFund.getNombre());
        fundTicker.setText(currentFund.getIdFondo());
        fundIsin.setText(currentFund.getCodigoIsin() != null ? currentFund.getCodigoIsin() : "N/A");
        fundType.setText(currentFund.getTipo());
        fundCategory.setText(currentFund.getCategoria());

        double risk = calculateExposureRisk();
        exposureRisk.setText(String.format("Riesgo: %.1f/10", risk));
    }

    private double calculateExposureRisk() {
        System.out.println("⚡ === CALCULANDO RIESGO FONDO " + currentFund.getIdFondo() + " ===");

        if (fundPositions == null || fundPositions.isEmpty()) {
            System.out.println("⚠️ Sin posiciones");
            return 5.0;
        }

        double totalRisk = 0;
        int validPositions = 0;

        for (FundPosition pos : fundPositions) {
            if (!pos.isValid()) continue;

            MarketEngine engine = DataStore.engines.get(pos.getIdAsset());
            if (engine != null && engine.getAsset() != null) {
                Asset asset = engine.getAsset();

                // ✅ RIESGO Mapeo mejorado
                double assetRisk = switch (asset.getRisk().toLowerCase()) {
                    case "high", "alto", "agresivo", "extreme" -> 9.5;
                    case "medium", "medio", "moderado" -> 6.0;
                    case "low", "bajo", "conservador" -> 2.5;
                    case "very high", "muy alto" -> 10.0;
                    case "very low", "muy bajo" -> 1.0;
                    default -> {
                        System.out.println("⚠️ Riesgo desconocido: " + asset.getRisk() + " → 5.0");
                        yield 5.0;
                    }
                };

                double positionWeight = pos.getPesoPorcentual() / 100.0;
                double weightedRisk = assetRisk * positionWeight;

                totalRisk += weightedRisk;
                validPositions++;

                System.out.printf("📊 %-12s (%.1f%%) | %s(%.1f) | Ponderado: %.3f%n",
                        pos.getIdAsset(), pos.getPesoPorcentual(),
                        asset.getRisk(), assetRisk, weightedRisk);
            } else {
                System.out.println("❌ Asset no encontrado: " + pos.getIdAsset());
            }
        }

        double avgRisk = validPositions > 0 ? totalRisk / validPositions : 5.0;
        System.out.printf("🎯 RIESGO FINAL FONDO: %.2f/10 (%d/%d posiciones)%n",
                avgRisk, validPositions, fundPositions.size());
        System.out.println("========================================\n");

        return Math.min(10.0, Math.max(0.0, avgRisk)); // Clamp 0-10
    }

    private void setupTopAssets() {
        topAssetsList.setItems(topAssets);
        topAssetsList.setCellFactory(lv -> new TopAssetCell());
    }

    public void onClose() {
        if (tickListener != null) {
            MarketClock.getInstance().removeListener(tickListener);
            tickListener = null;
            System.out.println("🔴 === FUND VIEW CERRADO: " + currentFund.getIdFondo() + " ===");
        }
    }

    private static class TopAssetCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }
            setText(item);
            // Estilo mejorado
            setStyle("-fx-font-size: 13px; -fx-padding: 8 12; -fx-background-color: linear-gradient(to right, transparent, rgba(0,0,0,0.05));");
        }
    }
}