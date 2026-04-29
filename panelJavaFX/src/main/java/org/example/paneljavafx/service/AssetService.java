package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.AssetDAO;
import org.example.paneljavafx.dao.impl.AssetImpl;
import org.example.paneljavafx.data.AssetDataSource;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.service.dto.AssetMetrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssetService {

    private static final AssetService INSTANCE = new AssetService();

    public static AssetService getInstance() {
        return INSTANCE;
    }

    private AssetService() {}


    private final AssetDAO assetDAO = new AssetImpl();
    private final FundPositionService positionService = FundPositionService.getInstance();

    public final List<Asset> assets = new ArrayList<>();

    private boolean loaded = false;

    public void load() {
        if (loaded) return;

        loaded = true;

        List<Asset> assetsFromDao = assetDAO.findAll();

        assets.clear();
        assets.addAll(assetsFromDao);
    }

    public List<Asset> getAll() {
        return assetDAO.findAll();
    }

    public Asset getById(String id) {
        return assetDAO.findById(id).orElse(null);
    }

    public AssetMetrics calculateMetrics(List<FundPosition> positions, String assetId) {

        if (positions == null || assetId == null) {
            return new AssetMetrics(0, 0, 0, 0);
        }

        double totalExposure = 0;
        double totalPortfolioValue = 0;

        Set<String> uniqueFunds = new HashSet<>();

        for (FundPosition p : positions) {

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
}
