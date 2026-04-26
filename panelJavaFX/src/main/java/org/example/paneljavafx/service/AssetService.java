package org.example.paneljavafx.service;

import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.service.dto.AssetMetrics;

import java.util.List;

public class AssetService {

    public List<FundPosition> filterByAsset(List<FundPosition> all, String assetId) {
        if (all == null || assetId == null) return List.of();

        return all.stream()
                .filter(p -> assetId.equals(p.getIdAsset()))
                .toList();
    }

    public AssetMetrics calculateMetrics(List<FundPosition> all, String assetId) {

        List<FundPosition> positions = filterByAsset(all, assetId);

        if (positions.isEmpty()) {
            return new AssetMetrics(0, 0, 0, 0);
        }

        double totalExposure = positions.stream()
                .mapToDouble(FundPosition::getValorPosicion)
                .sum();

        long fundsExposed = positions.stream()
                .map(FundPosition::getIdFund)
                .distinct()
                .count();

        // normalización simple (ajustable luego)
        double exposureRatio = Math.min(totalExposure / 1_000_000.0, 1.0);

        double globalWeight = positions.stream()
                .mapToDouble(FundPosition::getPesoPorcentual)
                .sum();

        return new AssetMetrics(
                totalExposure,
                exposureRatio,
                fundsExposed,
                globalWeight
        );
    }
}