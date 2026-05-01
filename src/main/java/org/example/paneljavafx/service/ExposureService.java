package org.example.paneljavafx.service;

import org.example.paneljavafx.model.FundAssetPosition;

import java.util.List;

public class ExposureService {

    // ========================= INSTANCE =========================
    private final FundService fundService = FundService.getInstance();

    // ========================= GET EXPOSURE BY ASSET =========================
    public List<FundAssetPosition> filterByAsset(
            List<FundAssetPosition> positions,
            String assetId
    ) {
        if (positions == null || assetId == null) return List.of();

        return positions.stream()
                .filter(FundAssetPosition::isValid)
                .filter(p -> assetId.equals(p.getIdAsset()))
                .toList();
    }

    // ========================= GET TOTAL EXPOSURE  =========================
    public double calculateTotalExposure(List<FundAssetPosition> positions) {
        if (positions == null || positions.isEmpty()) return 0;

        return fundService.calculateTotalNAV(positions);
    }

    // ========================= GET EXPOSURE BY ASSET =========================
    public double calculateExposureByAsset(
            List<FundAssetPosition> positions,
            String assetId
    ) {
        return fundService.calculateTotalNAV(filterByAsset(positions, assetId));
    }

    // ========================= DATA =========================
    public long countFundsExposedToAsset(
            List<FundAssetPosition> positions,
            String assetId
    ) {
        return filterByAsset(positions, assetId)
                .stream()
                .map(FundAssetPosition::getIdFund)
                .distinct()
                .count();
    }

    // -------------------------
    // PESO GLOBAL
    // -------------------------
    public double calculateGlobalAssetWeight(
            List<FundAssetPosition> positions,
            String assetId
    ) {
        double total = calculateTotalExposure(positions);
        double asset = calculateExposureByAsset(positions, assetId);

        return total == 0 ? 0 : asset / total;
    }
}