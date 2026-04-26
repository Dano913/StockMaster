package org.example.paneljavafx.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import lombok.Getter;
import lombok.Setter;

import org.example.paneljavafx.data.DataStore;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.service.AssetService;
import org.example.paneljavafx.service.ExposureService;
import org.example.paneljavafx.service.FundService;
import org.example.paneljavafx.service.MarketService;
import org.example.paneljavafx.service.dto.AssetMetrics;
import org.example.paneljavafx.service.dto.FundMetrics;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.simulation.MarketEngine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GlobalController {

    // =========================
    // MODELOS INTERNOS TABLA
    // =========================

    public static class FondoRow {
        private final SimpleStringProperty nombre = new SimpleStringProperty();
        private final SimpleDoubleProperty valor  = new SimpleDoubleProperty();
        private final SimpleDoubleProperty cambio = new SimpleDoubleProperty();

        public FondoRow(String nombre, double valor, double cambio) {
            this.nombre.set(nombre);
            this.valor.set(valor);
            this.cambio.set(cambio);
        }

        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleDoubleProperty valorProperty()  { return valor;  }
        public SimpleDoubleProperty cambioProperty() { return cambio; }
        public double getValor()  { return valor.get(); }
        public double getCambio() { return cambio.get(); }
    }

    public static class AssetGlobalRow {
        private final SimpleStringProperty nombre = new SimpleStringProperty();
        private final SimpleDoubleProperty precio = new SimpleDoubleProperty();
        private final SimpleDoubleProperty cambio = new SimpleDoubleProperty();

        public AssetGlobalRow(String nombre, double precio, double cambio) {
            this.nombre.set(nombre);
            this.precio.set(precio);
            this.cambio.set(cambio);
        }

        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleDoubleProperty precioProperty() { return precio; }
        public SimpleDoubleProperty cambioProperty() { return cambio; }
        public double getCambio() { return cambio.get(); }
    }

    // =========================
    // FXML — KPI STRIP
    // =========================
    @FXML private Label kpiCapitalTotal;
    @FXML private Label kpiCapitalDelta;
    @FXML private Label kpiRentabilidad;
    @FXML private Label kpiRentabilidadDelta;
    @FXML private Label kpiFondos;
    @FXML private Label kpiFondosDelta;
    @FXML private Label kpiActivos;
    @FXML private Label kpiActivosDelta;
    @FXML private Label kpiFlujo;
    @FXML private Label kpiFlujoBar;

    // =========================
    // FXML — GRÁFICOS
    // =========================
    @FXML private Canvas   globalChartCanvas;
    @FXML private Canvas   barChartCanvas;
    @FXML private PieChart globalPieChart;

    // =========================
    // FXML — TOP FONDOS
    // =========================
    @FXML private TableView<FondoRow>           topFondsTable;
    @FXML private TableColumn<FondoRow, String> colFondoNombre;
    @FXML private TableColumn<FondoRow, Number> colFondoValor;
    @FXML private TableColumn<FondoRow, Number> colFondoCambio;

    // =========================
    // FXML — TOP ACTIVOS
    // =========================
    @FXML private TableView<AssetGlobalRow>           topAssetsGlobalTable;
    @FXML private TableColumn<AssetGlobalRow, String> colAssetNombre;
    @FXML private TableColumn<AssetGlobalRow, Number> colAssetPrecio;
    @FXML private TableColumn<AssetGlobalRow, Number> colAssetCambio;

    // =========================
    // FXML — SEARCH
    // =========================
    @FXML private TextField        searchField;
    @FXML private ListView<Object> resultsList;

    @Setter
    private AdminViewController adminController;

    // =========================
    // DATA
    // =========================
    @Getter private List<Asset>        assets    = new ArrayList<>();
    @Getter private List<Fund>         funds     = new ArrayList<>();
    @Getter private List<FundPosition> positions = new ArrayList<>();

    private final ObservableList<Object>         masterData   = FXCollections.observableArrayList();
    private final ObservableList<Object>         filteredData = FXCollections.observableArrayList();
    private final ObservableList<FondoRow>       fondoRows    = FXCollections.observableArrayList();
    private final ObservableList<AssetGlobalRow> assetRows    = FXCollections.observableArrayList();

    // Histórico de capital total para el gráfico de línea
    private final List<Double> portfolioHistory = new ArrayList<>();

    // Snapshots del tick anterior para calcular deltas
    private double prevCapital  = 0;
    private double prevFondos   = 0;
    private double prevActivos  = 0;

    // =========================
    // SERVICES
    // =========================
    private final MarketService   marketService   = new MarketService();
    private final FundService     fundService     = new FundService();
    private final ExposureService exposureService = new ExposureService();
    private final AssetService    assetService    = new AssetService();

    private final DecimalFormat DF = new DecimalFormat("#,###.##");

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {

        loadData();
        bootstrapMarket();

        setupTables();
        setupSearch();
        setupCellFactory();

        resultsList.setItems(filteredData);
        filteredData.setAll(masterData);

        DataStore.assets.clear();
        DataStore.assets.addAll(assets);
        DataStore.funds.clear();
        DataStore.funds.addAll(funds);

        // Primer render + bind al MarketClock
        recalculatePosition();
        MarketClock.getInstance().addListener(() ->
                Platform.runLater(this::recalculatePosition)
        );
    }

    // =========================
    // DATA LOADING
    // =========================
    private void loadData() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            var fundsStream     = getClass().getResourceAsStream("/data/funds.json");
            var assetsStream    = getClass().getResourceAsStream("/data/assets.json");
            var positionsStream = getClass().getResourceAsStream("/data/positions.json");

            if (fundsStream != null) {
                funds = mapper.readValue(fundsStream, new TypeReference<>() {});
                masterData.addAll(funds);
            }

            if (assetsStream != null) {
                assets = mapper.readValue(assetsStream, new TypeReference<>() {});
                masterData.addAll(assets);
            }

            // Si tienes un JSON con todas las FundPositions del sistema
            if (positionsStream != null) {
                positions = mapper.readValue(positionsStream, new TypeReference<>() {});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // MARKET BOOTSTRAP
    // =========================
    private void bootstrapMarket() {
        marketService.bootstrapMarket()
                .forEach(engine ->
                        DataStore.engines.put(engine.getAsset().getId(), engine)
                );
        System.out.println("🚀 GlobalController: Market inicializado");
    }

    // =========================
    // CÁLCULO POSICIÓN TOTAL
    // ─────────────────────────
    // Delega en FundService para el NAV de cada fondo
    // y en MarketEngine.getLastPrice() para los assets directos.
    // ExposureService calcula la exposición total del sistema.
    // =========================
    private void recalculatePosition() {

        // ── 1. NAV de cada fondo vía FundService ────────────────
        double totalFondos = funds.stream()
                .mapToDouble(fund -> {
                    List<FundPosition> posFund =
                            fundService.getPositionsByFund(positions, fund.getIdFondo());

                    FundMetrics metrics = fundService.calculateMetrics(
                            fund, posFund, 0   // previousValue=0 → changePct irrelevante aquí
                    );
                    return metrics.getTotalValue();
                })
                .sum();

        // ── 2. Valor de activos vía AssetService ─────────────────
        // AssetService.calculateMetrics suma quantity × engine.getLastPrice()
        // para todas las FundPositions de ese asset.
        double totalActivos = assets.stream()
                .mapToDouble(asset ->
                        assetService.calculateMetrics(positions, asset.getId())
                                .getTotalExposure()
                )
                .sum();

        // ── 3. Exposición total del sistema vía ExposureService ──
        // (suma de todas las posiciones de todos los fondos)
        double exposicionTotal = exposureService.calculateTotalExposure(positions);

        double capitalTotal = totalFondos + totalActivos;

        // ── 4. Deltas tick a tick ────────────────────────────────
        double deltaCapital = prevCapital > 0
                ? ((capitalTotal - prevCapital) / prevCapital) * 100 : 0;
        double deltaFondos  = prevFondos > 0
                ? ((totalFondos - prevFondos) / prevFondos) * 100 : 0;
        double deltaActivos = prevActivos > 0
                ? ((totalActivos - prevActivos) / prevActivos) * 100 : 0;

        // ── 5. Rentabilidad global acumulada ─────────────────────
        // Exposición (coste invertido) vs valor actual
        double rentabilidad = exposicionTotal > 0
                ? ((capitalTotal - exposicionTotal) / exposicionTotal) * 100
                : 0;

        prevCapital  = capitalTotal;
        prevFondos   = totalFondos;
        prevActivos  = totalActivos;

        portfolioHistory.add(capitalTotal);

        // ── 6. Actualizar UI ─────────────────────────────────────
        updateKpis(capitalTotal, deltaCapital, totalFondos, deltaFondos,
                totalActivos, deltaActivos, rentabilidad);

        updateTopFondos();
        updateTopActivos();
        updatePieChart(totalFondos, totalActivos);
        drawLineChart();
        drawBarChart();
    }

    // =========================
    // KPI STRIP
    // =========================
    private void updateKpis(double capital, double dCapital,
                            double fondos,  double dFondos,
                            double activos, double dActivos,
                            double rentabilidad) {

        if (kpiCapitalTotal == null) return;

        kpiCapitalTotal.setText(formatShort(capital));
        setDelta(kpiCapitalDelta, dCapital);

        kpiRentabilidad.setText(String.format("%.2f%%", rentabilidad));
        setDelta(kpiRentabilidadDelta, rentabilidad);

        kpiFondos.setText(formatShort(fondos));
        setDelta(kpiFondosDelta, dFondos);

        kpiActivos.setText(formatShort(activos));
        setDelta(kpiActivosDelta, dActivos);

        // Flujo: peso de fondos sobre capital total
        double pesoFondos = capital > 0 ? (fondos / capital) * 100 : 0;
        kpiFlujo.setText(String.format("%.1f%%", pesoFondos) + " fondos");
        setDelta(kpiFlujoBar, dCapital);
    }

    private void setDelta(Label label, double pct) {
        if (label == null) return;
        label.setText(String.format("%+.2f%%", pct));
        label.getStyleClass().removeAll("kpi-delta-positive", "kpi-delta-negative");
        label.getStyleClass().add(pct >= 0 ? "kpi-delta-positive" : "kpi-delta-negative");
    }

    // =========================
    // TOP FONDOS
    // Usa FundService.calculateMetrics por fondo
    // =========================
    private void updateTopFondos() {
        if (topFondsTable == null) return;

        fondoRows.clear();

        funds.stream()
                .map(fund -> {
                    List<FundPosition> posFund =
                            fundService.getPositionsByFund(positions, fund.getIdFondo());

                    // previousValue = 0 para que changePct refleje rentabilidad vs coste
                    FundMetrics metrics = fundService.calculateMetrics(fund, posFund, 0);

                    return new FondoRow(
                            fund.getNombre(),
                            metrics.getTotalValue(),
                            metrics.getChangePct()
                    );
                })
                .sorted(Comparator.comparingDouble(FondoRow::getValor).reversed())
                .limit(10)
                .forEach(fondoRows::add);
    }

    // =========================
    // TOP ACTIVOS
    // Usa MarketEngine.getLastPrice() + getChange()
    // =========================
    private void updateTopActivos() {
        if (topAssetsGlobalTable == null) return;

        assetRows.clear();

        assets.stream()
                .map(asset -> {
                    MarketEngine engine = DataStore.engines.get(asset.getId());
                    if (engine == null) return null;

                    String label = asset.getTicker() != null
                            ? asset.getTicker() : asset.getName();

                    return new AssetGlobalRow(
                            label,
                            engine.getLastPrice(),   // precio actual vía engine
                            engine.getChange()       // cambio % del último tick vía engine
                    );
                })
                .filter(r -> r != null)
                .sorted(Comparator.comparingDouble(
                        (AssetGlobalRow r) -> Math.abs(r.getCambio())
                ).reversed())
                .limit(10)
                .forEach(assetRows::add);
    }

    // =========================
    // PIE CHART (fondos vs activos)
    // =========================
    private void updatePieChart(double fondos, double activos) {
        if (globalPieChart == null) return;

        globalPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Fondos",  fondos),
                new PieChart.Data("Activos", activos)
        ));
    }

    // =========================
    // BARCHART — EXPOSICIÓN POR SECTOR
    // Usa AssetService.calculateMetrics agrupando por sector del Asset
    // =========================
    private void drawBarChart() {
        if (barChartCanvas == null || barChartCanvas.getWidth() == 0) return;

        // Agrupa assets por sector y suma exposición vía AssetService
        Map<String, Double> sectorMap = assets.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getSector() != null ? a.getSector() : "Otros",
                        Collectors.summingDouble(a ->
                                assetService.calculateMetrics(positions, a.getId())
                                        .getTotalExposure()
                        )
                ));

        if (sectorMap.isEmpty()) return;

        double W = barChartCanvas.getWidth();
        double H = barChartCanvas.getHeight();
        GraphicsContext g = barChartCanvas.getGraphicsContext2D();
        g.clearRect(0, 0, W, H);

        double maxVal = sectorMap.values().stream().mapToDouble(d -> d).max().orElse(1);
        String[] colors = { "#6C63FF", "#00D09C", "#FFB347", "#FF5C7A", "#4ECDC4", "#A09CB8" };

        List<Map.Entry<String, Double>> entries = sectorMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(6)
                .toList();

        int total = entries.size();
        double barH = (H - 10) / total - 6;
        double padX = 8;
        double labelW = 70;

        for (int i = 0; i < total; i++) {
            var entry  = entries.get(i);
            double pct = entry.getValue() / maxVal;
            double y   = i * (barH + 6) + 5;
            double barW = (W - padX * 2 - labelW) * pct;

            // Track
            g.setFill(Color.web("#25223080"));
            g.fillRoundRect(padX + labelW, y, W - padX * 2 - labelW, barH, 4, 4);

            // Fill
            g.setFill(Color.web(colors[i % colors.length]));
            g.fillRoundRect(padX + labelW, y, barW, barH, 4, 4);

            // Etiqueta sector
            g.setFill(Color.web("#A09CB8"));
            g.setFont(javafx.scene.text.Font.font(11));
            g.fillText(truncate(entry.getKey(), 9), padX, y + barH / 2.0 + 4);

            // Valor
            g.setFill(Color.web("#F0EEF8"));
            g.fillText(formatShort(entry.getValue()), padX + labelW + barW + 4, y + barH / 2.0 + 4);
        }
    }

    // =========================
    // GRÁFICO DE LÍNEA GLOBAL
    // =========================
    private void drawLineChart() {
        if (globalChartCanvas == null || globalChartCanvas.getWidth() == 0) return;
        if (portfolioHistory.size() < 2) return;

        double W = globalChartCanvas.getWidth();
        double H = globalChartCanvas.getHeight();
        GraphicsContext g = globalChartCanvas.getGraphicsContext2D();
        g.clearRect(0, 0, W, H);

        List<Double> data = portfolioHistory;
        int n    = data.size();
        double mn = data.stream().mapToDouble(d -> d).min().orElse(0) * 0.998;
        double mx = data.stream().mapToDouble(d -> d).max().orElse(1) * 1.002;

        // Grid
        g.setStroke(Color.web("#2E2B3A"));
        g.setLineWidth(0.5);
        for (int i = 0; i <= 4; i++) {
            double y = H - (i / 4.0) * H;
            g.strokeLine(0, y, W, y);
        }

        double[] xs = new double[n];
        double[] ys = new double[n];
        for (int i = 0; i < n; i++) {
            xs[i] = (i / (double)(n - 1)) * W;
            ys[i] = H - ((data.get(i) - mn) / (mx - mn)) * (H - 10) - 5;
        }

        // Área
        g.setFill(Color.web("#6C63FF22"));
        g.beginPath();
        g.moveTo(xs[0], H);
        for (int i = 0; i < n; i++) g.lineTo(xs[i], ys[i]);
        g.lineTo(xs[n - 1], H);
        g.closePath();
        g.fill();

        // Línea
        g.setStroke(Color.web("#6C63FF"));
        g.setLineWidth(1.5);
        g.beginPath();
        g.moveTo(xs[0], ys[0]);
        for (int i = 1; i < n; i++) g.lineTo(xs[i], ys[i]);
        g.stroke();

        // Punto final
        g.setFill(Color.web("#6C63FF"));
        g.fillOval(xs[n - 1] - 4, ys[n - 1] - 4, 8, 8);
    }

    // =========================
    // TABLAS — SETUP
    // =========================
    private void setupTables() {
        setupFondosTable();
        setupActivosTable();
    }

    private void setupFondosTable() {
        if (topFondsTable == null) return;

        colFondoNombre.setCellValueFactory(c -> c.getValue().nombreProperty());

        colFondoValor.setCellValueFactory(c -> c.getValue().valorProperty());
        colFondoValor.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(formatShort(v.doubleValue()));
                setStyle("-fx-text-fill: #F0EEF8;");
            }
        });

        colFondoCambio.setCellValueFactory(c -> c.getValue().cambioProperty());
        colFondoCambio.setCellFactory(col -> pctCell());

        topFondsTable.setItems(fondoRows);
        topFondsTable.setPlaceholder(new Label("Sin fondos"));
    }

    private void setupActivosTable() {
        if (topAssetsGlobalTable == null) return;

        colAssetNombre.setCellValueFactory(c -> c.getValue().nombreProperty());

        colAssetPrecio.setCellValueFactory(c -> c.getValue().precioProperty());
        colAssetPrecio.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(String.format("%.2f €", v.doubleValue()));
                setStyle("-fx-text-fill: #F0EEF8;");
            }
        });

        colAssetCambio.setCellValueFactory(c -> c.getValue().cambioProperty());
        colAssetCambio.setCellFactory(col -> pctCell());

        topAssetsGlobalTable.setItems(assetRows);
        topAssetsGlobalTable.setPlaceholder(new Label("Sin activos"));
    }

    /** CellFactory reutilizable para columnas de porcentaje con color */
    private <T> TableCell<T, Number> pctCell() {
        return new TableCell<>() {
            @Override protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                double pct = v.doubleValue();
                setText(String.format("%+.2f%%", pct));
                setStyle(pct >= 0
                        ? "-fx-text-fill: #00D09C; -fx-font-weight: bold;"
                        : "-fx-text-fill: #FF5C7A; -fx-font-weight: bold;");
            }
        };
    }

    // =========================
    // SEARCH
    // =========================
    private void setupSearch() {
        searchField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                filteredData.setAll(masterData);
                return;
            }
            String q = newVal.toLowerCase();
            filteredData.setAll(
                    masterData.stream().filter(item -> matches(item, q)).toList()
            );
        });
    }

    private boolean matches(Object item, String q) {
        if (item instanceof Fund f) {
            return f.getNombre().toLowerCase().contains(q)
                    || f.getTipo().toLowerCase().contains(q);
        }
        if (item instanceof Asset a) {
            return a.getName().toLowerCase().contains(q)
                    || a.getTicker().toLowerCase().contains(q)
                    || a.getIsin().toLowerCase().contains(q)
                    || a.getSector().toLowerCase().contains(q);
        }
        return false;
    }

    // =========================
    // CELL FACTORY SEARCH LIST
    // =========================
    private void setupCellFactory() {
        resultsList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                if (item instanceof Fund f)   setText("FUND   " + f.getNombre());
                if (item instanceof Asset a)  setText("ASSET  " + a.getName());
            }
        });

        resultsList.setOnMouseClicked(event -> {
            Object selected = resultsList.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            if (selected instanceof Fund fund)   adminController.openFund(fund);
            if (selected instanceof Asset asset) adminController.openAsset(asset);
        });
    }

    // =========================
    // HELPERS
    // =========================

    /** Formatea en K / M / B */
    private String formatShort(double v) {
        if (v >= 1_000_000_000) return String.format("%.2fB €", v / 1_000_000_000);
        if (v >= 1_000_000)     return String.format("%.2fM €", v / 1_000_000);
        if (v >= 1_000)         return String.format("%.1fK €", v / 1_000);
        return DF.format(v) + " €";
    }

    /** Trunca strings largos para las etiquetas del bar chart */
    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}