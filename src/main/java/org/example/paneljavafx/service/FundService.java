package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.FundDAO;
import org.example.paneljavafx.dao.impl.FundImpl;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.service.dto.FundMetrics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FundService {

    // ========================= SINGLETON =========================
    private static final FundService INSTANCE = new FundService();
    public static FundService getInstance() { return INSTANCE; }
    private FundService() {}

    // ========================= DAO =========================
    private final FundDAO fundDAO = new FundImpl();

    // ========================= INSTANCE =========================
    private final MarketService marketService = MarketService.getInstance();
    private final FundAssetPositionService positionService = FundAssetPositionService.getInstance();

    // ========================= CACHE =========================
    public final List<Fund> funds = new ArrayList<>();
    private boolean loaded = false;

    // ========================= LOAD =========================
    public void load() {
        if (loaded) return;
        loaded = true;

        funds.clear();
        funds.addAll(fundDAO.findAll());
    }

    // ========================= GET FUND =========================
    public List<Fund> getAll() { return funds; }

    public Fund getById(String id) {
        return funds.stream()
                .filter(f -> f.getFundId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public String getFundName(String fundId) {
        return getAll().stream()
                .filter(f -> f.getFundId().equals(fundId))
                .map(Fund::getName)
                .findFirst()
                .orElse("Unknown fund");
    }

    // ========================= GET POSITION =========================

    public List<FundAssetPosition> getPositionsByFund(List<FundAssetPosition> all, String fundId) {
        return positionService.getByFundId(all, fundId);
    }

    // ========================= GET NAV =========================
    public double calculateTotalNAV(List<FundAssetPosition> positions) {
        return positionService.calculateNAV(positions);
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

    // ========================= GET SUMMARY =========================
    public List<PositionSummary> getSummaryByFund(List<FundAssetPosition> positions) {
        if (positions == null) return List.of();

        double navTotal = calculateTotalNAV(positions);

        return positions.stream()
                .filter(FundAssetPosition::isValid)
                .map(pos -> {
                    double price      = marketService.getPrice(pos.getIdAsset());
                    double valorActual = pos.getQuantity() * price;
                    double invested    = pos.getInvestedValue();
                    double returnPct   = invested > 0 ? ((valorActual - invested) / invested) * 100 : 0;
                    double dailyReturn = valorActual - invested;
                    double peso        = navTotal > 0 ? (valorActual / navTotal) * 100 : 0;

                    return new PositionSummary(
                            pos.getIdAsset(), valorActual, invested,
                            returnPct, dailyReturn, price, peso
                    );
                })
                .sorted(Comparator.comparingDouble(PositionSummary::valorActual).reversed())
                .toList();
    }

    // ========================= METRICS =========================
    public FundMetrics calculateMetrics(Fund fund, List<FundAssetPosition> positions, double previousValue) {
        if (positions == null || positions.isEmpty())
            return new FundMetrics(0, 0, 0, List.of());

        double totalValue  = calculateTotalNAV(positions);
        double totalChange = positions.stream()
                .filter(FundAssetPosition::isValid)
                .mapToDouble(p -> {
                    double price        = marketService.getPrice(p.getIdAsset());
                    double currentValue = p.getQuantity() * price;
                    return currentValue - p.getInvestedValue();
                })
                .sum();

        List<String> topMovers = new ArrayList<>();
        for (FundAssetPosition pos : positionService.getTopByMovement(positions, 5)) {
            double price     = marketService.getPrice(pos.getIdAsset());
            double value     = pos.getQuantity() * price;
            double returnPct = pos.getInvestedValue() > 0
                    ? ((value - pos.getInvestedValue()) / pos.getInvestedValue()) * 100 : 0;
            topMovers.add(String.format("%s:%.2f", pos.getIdAsset(), returnPct));
        }

        double changePct = previousValue > 0
                ? (totalValue - previousValue) / previousValue * 100 : 0;

        return new FundMetrics(totalValue, totalChange, changePct, topMovers);
    }
}