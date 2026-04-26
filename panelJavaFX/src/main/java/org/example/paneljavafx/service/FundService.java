package org.example.paneljavafx.service;

import org.example.paneljavafx.data.DataStore;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.service.dto.FundMetrics;
import org.example.paneljavafx.simulation.MarketEngine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FundService {

    private final DecimalFormat DF = new DecimalFormat("#,###.##");

    // -------------------------
    // QUERY: POSICIONES POR FONDO
    // -------------------------
    public List<FundPosition> getPositionsByFund(List<FundPosition> all, String fundId) {
        if (all == null || fundId == null) return List.of();

        return all.stream()
                .filter(p -> fundId.equals(p.getIdFund()))
                .toList();
    }

    // -------------------------
    // MÉTRICAS DEL FONDO
    // -------------------------
    public FundMetrics calculateMetrics(
            Fund fund,
            List<FundPosition> positions,
            double previousValue
    ) {

        double totalValue = 0;
        double totalChange = 0;

        List<FundPosition> topPositions = positions.stream()
                .filter(FundPosition::isValid)
                .sorted(Comparator.comparingDouble(
                        (FundPosition p) -> Math.abs(p.getDailyReturn())
                ).reversed())
                .limit(5)
                .toList();

        List<String> topMovers = new ArrayList<>();

        for (FundPosition pos : topPositions) {

            double positionValue = pos.getValorPosicion();
            double dailyReturn = pos.getDailyReturn();
            double returnPct = pos.getReturnPct();

            totalValue += positionValue;
            totalChange += dailyReturn;

            double currentPrice = 0;
            MarketEngine engine = DataStore.engines.get(pos.getIdAsset());
            if (engine != null) {
                currentPrice = engine.getLastPrice();
            }

            topMovers.add(String.format(
                    "📊 %s → %s%% (Δ%s€) [P: %s€]",
                    pos.getIdAsset(),
                    DF.format(returnPct),
                    DF.format(dailyReturn),
                    DF.format(currentPrice)
            ));
        }

        double changePct = previousValue > 0
                ? (totalValue - previousValue) / previousValue * 100
                : 0;

        return new FundMetrics(totalValue, totalChange, changePct, topMovers);
    }
}