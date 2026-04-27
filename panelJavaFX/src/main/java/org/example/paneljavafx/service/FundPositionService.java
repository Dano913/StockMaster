package org.example.paneljavafx.service;

import org.example.paneljavafx.data.FundPositionDataSource;
import org.example.paneljavafx.model.FundPosition;

import java.util.ArrayList;
import java.util.List;

public class FundPositionService {

    // =========================
    // SINGLETON
    // =========================
    private static final FundPositionService INSTANCE = new FundPositionService();
    private List<FundPosition> positions = new ArrayList<>();

    public static FundPositionService getInstance() {
        return INSTANCE;
    }

    private FundPositionService() {}

    // =========================
    // DEPENDENCY
    // =========================
    private final MarketService marketService = MarketService.getInstance();

    public void load() {

        positions = FundPositionDataSource.load();

        FundPositionDataSource.printPositions(positions);
    }

    // =========================
    // GET BY FUND
    // =========================
    public List<FundPosition> getByFundId(List<FundPosition> all, String fundId) {

        if (all == null || fundId == null) return List.of();

        return all.stream()
                .filter(p -> fundId.equals(p.getIdFund()))
                .toList();
    }

    // =========================
    // GET BY ASSET
    // =========================
    public List<FundPosition> getByAssetId(List<FundPosition> all, String assetId) {

        if (all == null || assetId == null) return List.of();

        return all.stream()
                .filter(p -> assetId.equals(p.getIdAsset()))
                .toList();
    }

    // =========================
    // VALOR POSICIÓN
    // =========================
    public double getValue(FundPosition p) {

        double price = marketService.getPrice(p.getIdAsset());
        return p.getQuantity() * price;
    }

    // =========================
    // DAILY RETURN
    // =========================
    public double getDailyReturn(FundPosition p) {

        double price = marketService.getPrice(p.getIdAsset());
        return (p.getQuantity() * price) - p.getInvestedValue();
    }

    // =========================
    // RETURN %
    // =========================
    public double getReturnPct(FundPosition p) {

        double price = marketService.getPrice(p.getIdAsset());

        if (p.getInvestedValue() <= 0) return 0;

        return ((p.getQuantity() * price) - p.getInvestedValue())
                / p.getInvestedValue() * 100;
    }

    // =========================
    // NAV TOTAL
    // =========================
    public double calculateNAV(List<FundPosition> positions) {

        if (positions == null || positions.isEmpty()) return 0;

        return positions.stream()
                .filter(FundPosition::isValid)
                .mapToDouble(this::getValue)
                .sum();
    }

    // =========================
    // TOP POSITIONS
    // =========================
    // =========================
    // TOP MOVERS (POR MOVIMIENTO ABSOLUTO)
    // =========================
    public List<FundPosition> getTopByMovement(List<FundPosition> positions, int limit) {

        if (positions == null) return List.of();

        return positions.stream()
                .filter(FundPosition::isValid)
                .sorted((p1, p2) -> {

                    double price1 = marketService.getPrice(p1.getIdAsset());
                    double value1 = p1.getQuantity() * price1;

                    double diff1 = Math.abs(value1 - p1.getInvestedValue());

                    double price2 = marketService.getPrice(p2.getIdAsset());
                    double value2 = p2.getQuantity() * price2;

                    double diff2 = Math.abs(value2 - p2.getInvestedValue());

                    return Double.compare(diff2, diff1);
                })
                .limit(limit)
                .toList();
    }
}