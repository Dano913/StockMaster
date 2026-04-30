package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.dao.AssetDAO;
import org.example.paneljavafx.dao.impl.AssetImpl;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.service.dto.AssetMetrics;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssetService {

    // ========================= SINGLETON =========================

    private static final AssetService INSTANCE = new AssetService();
    public static AssetService getInstance() {
        return INSTANCE;
    }
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
            System.err.println("Error cargando assets desde BD");
        }
    }

    // ========================= METRICS =========================

    public AssetMetrics calculateMetrics(List<FundAssetPosition> positions, String assetId) {

        if (positions == null || assetId == null) {
            return new AssetMetrics(0, 0, 0, 0);
        }

        double totalExposure = 0;
        double totalPortfolioValue = 0;

        Set<String> uniqueFunds = new HashSet<>();

        for (FundAssetPosition p : positions) {

            double value = p.getInvestedValue();
            totalPortfolioValue += value;

            if (assetId.equals(p.getIdAsset())) {
                totalExposure += value;
                uniqueFunds.add(p.getIdFund());
            }
        }

        double exposureRatio = (totalPortfolioValue == 0)
                ? 0
                : totalExposure / totalPortfolioValue;

        return new AssetMetrics(
                totalExposure,
                exposureRatio,
                uniqueFunds.size(),
                exposureRatio
        );
    }

    // ========================= GET =========================

    public ObservableList<Asset> getAll() {
        return assets;
    }

    public Asset getById(String id) {
        return assets.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public long count() {
        return assets.size();
    }

}
