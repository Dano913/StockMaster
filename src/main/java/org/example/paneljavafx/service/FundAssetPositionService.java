package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.FundAssetPositionDAO;
import org.example.paneljavafx.dao.impl.FundAssetPositionImpl;
import org.example.paneljavafx.model.FundAssetPosition;

import java.util.ArrayList;
import java.util.List;

public class FundAssetPositionService {

    // ========================= SINGLETON (LAZY SAFE) =========================
    private static FundAssetPositionService INSTANCE;

    public static FundAssetPositionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FundAssetPositionService();
        }
        return INSTANCE;
    }

    private FundAssetPositionService() {
        // ❌ NO tocar MarketService aquí (evita crash de init order)
    }

    // ========================= DEPENDENCY =========================
    private MarketService marketService;

    public void init() {
        this.marketService = MarketService.getInstance();
    }

    // ========================= DAO =========================
    private final FundAssetPositionDAO positionDAO = new FundAssetPositionImpl();

    // ========================= CACHE =========================
    private List<FundAssetPosition> positions = new ArrayList<>();
    private boolean loaded = false;

    // ========================= LOAD =========================
    public List<FundAssetPosition> load() {
        if (loaded) return positions;

        loaded = true;
        positions = positionDAO.findAll();
        return positions;
    }

    // ========================= GETTERS =========================
    public List<FundAssetPosition> getAll() {
        return positions;
    }

    public List<FundAssetPosition> getByFundId(List<FundAssetPosition> all, String fundId) {
        if (all == null || fundId == null) return List.of();

        return all.stream()
                .filter(p -> fundId.equals(p.getIdFund()))
                .toList();
    }

    public List<FundAssetPosition> getByAssetId(List<FundAssetPosition> all, String assetId) {
        if (all == null || assetId == null) return List.of();

        return all.stream()
                .filter(p -> assetId.equals(p.getIdAsset()))
                .toList();
    }

    // ========================= VALUE =========================
    public double getValue(FundAssetPosition p) {
        if (marketService == null) {
            throw new IllegalStateException("FundAssetPositionService no inicializado (init() no llamado)");
        }

        return p.getQuantity() * marketService.getPrice(p.getIdAsset());
    }

    // ========================= NAV =========================
    public double calculateNAV(List<FundAssetPosition> positions) {
        if (positions == null || positions.isEmpty()) return 0;

        double total = positions.stream()
                .filter(FundAssetPosition::isValid)
                .mapToDouble(this::getValue)
                .sum();

        return total;
    }

    // ========================= TOP MOVERS =========================
    public List<FundAssetPosition> getTopByMovement(List<FundAssetPosition> positions, int limit) {
        if (positions == null) return List.of();

        return positions.stream()
                .filter(FundAssetPosition::isValid)
                .sorted((p1, p2) -> Double.compare(
                        Math.abs(getValue(p2) - p2.getInvestedValue()),
                        Math.abs(getValue(p1) - p1.getInvestedValue())
                ))
                .limit(limit)
                .toList();
    }
}