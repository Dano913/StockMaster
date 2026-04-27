package org.example.paneljavafx.service;

import org.example.paneljavafx.data.ClienteDataSource;
import org.example.paneljavafx.data.FundDataSource;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.service.dto.FundMetrics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FundService {

    // =========================
    // SINGLETON
    // =========================
    private static final FundService INSTANCE = new FundService();

    public static FundService getInstance() {
        return INSTANCE;
    }

    private FundService() {}

    // =========================
    // DEPENDENCIES
    // =========================
    private final FundDataSource dataSource = new FundDataSource();
    private final MarketService marketService = MarketService.getInstance();
    private final FundPositionService positionService = FundPositionService.getInstance();

    private final FundDataSource fundDataSource = new FundDataSource();

    // =========================
    // STATE
    // =========================
    public final List<Fund> funds = new ArrayList<>();
    private boolean loaded = false;

    // =========================
    // LOAD
    // =========================
    public void load() {

        if (loaded) return;
        loaded = true;

        funds.clear();
        funds.addAll(dataSource.load());
        fundDataSource.printFunds(funds);
    }

    // =========================
    // GET ALL
    // =========================
    public List<Fund> getAll() {
        return funds;
    }

    // =========================
    // GET BY ID
    // =========================
    public Fund getById(String id) {
        return funds.stream()
                .filter(f -> f.getIdFondo().equals(id))
                .findFirst()
                .orElse(null);
    }

    // =========================
    // POSITIONS BY FUND
    // =========================
    public List<FundPosition> getPositionsByFund(List<FundPosition> all, String fundId) {
        return positionService.getByFundId(all, fundId);
    }

    // =========================
    // NAV TOTAL (USANDO SERVICE)
    // =========================
    public double calculateTotalNAV(List<FundPosition> positions) {
        return positionService.calculateNAV(positions);
    }

    // =========================
    // POSITION SUMMARY
    // =========================
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

                    double price = marketService.getPrice(pos.getIdAsset());

                    double valorActual = pos.getQuantity() * price;
                    double invested = pos.getInvestedValue();

                    double returnPct = invested > 0
                            ? ((valorActual - invested) / invested) * 100
                            : 0;

                    double dailyReturn = valorActual - invested;

                    double peso = navTotal > 0
                            ? (valorActual / navTotal) * 100
                            : 0;

                    return new PositionSummary(
                            pos.getIdAsset(),
                            valorActual,
                            invested,
                            returnPct,
                            dailyReturn,
                            price,
                            peso
                    );
                })
                .sorted(Comparator.comparingDouble(PositionSummary::valorActual).reversed())
                .toList();
    }

    // =========================
    // METRICS
    // =========================
    public FundMetrics calculateMetrics(
            Fund fund,
            List<FundPosition> positions,
            double previousValue
    ) {

        if (positions == null || positions.isEmpty()) {
            return new FundMetrics(0, 0, 0, List.of());
        }

        double totalValue = calculateTotalNAV(positions);

        double totalChange = positions.stream()
                .filter(FundPosition::isValid)
                .mapToDouble(p -> {
                    double price = marketService.getPrice(p.getIdAsset());
                    double currentValue = p.getQuantity() * price;
                    return currentValue - p.getInvestedValue();
                })
                .sum();

        List<FundPosition> topPositions =
                positionService.getTopByMovement(positions, 5);

        List<String> topMovers = new ArrayList<>();

        for (FundPosition pos : topPositions) {

            double price = marketService.getPrice(pos.getIdAsset());
            double value = pos.getQuantity() * price;

            double returnPct = pos.getInvestedValue() > 0
                    ? ((value - pos.getInvestedValue()) / pos.getInvestedValue()) * 100
                    : 0;

            topMovers.add(String.format(
                    "%s:%.2f",
                    pos.getIdAsset(),
                    returnPct
            ));
        }

        double changePct = previousValue > 0
                ? (totalValue - previousValue) / previousValue * 100
                : 0;

        return new FundMetrics(totalValue, totalChange, changePct, topMovers);
    }
}