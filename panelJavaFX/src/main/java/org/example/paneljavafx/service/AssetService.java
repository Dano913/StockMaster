package org.example.paneljavafx.service;

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

    private final AssetDataSource assetDataSource = new AssetDataSource();
    private final FundPositionService positionService = FundPositionService.getInstance();

    public final List<Asset> assets = new ArrayList<>();
    private boolean loaded = false;

    public void load() {
        if (loaded) return;
        loaded = true;

        assets.clear();
        assets.addAll(assetDataSource.load());

        assetDataSource.printAssets(assets);
    }

    public List<Asset> getAll() {
        return assets;
    }

    public Asset getById(String id) {
        return assets.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public AssetMetrics calculateMetrics(List<FundPosition> positions, String assetId) {

        if (positions == null || assetId == null) {
            return new AssetMetrics(0, 0, 0, 0);
        }

        double totalExposure = 0;
        long fundsExposed = 0;

        Set<String> uniqueFunds = new HashSet<>();

        double totalPortfolioValue = 0; // necesario para ratios

        for (FundPosition p : positions) {

            double value = p.getInvestedValue();
            totalPortfolioValue += value;

            if (assetId.equals(p.getIdAsset())) {
                totalExposure += value;
                uniqueFunds.add(p.getIdFund());
            }
        }

        fundsExposed = uniqueFunds.size();

        double exposureRatio = (totalPortfolioValue == 0)
                ? 0
                : totalExposure / totalPortfolioValue;

        double globalWeight = exposureRatio; // si no tienes otra métrica más compleja aún

        return new AssetMetrics(
                totalExposure,
                exposureRatio,
                fundsExposed,
                globalWeight
        );
    }


}