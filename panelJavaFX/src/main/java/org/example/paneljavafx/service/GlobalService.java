package org.example.paneljavafx.service;

import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.simulation.MarketEngine;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
            List<FondoSnapshot>  fondos,
            List<AssetSnapshot>  activos
    ) {}

    public record MonthlyBar(
            String label,
            double returnPct
    ) {}

    // ========================= INSTANCE =========================
    MarketService marketService = MarketService.getInstance();
    FundService fundService = FundService.getInstance();
    private final ExposureService exposureService = new ExposureService();
    private final AssetService    assetService    = AssetService.getInstance();

    private final DecimalFormat DF = new DecimalFormat("#,###.##");


    // ========================= BOOTSTRAP MARKET =========================
    public void bootstrapMarket() {
        marketService.bootstrapMarket();
    }

    // ========================= CALCULATE DATE =========================
    public GlobalSnapshot calculateSnapshot(
            List<Fund>         funds,
            List<Asset>        assets,
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

        // ── Valor de activos vía AssetService ────────────────────
        double totalActivos = assets.stream()
                .mapToDouble(asset ->
                        assetService.calculateMetrics(positions, asset.getId())
                                .getTotalExposure()
                )
                .sum();

        double capitalTotal = totalFondos + totalActivos;

        // ── Deltas tick a tick ────────────────────────────────────
        double deltaCapital = prevCapital > 0
                ? ((capitalTotal - prevCapital) / prevCapital) * 100 : 0;
        double deltaFondos  = prevFondos  > 0
                ? ((totalFondos  - prevFondos)  / prevFondos)  * 100 : 0;
        double deltaActivos = prevActivos > 0
                ? ((totalActivos - prevActivos) / prevActivos) * 100 : 0;

        // ── Rentabilidad acumulada ────────────────────────────────
        double exposicionTotal = exposureService.calculateTotalExposure(positions);
        double rentabilidad = exposicionTotal > 0
                ? ((capitalTotal - exposicionTotal) / exposicionTotal) * 100 : 0;

        // ── DTOs para las tablas ──────────────────────────────────
        List<FondoSnapshot> fondoSnapshots  = buildFondoSnapshots(funds, positions);
        List<AssetSnapshot> assetSnapshots  = buildAssetSnapshots(assets);

        return new GlobalSnapshot(
                capitalTotal, totalFondos, totalActivos,
                deltaCapital, deltaFondos, deltaActivos,
                rentabilidad,
                fondoSnapshots, assetSnapshots
        );
    }

    // ========================= BUILD SNAPSHOT =========================
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
                            ? ((nav - invertido) / invertido) * 100 : 0;

                    return new FondoSnapshot(
                            fund.getFundId(),
                            fund.getName(),
                            nav,
                            invertido,
                            rentabilidad
                    );
                })
                .sorted(Comparator.comparingDouble(FondoSnapshot::nav).reversed())
                .toList();
    }

    public List<AssetSnapshot> buildAssetSnapshots(List<Asset> assets) {
        return assets.stream()
                .map(asset -> {
                    MarketEngine engine = marketService.getEngine(asset.getId());
                    if (engine == null) return null;

                    String label = asset.getTicker() != null
                            ? asset.getTicker() : asset.getName();

                    return new AssetSnapshot(
                            label,
                            engine.getLastPrice(),
                            engine.getChange()
                    );
                })
                .filter(r -> r != null)
                .sorted(Comparator.comparingDouble(
                        (AssetSnapshot r) -> Math.abs(r.changePct())
                ).reversed())
                .toList();
    }

    // ========================= BAR CHART =========================
    public List<MonthlyBar> calculateMonthlyBars(List<Double> history, int maxBars) {
        List<MonthlyBar> result = new ArrayList<>();
        int totalTicks = history.size();
        if (totalTicks < 2) return result;

        int ticksPerBar = Math.max(1, totalTicks / maxBars);
        int numBars     = Math.min(maxBars, totalTicks / ticksPerBar);
        if (numBars < 1) return result;

        String[] meses = { "Ene","Feb","Mar","Abr","May","Jun",
                "Jul","Ago","Sep","Oct","Nov","Dic" };

        int currentMonth = java.time.LocalDate.now().getMonthValue() - 1; // 0-based

        for (int i = 0; i < numBars; i++) {
            int startIdx = i * ticksPerBar;
            int endIdx   = Math.min(startIdx + ticksPerBar, totalTicks) - 1;

            double startVal = history.get(startIdx);
            double endVal   = history.get(endIdx);
            double ret      = startVal > 0 ? ((endVal - startVal) / startVal) * 100 : 0;

            int mesIdx = (currentMonth - numBars + 1 + i + 12) % 12;
            result.add(new MonthlyBar(meses[mesIdx], ret));
        }

        return result;
    }

    // ========================= EXTRA =========================
    public List<Object> filter(List<Object> masterData, String query) {
        if (query == null || query.isBlank()) return new ArrayList<>(masterData);

        String q = query.toLowerCase();
        return masterData.stream()
                .filter(item -> matches(item, q))
                .toList();
    }

    private boolean matches(Object item, String q) {
        if (item instanceof Fund f) {
            return f.getName().toLowerCase().contains(q)
                    || f.getType().toLowerCase().contains(q);
        }
        if (item instanceof Asset a) {
            return a.getName().toLowerCase().contains(q)
                    || (a.getTicker()  != null && a.getTicker().toLowerCase().contains(q))
                    || (a.getIsinCode()    != null && a.getIsinCode().toLowerCase().contains(q))
                    || (a.getSector()  != null && a.getSector().toLowerCase().contains(q));
        }
        return false;
    }

    public String formatShort(double v) {
        if (v >= 1_000_000_000) return String.format("%.2fB €", v / 1_000_000_000);
        if (v >= 1_000_000)     return String.format("%.2fM €", v / 1_000_000);
        if (v >= 1_000)         return String.format("%.1fK €", v / 1_000);
        return DF.format(v) + " €";
    }
}