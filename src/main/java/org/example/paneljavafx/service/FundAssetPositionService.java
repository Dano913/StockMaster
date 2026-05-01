package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.FundAssetPositionDAO;
import org.example.paneljavafx.dao.impl.FundAssetPositionImpl;
import org.example.paneljavafx.model.FundAssetPosition;

import java.util.ArrayList;
import java.util.List;

public class FundAssetPositionService {

    // ========================= SINGLETON =========================
    private static final FundAssetPositionService INSTANCE = new FundAssetPositionService();
    public static FundAssetPositionService getInstance() { return INSTANCE; }
    private FundAssetPositionService() {}

    // ========================= DAO =========================
    private final FundAssetPositionDAO positionDAO   = new FundAssetPositionImpl();

    // ========================= INSTANCIA =========================
    private final MarketService   marketService = MarketService.getInstance();

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

    // ========================= GET FUND POSITION =========================
    public List<FundAssetPosition> getAll() { return positions; }

    public List<FundAssetPosition> getByFundId(List<FundAssetPosition> all, String fundId) {
        if (all == null || fundId == null) return List.of();
        return all.stream()
                .filter(p -> fundId.equals(p.getIdFund()))
                .toList();
    }

    // ========================= GET ASSET =========================
    public List<FundAssetPosition> getByAssetId(List<FundAssetPosition> all, String assetId) {
        if (all == null || assetId == null) return List.of();
        return all.stream()
                .filter(p -> assetId.equals(p.getIdAsset()))
                .toList();
    }

    // ========================= GET POSITION VALUE =========================
    public double getValue(FundAssetPosition p) {
        return p.getQuantity() * marketService.getPrice(p.getIdAsset());
    }

    // ========================= GET TOTAL NAV =========================
    public double calculateNAV(List<FundAssetPosition> positions) {
        if (positions == null || positions.isEmpty()) return 0;
        return positions.stream()
                .filter(FundAssetPosition::isValid)
                .mapToDouble(this::getValue)
                .sum();
    }

    // ========================= GET TOPS =========================
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