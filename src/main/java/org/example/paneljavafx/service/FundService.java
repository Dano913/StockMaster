package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.FundDAO;
import org.example.paneljavafx.dao.impl.FundImpl;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.service.dto.FundMetrics;

import java.util.Comparator;
import java.util.List;

public class FundService {

    // ========================= SINGLETON =========================
    private static final FundService INSTANCE = new FundService();
    public static FundService getInstance() { return INSTANCE; }
    private FundService() {}

    // ========================= DAO =========================
    private final FundDAO fundDAO = new FundImpl();

    // ========================= SERVICES =========================
    private final MarketService marketService = MarketService.getInstance();
    private final FundAssetPositionService positionService = FundAssetPositionService.getInstance();

    // ========================= CACHE =========================
    public final List<Fund> funds = new java.util.ArrayList<>();
    private boolean loaded = false;

    // ========================= LOAD =========================
    public void load() {
        if (loaded) return;

        funds.clear();
        funds.addAll(fundDAO.findAll());
        loaded = true;
    }

    // ========================= BASIC GETTERS =========================
    public List<Fund> getAll() {
        return funds;
    }

    public Fund getById(String id) {
        return funds.stream()
                .filter(f -> f.getFundId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public String getFundName(String id) {
        Fund fund = getById(id);
        return (fund != null) ? fund.getName() : "Unknown fund";
    }

    // ========================= POSITIONS =========================
    public List<FundAssetPosition> getPositionsByFund(List<FundAssetPosition> all, String fundId) {
        return positionService.getByFundId(all, fundId);
    }

    // ========================= NAV (SINGLE SOURCE) =========================
    public double calculateTotalNAV(List<FundAssetPosition> positions) {
        if (positions == null) return 0;

        double total = positions.stream()
                .mapToDouble(FundAssetPosition::getInvestedValue)
                .sum();

        System.out.println("Total NAV: " + total);

        return total;
    }

    // ========================= POSITION SUMMARY =========================
    public record PositionSummary(
            String idAsset,
            double valorActual,
            double investedValue,
            double returnPct,
            double dailyReturn,
            double currentPrice,
            double pesoPorcentual
    ) {}

    // ========================= CORE LOGIC =========================
    public List<PositionSummary> getSummaryByFund(List<FundAssetPosition> positions) {

        if (positions == null || positions.isEmpty()) {
            return List.of();
        }

        double navTotal = calculateTotalNAV(positions);

        return positions.stream()
                .filter(FundAssetPosition::isValid)
                .map(pos -> {

                    double price = marketService.getPrice(pos.getIdAsset());

                    double value = pos.getQuantity() * price;
                    double invested = pos.getInvestedValue();

                    double returnPct = invested > 0
                            ? ((value - invested) / invested) * 100
                            : 0;

                    double dailyReturn = value - invested;

                    double weight = navTotal > 0
                            ? (value / navTotal) * 100
                            : 0;

                    return new PositionSummary(
                            pos.getIdAsset(),
                            value,
                            invested,
                            returnPct,
                            dailyReturn,
                            price,
                            weight
                    );
                })
                .sorted(Comparator.comparingDouble(PositionSummary::valorActual).reversed())
                .toList();
    }
}