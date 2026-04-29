package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.FundPositionDAO;
import org.example.paneljavafx.dao.impl.FundPositionImpl;
import org.example.paneljavafx.model.FundPosition;

import java.util.ArrayList;
import java.util.List;

public class FundPositionService {

    // =========================
    // SINGLETON
    // =========================
    private static final FundPositionService INSTANCE = new FundPositionService();

    public static FundPositionService getInstance() { return INSTANCE; }

    private FundPositionService() {}

    // =========================
    // DEPENDENCIES
    // =========================
    private final FundPositionDAO positionDAO   = new FundPositionImpl(); // ← sustituye FundPositionDataSource
    private final MarketService   marketService = MarketService.getInstance();

    // =========================
    // STATE
    // =========================
    private List<FundPosition> positions = new ArrayList<>();
    private boolean loaded = false;

    // =========================
    // LOAD
    // =========================
    public List<FundPosition> load() {
        if (loaded) return positions;
        loaded = true;

        positions = positionDAO.findAll();  // ← BD en lugar de JSON
        return positions;
    }

    public List<FundPosition> getAll() { return positions; }

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
        return p.getQuantity() * marketService.getPrice(p.getIdAsset());
    }

    // =========================
    // DAILY RETURN
    // =========================
    public double getDailyReturn(FundPosition p) {
        return getValue(p) - p.getInvestedValue();
    }

    // =========================
    // RETURN %
    // =========================
    public double getReturnPct(FundPosition p) {
        if (p.getInvestedValue() <= 0) return 0;
        return (getValue(p) - p.getInvestedValue()) / p.getInvestedValue() * 100;
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
    // TOP MOVERS
    // =========================
    public List<FundPosition> getTopByMovement(List<FundPosition> positions, int limit) {
        if (positions == null) return List.of();
        return positions.stream()
                .filter(FundPosition::isValid)
                .sorted((p1, p2) -> Double.compare(
                        Math.abs(getValue(p2) - p2.getInvestedValue()),
                        Math.abs(getValue(p1) - p1.getInvestedValue())
                ))
                .limit(limit)
                .toList();
    }
}