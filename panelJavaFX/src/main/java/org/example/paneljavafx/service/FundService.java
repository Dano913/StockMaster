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
    // NAV TOTAL DEL FONDO
    // Suma TODAS las posiciones válidas valoradas a precio de mercado actual.
    // A diferencia de calculateMetrics, no limita a top 5 ni filtra por
    // mayor movimiento — devuelve el valor liquidativo real completo.
    // -------------------------
    public double calculateTotalNAV(List<FundPosition> positions) {
        if (positions == null || positions.isEmpty()) return 0;

        return positions.stream()
                .filter(FundPosition::isValid)
                .mapToDouble(FundPosition::getValorPosicion)
                .sum();
    }

    // -------------------------
    // NAV + RENTABILIDAD DE UNA POSICIÓN
    // Devuelve par [valorActual, returnPct] para cada posición.
    // Útil para alimentar tablas con valor real de cada asset en el fondo.
    // -------------------------
    public record PositionSummary(
            String idAsset,
            double valorActual,
            double investedValue,
            double returnPct,
            double dailyReturn,
            double currentPrice,
            double pesoPorcentual
    ) {}

    public List<PositionSummary> getSummaryByFund(List<FundPosition> positions) {
        if (positions == null) return List.of();

        double navTotal = calculateTotalNAV(positions);

        return positions.stream()
                .filter(FundPosition::isValid)
                .map(pos -> {
                    MarketEngine engine = DataStore.engines.get(pos.getIdAsset());
                    double currentPrice = engine != null ? engine.getLastPrice() : 0;

                    double valorActual  = pos.getValorPosicion();
                    double invested     = pos.getInvestedValue();
                    double returnPct    = pos.getReturnPct();
                    double dailyReturn  = pos.getDailyReturn();

                    // Peso real de esta posición sobre el NAV total del fondo
                    double pesoReal = navTotal > 0 ? (valorActual / navTotal) * 100 : 0;

                    return new PositionSummary(
                            pos.getIdAsset(),
                            valorActual,
                            invested,
                            returnPct,
                            dailyReturn,
                            currentPrice,
                            pesoReal
                    );
                })
                .sorted(Comparator.comparingDouble(PositionSummary::valorActual).reversed())
                .toList();
    }

    // -------------------------
    // MÉTRICAS DEL FONDO (top movers para el panel rápido)
    // Calcula el NAV completo primero, luego selecciona top 5 por movimiento
    // para el listado de top movers — pero totalValue refleja el fondo entero.
    // -------------------------
    public FundMetrics calculateMetrics(
            Fund fund,
            List<FundPosition> positions,
            double previousValue
    ) {
        if (positions == null || positions.isEmpty()) {
            return new FundMetrics(0, 0, 0, List.of());
        }

        // ── NAV completo (TODAS las posiciones válidas) ──────────
        double totalValue  = calculateTotalNAV(positions);
        double totalChange = positions.stream()
                .filter(FundPosition::isValid)
                .mapToDouble(FundPosition::getDailyReturn)
                .sum();

        // ── Top 5 por mayor movimiento absoluto (para el panel) ──
        List<FundPosition> topPositions = positions.stream()
                .filter(FundPosition::isValid)
                .sorted(Comparator.comparingDouble(
                        (FundPosition p) -> Math.abs(p.getDailyReturn())
                ).reversed())
                .limit(5)
                .toList();

        List<String> topMovers = new ArrayList<>();

        for (FundPosition pos : topPositions) {
            double currentPrice = 0;
            MarketEngine engine = DataStore.engines.get(pos.getIdAsset());
            if (engine != null) currentPrice = engine.getLastPrice();

            topMovers.add(String.format(
                    "%s:%.2f",          // formato "TICKER:returnPct" que parsea FundViewController
                    pos.getIdAsset(),
                    pos.getReturnPct()
            ));
        }

        double changePct = previousValue > 0
                ? (totalValue - previousValue) / previousValue * 100
                : 0;

        return new FundMetrics(totalValue, totalChange, changePct, topMovers);
    }
}