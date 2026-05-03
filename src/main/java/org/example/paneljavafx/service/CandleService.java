package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.CandleDAO;
import org.example.paneljavafx.dao.impl.CandleImpl;
import org.example.paneljavafx.model.*;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class CandleService {

    // ========================= SINGLETON =========================
    private static final CandleService INSTANCE = new CandleService();

    public static CandleService getInstance() { return INSTANCE; }

    private CandleService() {}

    // ========================= CACHE =========================
    private Map<String, Double> lastAssetRent;
    private Map<String, Double> lastFundRent;

    public Map<String, Double> getCachedAssetRent(List<Asset> assets) {
        lastAssetRent = rentabilidadUltimoMesActivos(assets);
        return lastAssetRent;
    }

    public Map<String, Double> getCachedFundRent(List<Fund> funds) {
        lastFundRent = rentabilidadUltimoMesFondos(funds);
        return lastFundRent;
    }

    // ========================= DAO =========================
    private final CandleDAO candleDAO = new CandleImpl();

    private final Map<String, List<Candle>> cache = new HashMap<>();

    public void refreshCache(String assetId) {
        cache.put(assetId, candleDAO.findByAssetId(assetId));
    }

    public void refreshAll(List<Asset> assets) {
        for (Asset a : assets) {
            cache.put(a.getId(), candleDAO.findByAssetId(a.getId()));
        }
    }

    // ========================= DEPENDENCIES =========================
    private final FundService fundService = FundService.getInstance();
    private final FundAssetPositionService positionService = FundAssetPositionService.getInstance();

    // ========================= DTOs =========================
    public record RentabilidadMensual(int year, int month, double rentabilidadPct) {}
    public record RentabilidadPeriodo(double rentabilidadPct, double precioInicio, double precioFin) {}

    // ========================= UTILIDADES =========================
    private TemporalAccessor toLocalDate(long timestampMs) {
        return Instant.ofEpochMilli(timestampMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private List<String> getAssetIdsByFund(String fundId) {
        return positionService.getAll().stream()
                .filter(p -> fundId.equals(p.getIdFund()))
                .map(FundAssetPosition::getIdAsset)
                .distinct()
                .toList();
    }

    private List<Candle> getCandles(String assetId) {
        return cache.getOrDefault(assetId, List.of());
    }

    private List<Candle> filterAndSortCandlesByMonth(List<Candle> candles, YearMonth yearMonth) {
        return candles.stream()
                .filter(c -> YearMonth.from(toLocalDate(c.getTimestamp())).equals(yearMonth))
                .sorted(Comparator.comparingLong(Candle::getTimestamp))
                .toList();
    }

    // ========================= ASSET RENTABILIDAD =========================

    public double rentabilidadUltimoMesActivo(String assetId) {
        List<Candle> candles = getCandles(assetId);
        if (candles.isEmpty()) return 0;

        YearMonth lastMonth = YearMonth.from(
                toLocalDate(candles.get(candles.size() - 1).getTimestamp())
        ).minusMonths(1);

        List<Candle> mes = filterAndSortCandlesByMonth(candles, lastMonth);

        if (mes.isEmpty()) return 0;

        double inicio = mes.get(0).getOpen();
        double fin = mes.get(mes.size() - 1).getClose();

        return inicio > 0 ? ((fin - inicio) / inicio) * 100 : 0;
    }

    public Map<String, Double> rentabilidadUltimoMesActivos(List<Asset> assets) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Asset a : assets) {
            result.put(a.getName(), rentabilidadUltimoMesActivo(a.getId()));
        }
        return result;
    }

    // ========================= FUND RENTABILIDAD =========================

    public Map<String, Double> rentabilidadUltimoMesFondos(List<Fund> funds) {

        Map<String, Double> result = new LinkedHashMap<>();

        for (Fund f : funds) {

            List<String> assetIds = getAssetIdsByFund(f.getFundId());
            if (assetIds.isEmpty()) continue;

            YearMonth targetMonth = assetIds.stream()
                    .map(this::getCandles)
                    .filter(c -> !c.isEmpty())
                    .map(c -> YearMonth.from(toLocalDate(c.get(c.size() - 1).getTimestamp()))
                            .minusMonths(1))
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            if (targetMonth == null) continue;

            final YearMonth month = targetMonth;

            double sumInicio = 0;
            double sumFin = 0;
            boolean hayDatos = false;

            for (String assetId : assetIds) {

                List<Candle> mes = filterAndSortCandlesByMonth(getCandles(assetId), month);

                if (mes.isEmpty()) continue;

                sumInicio += mes.get(0).getOpen();
                sumFin += mes.get(mes.size() - 1).getClose();
                hayDatos = true;
            }

            if (!hayDatos || sumInicio == 0) continue;

            double rentFondo = ((sumFin - sumInicio) / sumInicio) * 100;
            result.put(f.getName(), rentFondo);
        }

        return result;
    }

    // ========================= HISTÓRICO =========================

    public Map<YearMonth, Double> rentabilidadUltimos6MesesFondo(Fund fund) {

        List<String> assetIds = getAssetIdsByFund(fund.getFundId());
        if (assetIds.isEmpty()) return Map.of();

        Map<YearMonth, Double> navByMonth = new TreeMap<>();

        for (String assetId : assetIds) {

            List<Candle> candles = getCandles(assetId);
            if (candles.isEmpty()) continue;

            for (Candle c : candles) {
                YearMonth ym = YearMonth.from(toLocalDate(c.getTimestamp()));
                navByMonth.merge(ym, c.getClose(), Double::sum);
            }
        }

        if (navByMonth.size() < 2) return Map.of();

        List<Map.Entry<YearMonth, Double>> sorted = new ArrayList<>(navByMonth.entrySet());

        int fromIndex = Math.max(0, sorted.size() - 6);
        List<Map.Entry<YearMonth, Double>> last6 = sorted.subList(fromIndex, sorted.size());

        Map<YearMonth, Double> result = new LinkedHashMap<>();
        Double prev = null;

        for (Map.Entry<YearMonth, Double> e : last6) {
            if (prev != null && prev != 0) {
                double rent = ((e.getValue() - prev) / Math.abs(prev)) * 100;
                result.put(e.getKey(), rent);
            } else {
                result.put(e.getKey(), 0.0);
            }
            prev = e.getValue();
        }

        return result;
    }

    // ========================= EMPRESA =========================

    public Map<YearMonth, Double> rentabilidadEmpresaMensual(List<Asset> assets) {

        if (assets == null || assets.isEmpty()) return Map.of();

        Set<YearMonth> months = new TreeSet<>();

        for (Asset a : assets) {
            for (Candle c : getCandles(a.getId())) {
                months.add(YearMonth.from(toLocalDate(c.getTimestamp())));
            }
        }

        List<YearMonth> sortedMonths = new ArrayList<>(months);
        if (sortedMonths.size() < 2) return Map.of();

        Map<YearMonth, Double> monthlyChangeSum = new LinkedHashMap<>();

        for (YearMonth month : sortedMonths) {

            double navChange = 0;

            for (Asset a : assets) {

                List<Candle> mesCandles =
                        filterAndSortCandlesByMonth(getCandles(a.getId()), month);

                if (!mesCandles.isEmpty()) {
                    double open = mesCandles.get(0).getOpen();
                    double close = mesCandles.get(mesCandles.size() - 1).getClose();
                    navChange += (close - open);
                }
            }

            monthlyChangeSum.put(month, navChange);
        }

        Map<YearMonth, Double> result = new LinkedHashMap<>();
        Double prevSum = null;

        for (Map.Entry<YearMonth, Double> e : monthlyChangeSum.entrySet()) {
            if (prevSum != null && prevSum != 0) {
                double rent = ((e.getValue() - prevSum) / Math.abs(prevSum)) * 100;
                result.put(e.getKey(), rent);
            }
            prevSum = e.getValue();
        }

        return result;
    }

    // ========================= NAV =========================

    public Map<YearMonth, Double> navEmpresaMensual(List<Asset> assets) {

        Map<YearMonth, Double> result = new LinkedHashMap<>();
        Set<YearMonth> months = new TreeSet<>();

        for (Asset a : assets) {
            for (Candle c : getCandles(a.getId())) {
                months.add(YearMonth.from(toLocalDate(c.getTimestamp())));
            }
        }

        for (YearMonth month : months) {

            double nav = 0;

            for (Asset a : assets) {

                List<Candle> mesCandles =
                        filterAndSortCandlesByMonth(getCandles(a.getId()), month);

                if (!mesCandles.isEmpty()) {
                    nav += mesCandles.get(mesCandles.size() - 1).getClose();
                }
            }

            result.put(month, nav);
        }

        return result;
    }
}