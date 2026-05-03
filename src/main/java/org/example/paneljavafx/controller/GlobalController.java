package org.example.paneljavafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import lombok.Setter;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundAssetPosition;
import org.example.paneljavafx.service.*;
import org.example.paneljavafx.service.GlobalService.GlobalSnapshot;
import org.example.paneljavafx.service.GlobalService.FondoSnapshot;
import org.example.paneljavafx.simulation.MarketClock;

import java.time.YearMonth;
import java.util.*;

public class GlobalController {

    // ================= UI =================
    @FXML private Label kpiCapitalTotal;
    @FXML private Label kpiGestores;
    @FXML private Label kpiClientes;
    @FXML private Label kpiFunds;
    @FXML private Label kpiAssets;

    @FXML private TableView<Map.Entry<String, Double>> topFondsTable;
    @FXML private TableColumn<Map.Entry<String, Double>, String> colFondoNombre;
    @FXML private TableColumn<Map.Entry<String, Double>, String> colFondoValor;

    @FXML private TableView<Map.Entry<String, Double>> topAssetsGlobalTable;
    @FXML private TableColumn<Map.Entry<String, Double>, String> colAssetNombre;
    @FXML private TableColumn<Map.Entry<String, Double>, String> colAssetCambio;

    @FXML private PieChart globalPieChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private LineChart<String, Number> lineChartNav;

    @FXML private TextField searchField;
    @FXML private ListView<Object> resultsList;

    // ================= STATE =================
    private final ObservableList<Object> masterData = FXCollections.observableArrayList();
    private final ObservableList<Object> filteredData = FXCollections.observableArrayList();

    private double prevCapital = 0;
    private double prevFondos  = 0;
    private double prevActivos = 0;

    // ================= SERVICES =================
    private final GlobalService globalService = new GlobalService();
    private final AssetService assetService = AssetService.getInstance();
    private final FundService fundService = FundService.getInstance();
    private final GestorService gestorService = GestorService.getInstance();
    private final ClientService clienteService = ClientService.getInstance();
    private final CandleService candleService = CandleService.getInstance();

    @Setter
    private AdminViewController adminController;

    private List<Asset> assets;
    private List<Fund> funds;
    private List<FundAssetPosition> positions;

    private boolean chartsInitialized = false;

    // =========================================================
    // INIT
    // =========================================================
    @FXML
    public void initialize() {

        System.out.println("\n========== GLOBAL CONTROLLER INIT ==========");

        assetService.load();
        fundService.load();

        assets = assetService.getAll();
        funds  = fundService.getAll();

        System.out.println("Assets cargados: " + assets.size());
        System.out.println("Funds cargados: "  + funds.size());

        FundAssetPositionService.getInstance().load();
        positions = FundAssetPositionService.getInstance().getAll();
        System.out.println("Positions cargadas: " + positions.size());

        gestorService.getAll();
        clienteService.load();

        kpiGestores.setText(String.valueOf(gestorService.getAll().size()));
        kpiClientes.setText(String.valueOf(clienteService.getAll().size()));

        System.out.println("Gestores: " + gestorService.getAll().size());
        System.out.println("Clientes: " + clienteService.getAll().size());

        masterData.addAll(funds);
        masterData.addAll(assets);

        filteredData.setAll(masterData);
        resultsList.setItems(filteredData);

        setupSearch();
        setupCellFactory();
        setupTableColumns();

        System.out.println("\nBOOTSTRAP MARKET...");
        globalService.bootstrapMarket();
        FundAssetPositionService.getInstance().init();

        System.out.println("Candle asset sample:");
        System.out.println(candleService.getCachedAssetRent(assets));

        MarketClock.getInstance()
                .addListener(() -> Platform.runLater(this::tick));

        initializeCharts();

        tick();
    }

    // =========================================================
    // CHART INIT
    // =========================================================
    private void initializeCharts() {

        if (chartsInitialized) return;

        System.out.println("\n========== INIT CHARTS ==========");

        System.out.println("Bar chart data:");
        var barData = candleService.rentabilidadEmpresaMensual(assets);
        System.out.println(barData);

        System.out.println("Line chart data:");
        var lineData = candleService.navEmpresaMensual(assets);
        System.out.println(lineData);

        renderBarChart(barData);
        renderLineChart(lineData);

        GlobalSnapshot snap = globalService.calculateSnapshot(
                funds, assets, positions, 0, 0, 0
        );

        System.out.println("Pie chart fondos:");
        System.out.println(snap.fondos());

        renderPieChart(snap.fondos());

        chartsInitialized = true;
    }

    // =========================================================
    // TICK
    // =========================================================
    private void tick() {

        GlobalSnapshot snap = globalService.calculateSnapshot(
                funds, assets, positions,
                prevCapital, prevFondos, prevActivos
        );

        System.out.println("\n========== TICK ==========");
        System.out.println("Capital: "      + snap.capitalTotal());
        System.out.println("Rentabilidad: " + snap.rentabilidad());
        System.out.println("Fondos: "       + snap.totalFondos());
        System.out.println("Activos: "      + snap.totalActivos());

        prevCapital = snap.capitalTotal();
        prevFondos  = snap.totalFondos();
        prevActivos = snap.totalActivos();

        renderKpis(snap);
        renderTopLists();
    }

    // =========================================================
    // KPI
    // =========================================================
    private void renderKpis(GlobalSnapshot snap) {

        kpiCapitalTotal.setText(globalService.formatShort(snap.capitalTotal()));
        kpiFunds.setText(String.valueOf(fundService.getAll().size()));
        kpiAssets.setText(String.valueOf(assetService.count()));
    }

    // =========================================================
    // TABLES
    // =========================================================
    private void setupTableColumns() {

        colFondoNombre.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getKey())
        );
        colFondoValor.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%+.2f%%", d.getValue().getValue()))
        );

        colAssetNombre.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getKey())
        );
        colAssetCambio.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%+.2f%%", d.getValue().getValue()))
        );
    }

    private void renderTopLists() {

        System.out.println("\n========== TOP LISTS ==========");

        Map<String, Double> assetRent = candleService.getCachedAssetRent(assets);
        System.out.println("Asset rent size: " + assetRent.size());

        topAssetsGlobalTable.setItems(
                FXCollections.observableArrayList(
                        assetRent.entrySet().stream()
                                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                                .limit(10)
                                .toList()
                )
        );

        Map<String, Double> fundRent = candleService.getCachedFundRent(funds);
        System.out.println("Fund rent size: " + fundRent.size());

        topFondsTable.setItems(
                FXCollections.observableArrayList(
                        fundRent.entrySet().stream()
                                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                                .limit(10)
                                .toList()
                )
        );
    }

    // =========================================================
    // CHARTS
    // =========================================================
    private void renderPieChart(List<FondoSnapshot> snapshots) {

        System.out.println("Pie snapshots: " + snapshots.size());

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        snapshots.forEach(s ->
                data.add(new PieChart.Data(s.nombre(), s.nav()))
        );

        globalPieChart.setData(data);
    }

    private void renderBarChart(Map<YearMonth, Double> dataMap) {

        System.out.println("Bar points: " + dataMap.size());

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        dataMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> series.getData().add(
                        new XYChart.Data<>(e.getKey().toString(), e.getValue())
                ));

        barChart.getData().setAll(series);
    }

    private void renderLineChart(Map<YearMonth, Double> dataMap) {

        System.out.println("Line points: " + dataMap.size());

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        dataMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> series.getData().add(
                        new XYChart.Data<>(e.getKey().toString(), e.getValue())
                ));

        lineChartNav.getData().setAll(series);
    }

    // =========================================================
    // SEARCH
    // =========================================================
    private void setupSearch() {
        searchField.textProperty().addListener((obs, o, val) -> {
            List<Object> result = globalService.filter(
                    new ArrayList<>(masterData),
                    val
            );
            filteredData.setAll(result);
        });
    }

    private void setupCellFactory() {

        resultsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                if (item instanceof Fund f)  setText("FUND  " + f.getName());
                if (item instanceof Asset a) setText("ASSET " + a.getName());
            }
        });

        resultsList.setOnMouseClicked(e -> {
            Object selected = resultsList.getSelectionModel().getSelectedItem();
            if (selected == null || adminController == null) return;

            if (selected instanceof Fund fund)   adminController.openFund(fund);
            if (selected instanceof Asset asset) adminController.openAsset(asset);
        });
    }
}