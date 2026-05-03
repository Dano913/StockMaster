package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.dao.AssetDAO;
import org.example.paneljavafx.dao.impl.AssetImpl;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.service.dto.AssetMetrics;

import java.util.ArrayList;
import java.util.List;

public class AssetService {

    // ========================= SINGLETON =========================
    private static final AssetService INSTANCE = new AssetService();
    public static AssetService getInstance() { return INSTANCE; }
    private AssetService() {}

    // ========================= DAO =========================
    private final AssetDAO assetDAO = new AssetImpl();

    // ========================= CACHE =========================
    public final ObservableList<Asset> assets = FXCollections.observableArrayList();
    private boolean loaded = false;

    // ========================= LOAD =========================
    public void load() {
        if (loaded) return;

        try {
            List<Asset> data = assetDAO.findAll();
            assets.setAll(data);
            loaded = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========================= METRICS =========================
    public AssetMetrics calculateMetrics(List<FundAssetPosition> positions,
                                         String assetId) {

        double totalExposure = 0;
        long fundsExposed = 0;

        double portfolioTotal = getPortfolioTotal(positions);

        for (FundAssetPosition p : positions) {

            if (!p.getIdAsset().equals(assetId)) continue;

            totalExposure += p.getInvestedValue();
            fundsExposed++;
        }

        double exposureRatio = portfolioTotal == 0
                ? 0
                : totalExposure / portfolioTotal;

        double globalWeight = exposureRatio;

        // 🔥 volatilidad simplificada (SIN returns)
        double volatility = calculateSimpleVolatility(positions, assetId);

        return new AssetMetrics(
                totalExposure,
                exposureRatio,
                fundsExposed,
                globalWeight,
                volatility
        );
    }

    // ========================= HELPERS =========================

    private double getPortfolioTotal(List<FundAssetPosition> positions) {
        return positions.stream()
                .mapToDouble(FundAssetPosition::getInvestedValue)
                .sum();
    }

    private double calculateSimpleVolatility(List<FundAssetPosition> positions,
                                             String assetId) {

        List<Double> values = new ArrayList<>();

        for (FundAssetPosition p : positions) {
            if (!p.getIdAsset().equals(assetId)) continue;
            values.add(p.getInvestedValue());
        }

        if (values.isEmpty()) return 0;

        double mean = values.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0);

        return Math.sqrt(variance);
    }

    // ========================= GET =========================

    public ObservableList<Asset> getAll() {
        return assets;
    }

    public long count() {
        return assets.size();
    }
}