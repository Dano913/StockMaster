package org.example.paneljavafx.controller;

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

import org.example.paneljavafx.dao.AssetDAO;
import org.example.paneljavafx.dao.FundDAO;
import org.example.paneljavafx.dao.impl.AssetImpl;
import org.example.paneljavafx.dao.impl.FundImpl;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.service.*;
import org.example.paneljavafx.service.GlobalService.AssetSnapshot;
import org.example.paneljavafx.service.GlobalService.FondoSnapshot;
import org.example.paneljavafx.service.GlobalService.GlobalSnapshot;
import org.example.paneljavafx.service.GlobalService.MonthlyBar;
import org.example.paneljavafx.simulation.MarketClock;

import java.util.ArrayList;
import java.util.List;

public class GlobalController {

    // =========================
    // MODELOS DE TABLA
    // =========================

    public static class FondoRow {
        private final SimpleStringProperty nombre = new SimpleStringProperty();
        private final SimpleDoubleProperty valor  = new SimpleDoubleProperty();

        public FondoRow(FondoSnapshot s) {
            nombre.set(s.nombre());
            valor.set(s.nav());
        }

        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleDoubleProperty valorProperty()  { return valor;  }
        public double getValor() { return valor.get(); }
    }

    public static class AssetGlobalRow {
        private final SimpleStringProperty nombre = new SimpleStringProperty();
        private final SimpleDoubleProperty precio = new SimpleDoubleProperty();
        private final SimpleDoubleProperty cambio = new SimpleDoubleProperty();

        public AssetGlobalRow(AssetSnapshot s) {
            nombre.set(s.label());
            precio.set(s.precioActual());
            cambio.set(s.changePct());
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
    @FXML private Label kpiGestores;
    @FXML private Label kpiClientes;

    // =========================
    // FXML — GRÁFICOS
    // =========================
    @FXML private Canvas    barChartCanvas;
    @FXML private PieChart  globalPieChart;
    private boolean pieChartInitialized = false;

    // =========================
    // FXML — TOP FONDOS
    // =========================
    @FXML private TableView<FondoRow>           topFondsTable;
    @FXML private TableColumn<FondoRow, String> colFondoNombre;
    @FXML private TableColumn<FondoRow, Number> colFondoValor;
    @FXML private Label                         navTotalLabel;

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
    // STATE
    // =========================
    @Getter private List<Asset>        assets    = new ArrayList<>();
    @Getter private List<Fund>         funds     = new ArrayList<>();
    @Getter private List<FundAssetPosition> positions = new ArrayList<>();

    private final ObservableList<Object>         masterData   = FXCollections.observableArrayList();
    private final ObservableList<Object>         filteredData = FXCollections.observableArrayList();
    private final ObservableList<FondoRow>       fondoRows    = FXCollections.observableArrayList();
    private final ObservableList<AssetGlobalRow> assetRows    = FXCollections.observableArrayList();

    private final List<Double> portfolioHistory = new ArrayList<>();

    private double prevCapital = 0;
    private double prevFondos  = 0;
    private double prevActivos = 0;

    // =========================
    // SERVICES
    // =========================
    private final GlobalService  globalService  = new GlobalService();
    private final AssetDAO assetDAO       = new AssetImpl();
    private final FundDAO fundDAO        = new FundImpl();

    FundService    fundService    = FundService.getInstance();
    AssetService   assetService   = AssetService.getInstance();
    GestorService  gestorService  = GestorService.getInstance();
    ClientService clienteService = ClientService.getInstance();

    // =========================
// INIT
// =========================
    @FXML
    public void initialize() {

        // ← ANTES: GlobalService.LoadResult loaded = globalService.loadData(getClass());
        // ← AHORA: carga directa desde DAO
        assets    = assetDAO.findAll();
        funds     = fundDAO.findAll();
        FundPositionService.getInstance().load();

        globalService.bootstrapMarket();

        if (kpiGestores != null)
            kpiGestores.setText(String.valueOf(gestorService.getAll().size()));
        if (kpiClientes != null)
            kpiClientes.setText(String.valueOf(clienteService.getAll().size()));

        assetService.assets.clear();
        assetService.assets.addAll(assets);
        fundService.funds.clear();
        fundService.funds.addAll(funds);

        masterData.addAll(funds);
        masterData.addAll(assets);

        setupTables();
        setupSearch();
        setupCellFactory();

        resultsList.setItems(filteredData);
        filteredData.setAll(masterData);

        tick();
        MarketClock.getInstance().addListener(() -> Platform.runLater(this::tick));
    }

    // =========================
    // TICK
    // =========================
    private void tick() {
        GlobalSnapshot snap = globalService.calculateSnapshot(
                funds, assets, positions,
                prevCapital, prevFondos, prevActivos
        );

        prevCapital = snap.capitalTotal();
        prevFondos  = snap.totalFondos();
        prevActivos = snap.totalActivos();

        portfolioHistory.add(snap.capitalTotal());

        double navTotal = snap.fondos().stream()
                .mapToDouble(FondoSnapshot::nav)
                .sum();

        renderKpis(snap);
        renderTopFondos(snap.fondos(), navTotal);
        renderTopActivos(snap.activos());
        drawBarChart();

        if (!pieChartInitialized) {
            renderPieChart(snap.fondos());
            pieChartInitialized = true;
        }
    }

    // =========================
    // RENDER — KPI STRIP
    // =========================
    private void renderKpis(GlobalSnapshot snap) {
        if (kpiCapitalTotal == null) return;

        kpiCapitalTotal.setText(globalService.formatShort(snap.capitalTotal()));
        setDelta(kpiCapitalDelta, snap.deltaCapital());

        kpiRentabilidad.setText(String.format("%.2f%%", snap.rentabilidad()));
        setDelta(kpiRentabilidadDelta, snap.rentabilidad());
    }

    private void setDelta(Label label, double pct) {
        if (label == null) return;
        label.setText(String.format("%+.2f%%", pct));
        label.getStyleClass().removeAll("kpi-delta-positive", "kpi-delta-negative");
        label.getStyleClass().add(pct >= 0 ? "kpi-delta-positive" : "kpi-delta-negative");
    }

    // =========================
    // RENDER — TOP FONDOS
    // =========================
    private void renderTopFondos(List<FondoSnapshot> snapshots, double navTotal) {
        if (topFondsTable == null) return;

        if (navTotalLabel != null)
            navTotalLabel.setText(globalService.formatShort(navTotal));

        fondoRows.clear();
        snapshots.stream()
                .limit(10)
                .map(FondoRow::new)
                .forEach(fondoRows::add);
    }

    // =========================
    // RENDER — TOP ACTIVOS
    // =========================
    private void renderTopActivos(List<AssetSnapshot> snapshots) {
        if (topAssetsGlobalTable == null) return;
        assetRows.clear();
        snapshots.stream()
                .limit(10)
                .map(AssetGlobalRow::new)
                .forEach(assetRows::add);
    }

    // =========================
    // RENDER — BAR CHART
    // =========================
    private void drawBarChart() {
        if (barChartCanvas == null || barChartCanvas.getWidth() == 0) return;
        if (portfolioHistory.size() < 2) return;

        List<MonthlyBar> bars = globalService.calculateMonthlyBars(portfolioHistory, 12);
        if (bars.isEmpty()) return;

        double W = barChartCanvas.getWidth();
        double H = barChartCanvas.getHeight();
        GraphicsContext g = barChartCanvas.getGraphicsContext2D();
        g.clearRect(0, 0, W, H);

        double maxAbs = bars.stream()
                .mapToDouble(b -> Math.abs(b.returnPct())).max().orElse(1);
        if (maxAbs == 0) maxAbs = 1;

        double padX    = 10;
        double padTop  = 10;
        double padBot  = 22;
        double gap     = 4;
        int    numBars = bars.size();
        double totalW  = W - padX * 2;
        double barW    = (totalW - gap * (numBars - 1)) / numBars;
        double midY    = padTop + (H - padTop - padBot) / 2.0;
        double halfH   = (H - padTop - padBot) / 2.0;

        g.setStroke(Color.web("#2E2B3A"));
        g.setLineWidth(1);
        g.strokeLine(padX, midY, W - padX, midY);

        g.setFill(Color.web("#6B6880"));
        g.setFont(javafx.scene.text.Font.font(10));
        g.fillText(String.format("+%.1f%%", maxAbs), 0, padTop + 4);
        g.fillText("0%",                              0, midY + 4);
        g.fillText(String.format("-%.1f%%", maxAbs), 0, H - padBot + 4);

        for (int i = 0; i < numBars; i++) {
            MonthlyBar bar = bars.get(i);
            double ret    = bar.returnPct();
            double x      = padX + i * (barW + gap);
            double barH   = (Math.abs(ret) / maxAbs) * halfH;
            boolean pos   = ret >= 0;
            String color  = pos ? "#00D09C" : "#FF5C7A";
            double y      = pos ? midY - barH : midY;

            g.setFill(Color.web(pos ? "#00D09C30" : "#FF5C7A30"));
            g.fillRoundRect(x - 1, y - 1, barW + 2, barH + 2, 4, 4);

            g.setFill(Color.web(color));
            g.fillRoundRect(x, y, barW, barH, 3, 3);

            g.setFill(Color.web(color));
            g.setFont(javafx.scene.text.Font.font(9));
            double textY = pos ? midY - barH - 3 : midY + barH + 10;
            g.fillText(String.format("%+.1f%%", ret), x + barW / 2 - 10, textY);

            g.setFill(Color.web("#6B6880"));
            g.setFont(javafx.scene.text.Font.font(10));
            g.fillText(bar.label(), x + barW / 2 - 8, H - 6);
        }
    }

    // =========================
    // SETUP TABLAS
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
                setText(globalService.formatShort(v.doubleValue()));
                setStyle("-fx-text-fill: #F0EEF8; -fx-font-weight: bold;");
            }
        });

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
    // SETUP BÚSQUEDA
    // =========================
    private void setupSearch() {
        searchField.textProperty().addListener((obs, old, newVal) -> {
            List<Object> result = globalService.filter(new ArrayList<>(masterData), newVal);
            filteredData.setAll(result);
        });
    }

    private void setupCellFactory() {
        resultsList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                if (item instanceof Fund f)  setText("FUND   " + f.getName());
                if (item instanceof Asset a) setText("ASSET  " + a.getName());
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
    // RENDER — PIE CHART
    // =========================
    private void renderPieChart(List<FondoSnapshot> snapshots) {
        if (globalPieChart == null) return;

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        snapshots.stream()
                .filter(s -> s.nav() > 0)
                .forEach(s -> data.add(new PieChart.Data(s.nombre(), s.nav())));

        globalPieChart.setData(data);
    }
}