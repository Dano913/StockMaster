package org.example.paneljavafx.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.paneljavafx.data.FundPositionDataSource;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.simulation.MarketEngine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * GlobalService — lógica de negocio del dashboard global.
 *
 * Responsabilidades:
 *  - Carga de datos (JSON → modelos)
 *  - Bootstrap del mercado
 *  - Cálculo de posición total (NAV fondos + activos + deltas + rentabilidad)
 *  - Producción de DTOs para las tablas (FondoSnapshot, AssetSnapshot)
 *  - Cálculo de rentabilidad mensual para el bar chart
 *  - Filtrado de búsqueda
 *  - Formateo de valores (K / M / B)
 */
public class GlobalService {

    // ═══════════════════════════════════════════════════
    // DTOs DE SALIDA
    // ═══════════════════════════════════════════════════

    /** Snapshot de un fondo para la tabla y el pie chart */
    public record FondoSnapshot(
            String id,
            String nombre,
            double nav,
            double invertido,
            double rentabilidadPct    // (nav - invertido) / invertido * 100
    ) {}

    /** Snapshot de un activo para la tabla top activos */
    public record AssetSnapshot(
            String label,             // ticker o nombre
            double precioActual,
            double changePct          // cambio % del último tick
    ) {}

    /** Resultado completo de un tick de recálculo */
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

    /** Barra de rentabilidad mensual para el bar chart */
    public record MonthlyBar(
            String label,             // "Ene", "Feb", …
            double returnPct
    ) {}

    // ═══════════════════════════════════════════════════
    // SERVICIOS DELEGADOS
    // ═══════════════════════════════════════════════════
    MarketService marketService = MarketService.getInstance();
    FundService fundService = FundService.getInstance();
    private final ExposureService exposureService = new ExposureService();
    private final AssetService    assetService    = AssetService.getInstance();

    private final DecimalFormat DF = new DecimalFormat("#,###.##");

    // ═══════════════════════════════════════════════════
    // CARGA DE DATOS
    // ═══════════════════════════════════════════════════

    public record LoadResult(
            List<Fund>         funds,
            List<Asset>        assets,
            List<FundPosition> positions
    ) {}

    /**
     * Carga funds y assets desde JSON, y las posiciones desde FundPositionLoader.
     * No modifica ningún estado global — el controller decide
     * qué hacer con el resultado (poblar DataStore, masterData, etc.).
     */
    public LoadResult loadData(Class<?> callerClass) {
        List<Fund>         funds     = new ArrayList<>();
        List<Asset>        assets    = new ArrayList<>();
        List<FundPosition> positions = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            var fundsStream  = callerClass.getResourceAsStream("/data/funds.json");
            var assetsStream = callerClass.getResourceAsStream("/data/assets.json");

            if (fundsStream  != null) funds  = mapper.readValue(fundsStream,  new TypeReference<>() {});
            if (assetsStream != null) assets = mapper.readValue(assetsStream, new TypeReference<>() {});

            // Posiciones via FundPositionLoader (parsea fund_positions.json con su lógica propia)
            positions = FundPositionDataSource.load();

        } catch (Exception e) {
            System.err.println("❌ GlobalService.loadData error: " + e.getMessage());
            e.printStackTrace();
        }

        return new LoadResult(funds, assets, positions);
    }

    // ═══════════════════════════════════════════════════
    // BOOTSTRAP DE MERCADO
    // ═══════════════════════════════════════════════════

    /**
     * Inicializa los MarketEngine para todos los assets y los registra en DataStore.
     */
    public void bootstrapMarket() {
        marketService.bootstrapMarket();
    }

    // ═══════════════════════════════════════════════════
    // CÁLCULO DE POSICIÓN TOTAL
    // ═══════════════════════════════════════════════════

    /**
     * Calcula el snapshot global completo en un tick.
     *
     * @param funds     lista de fondos del sistema
     * @param assets    lista de assets del sistema
     * @param positions todas las FundPosition del sistema
     * @param prevCapital  capital total del tick anterior (0 en el primero)
     * @param prevFondos   NAV fondos del tick anterior
     * @param prevActivos  valor activos del tick anterior
     */
    public GlobalSnapshot calculateSnapshot(
            List<Fund>         funds,
            List<Asset>        assets,
            List<FundPosition> positions,
            double prevCapital,
            double prevFondos,
            double prevActivos
    ) {
        // ── NAV total de fondos ──────────────────────────────────
        double totalFondos = funds.stream()
                .mapToDouble(fund -> {
                    List<FundPosition> posFund =
                            fundService.getPositionsByFund(positions, fund.getIdFondo());
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

    // ═══════════════════════════════════════════════════
    // SNAPSHOTS DE FONDOS
    // ═══════════════════════════════════════════════════

    /**
     * Produce un FondoSnapshot por cada fondo, ordenados por NAV descendente.
     * Loguea cada fondo para depuración.
     */
    public List<FondoSnapshot> buildFondoSnapshots(
            List<Fund> funds,
            List<FundPosition> positions
    ) {
        return funds.stream()
                .map(fund -> {
                    List<FundPosition> posFund =
                            fundService.getPositionsByFund(positions, fund.getIdFondo());

                    double nav = fundService.calculateTotalNAV(posFund);

                    double invertido = posFund.stream()
                            .filter(FundPosition::isValid)
                            .mapToDouble(FundPosition::getInvestedValue)
                            .sum();

                    double rentabilidad = invertido > 0
                            ? ((nav - invertido) / invertido) * 100 : 0;

//                    System.out.printf("📊 Fondo [%s] NAV: %s € | Rentab: %+.2f%%%n",
//                            fund.getNombre(), formatShort(nav), rentabilidad);

                    return new FondoSnapshot(
                            fund.getIdFondo(),
                            fund.getNombre(),
                            nav,
                            invertido,
                            rentabilidad
                    );
                })
                .sorted(Comparator.comparingDouble(FondoSnapshot::nav).reversed())
                .toList();
    }

    // ═══════════════════════════════════════════════════
    // SNAPSHOTS DE ACTIVOS
    // ═══════════════════════════════════════════════════

    /**
     * Produce un AssetSnapshot por cada asset con engine activo,
     * ordenados por mayor movimiento absoluto descendente.
     */
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

    // ═══════════════════════════════════════════════════
    // RENTABILIDAD MENSUAL
    // ═══════════════════════════════════════════════════

    /**
     * Divide el portfolioHistory en hasta maxBars buckets y calcula
     * la rentabilidad porcentual de cada uno como barra mensual.
     *
     * @param history   lista de valores de capital total (un valor por tick)
     * @param maxBars   máximo de barras a devolver (normalmente 12)
     */
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

    // ═══════════════════════════════════════════════════
    // BÚSQUEDA / FILTRADO
    // ═══════════════════════════════════════════════════

    /**
     * Filtra la lista maestra de objetos (Fund + Asset) según la query.
     * Devuelve todos si la query es nula o vacía.
     */
    public List<Object> filter(List<Object> masterData, String query) {
        if (query == null || query.isBlank()) return new ArrayList<>(masterData);

        String q = query.toLowerCase();
        return masterData.stream()
                .filter(item -> matches(item, q))
                .toList();
    }

    private boolean matches(Object item, String q) {
        if (item instanceof Fund f) {
            return f.getNombre().toLowerCase().contains(q)
                    || f.getTipo().toLowerCase().contains(q);
        }
        if (item instanceof Asset a) {
            return a.getName().toLowerCase().contains(q)
                    || (a.getTicker()  != null && a.getTicker().toLowerCase().contains(q))
                    || (a.getIsin()    != null && a.getIsin().toLowerCase().contains(q))
                    || (a.getSector()  != null && a.getSector().toLowerCase().contains(q));
        }
        return false;
    }

    // ═══════════════════════════════════════════════════
    // HELPERS DE FORMATO (reutilizables desde el controller)
    // ═══════════════════════════════════════════════════

    /** Formatea un valor en K / M / B con símbolo € */
    public String formatShort(double v) {
        if (v >= 1_000_000_000) return String.format("%.2fB €", v / 1_000_000_000);
        if (v >= 1_000_000)     return String.format("%.2fM €", v / 1_000_000);
        if (v >= 1_000)         return String.format("%.1fK €", v / 1_000);
        return DF.format(v) + " €";
    }

    /** Trunca strings para etiquetas de gráficos */
    public String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}