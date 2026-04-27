package org.example.paneljavafx.service;

import org.example.paneljavafx.model.FundPosition;

import java.util.List;

public class ExposureService {

    private final FundService fundService = FundService.getInstance();

    // -------------------------
    // FILTRAR POR ASSET
    // -------------------------
    public List<FundPosition> filterByAsset(
            List<FundPosition> positions,
            String assetId
    ) {
        if (positions == null || assetId == null) return List.of();

        return positions.stream()
                .filter(FundPosition::isValid)
                .filter(p -> assetId.equals(p.getIdAsset()))
                .toList();
    }

    // -------------------------
    // EXPOSICIÓN TOTAL
    // -------------------------
    public double calculateTotalExposure(List<FundPosition> positions) {
        if (positions == null || positions.isEmpty()) return 0;

        return fundService.calculateTotalNAV(positions);
    }

    // -------------------------
    // EXPOSICIÓN POR ASSET
    // -------------------------
    public double calculateExposureByAsset(
            List<FundPosition> positions,
            String assetId
    ) {
        return fundService.calculateTotalNAV(filterByAsset(positions, assetId));
    }

    // -------------------------
    // FONDOS EXPUESTOS
    // -------------------------
    public long countFundsExposedToAsset(
            List<FundPosition> positions,
            String assetId
    ) {
        return filterByAsset(positions, assetId)
                .stream()
                .map(FundPosition::getIdFund)
                .distinct()
                .count();
    }

    // -------------------------
    // PESO GLOBAL
    // -------------------------
    public double calculateGlobalAssetWeight(
            List<FundPosition> positions,
            String assetId
    ) {
        double total = calculateTotalExposure(positions);
        double asset = calculateExposureByAsset(positions, assetId);

        return total == 0 ? 0 : asset / total;
    }
}