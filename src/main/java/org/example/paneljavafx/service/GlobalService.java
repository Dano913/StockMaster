package org.example.paneljavafx.service;

import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.simulation.MarketEngine;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalService {

    // ========================= DATA =========================
    public record FondoSnapshot(
            String id,
            String nombre,
            double nav,
            double invertido,
            double rentabilidadPct
    ) {}

    public record AssetSnapshot(
            String label,
            double precioActual,
            double changePct
    ) {}

    public record GlobalSnapshot(
            double capitalTotal,
            double totalFondos,
            double totalActivos,
            double deltaCapital,
            double deltaFondos,
            double deltaActivos,
            double rentabilidad,
            List<FondoSnapshot> fondos,
            List<AssetSnapshot> activos
    ) {}

    // ========================= SERVICES =========================
    MarketService marketService = MarketService.getInstance();
    FundService fundService = FundService.getInstance();
    AssetService assetService = AssetService.getInstance();
    private final ExposureService exposureService = new ExposureService();

    private final DecimalFormat DF = new DecimalFormat("#,###.##");

    public void bootstrapMarket() {
        marketService.bootstrapMarket();
    }

    // ========================= CALCULATION =========================
    public GlobalSnapshot calculateSnapshot(
            List<Fund> funds,
            List<Asset> assets,
            List<FundAssetPosition> positions,
            double prevCapital,
            double prevFondos,
            double prevActivos
    ) {

        double totalFondos = funds.stream()
                .mapToDouble(fund -> {
                    List<FundAssetPosition> posFund =
                            fundService.getPositionsByFund(positions, fund.getFundId());
                    return fundService.calculateTotalNAV(posFund);
                })
                .sum();

        double totalActivos = assets.stream()
                .mapToDouble(asset ->
                        assetService.calculateMetrics(positions, asset.getId())
                                .getTotalExposure()
                )
                .sum();

        double capitalTotal = totalFondos + totalActivos;

        double deltaCapital = prevCapital > 0
                ? ((capitalTotal - prevCapital) / prevCapital) * 100 : 0;

        double deltaFondos = prevFondos > 0
                ? ((totalFondos - prevFondos) / prevFondos) * 100 : 0;

        double deltaActivos = prevActivos > 0
                ? ((totalActivos - prevActivos) / prevActivos) * 100 : 0;

        double exposicionTotal = exposureService.calculateTotalExposure(positions);

        double rentabilidad = exposicionTotal > 0
                ? ((capitalTotal - exposicionTotal) / exposicionTotal) * 100
                : 0;

        return new GlobalSnapshot(
                capitalTotal,
                totalFondos,
                totalActivos,
                deltaCapital,
                deltaFondos,
                deltaActivos,
                rentabilidad,
                buildFondoSnapshots(funds, positions),
                buildAssetSnapshots(assets)
        );
    }

    // ========================= SNAPSHOTS =========================
    public List<FondoSnapshot> buildFondoSnapshots(
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

                    return new FondoSnapshot(
                            fund.getFundId(),
                            fund.getName(),
                            nav,
                            invertido,
                            rentabilidad
                    );
                })
                .toList();
    }

    public List<AssetSnapshot> buildAssetSnapshots(List<Asset> assets) {
        return assets.stream()
                .map(asset -> {
                    MarketEngine engine = marketService.getEngine(asset.getId());
                    if (engine == null) return null;

                    String label = asset.getTicker() != null
                            ? asset.getTicker()
                            : asset.getName();

                    return new AssetSnapshot(
                            label,
                            engine.getLastPrice(),
                            engine.getChange()
                    );
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    // ========================= SEARCH =========================
    public List<Object> filter(List<Object> masterData, String query) {
        if (query == null || query.isBlank())
            return masterData;

        String q = query.toLowerCase();

        return masterData.stream()
                .filter(item -> matches(item, q))
                .collect(Collectors.toList());
    }

    private boolean matches(Object item, String q) {

        if (item instanceof Fund f) {
            return f.getName().toLowerCase().contains(q)
                    || f.getType().toLowerCase().contains(q);
        }

        if (item instanceof Asset a) {
            return a.getName().toLowerCase().contains(q)
                    || (a.getTicker() != null && a.getTicker().toLowerCase().contains(q))
                    || (a.getIsinCode() != null && a.getIsinCode().toLowerCase().contains(q))
                    || (a.getSector() != null && a.getSector().toLowerCase().contains(q));
        }

        return false;
    }

    // ========================= FORMAT =========================
    public String formatShort(double v) {
        if (v >= 1_000_000_000) return String.format("%.2fB €", v / 1_000_000_000);
        if (v >= 1_000_000)     return String.format("%.2fM €", v / 1_000_000);
        if (v >= 1_000)         return String.format("%.1fK €", v / 1_000);
        return DF.format(v) + " €";
    }
}