package org.example.paneljavafx.service;

import org.example.paneljavafx.model.*;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.List;
import java.util.Objects;

public class SnapshotService {

    private final FundService fundService = FundService.getInstance();
    private final AssetService assetService = AssetService.getInstance();
    private final MarketService marketService = MarketService.getInstance();

    // ========================= FONDOS =========================
    public List<GlobalService.FondoSnapshot> buildFondoSnapshots(
            List<Fund> funds,
            List<FundAssetPosition> positions
    ) {
        return funds.stream()
                .map(fund -> {

                    List<FundAssetPosition> posFund =
                            fundService.getPositionsByFund(positions, fund.getFundId());

                    double nav = fundService.calculateTotalNAV(posFund);

                    double invertido = posFund.stream()
                            .filter(FundAssetPosition::isValid)
                            .mapToDouble(FundAssetPosition::getInvestedValue)
                            .sum();

                    double rentabilidad = invertido > 0
                            ? ((nav - invertido) / invertido) * 100
                            : 0;

                    return new GlobalService.FondoSnapshot(
                            fund.getFundId(),
                            fund.getName(),
                            nav,
                            invertido,
                            rentabilidad
                    );
                })
                .toList();
    }

    // ========================= ASSETS =========================
    public List<GlobalService.AssetSnapshot> buildAssetSnapshots(List<Asset> assets) {

        return assets.stream()
                .map(asset -> {

                    MarketEngine engine = marketService.getEngine(asset.getId());
                    if (engine == null) return null;

                    double referencePrice = asset.getInitialPrice();
                    double currentPrice = engine.getLastPrice();

                    double change = ((currentPrice - referencePrice) / referencePrice) * 100;

                    String label = asset.getTicker() != null
                            ? asset.getTicker()
                            : asset.getName();

                    return new GlobalService.AssetSnapshot(
                            label,
                            currentPrice,
                            change
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }
}