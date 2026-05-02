package org.example.paneljavafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
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
import org.example.paneljavafx.service.GlobalService.GlobalSnapshot;
import org.example.paneljavafx.service.GlobalService.FondoSnapshot;
import org.example.paneljavafx.simulation.MarketClock;

import java.util.ArrayList;
import java.util.List;

public class GlobalController {

    // =========================================================
    // KPI STRIP (UI)
    // =========================================================
    @FXML private Label kpiCapitalTotal;
    @FXML private Label kpiCapitalDelta;
    @FXML private Label kpiRentabilidad;
    @FXML private Label kpiRentabilidadDelta;
    @FXML private Label kpiGestores;
    @FXML private Label kpiClientes;
    @FXML private Label kpiCapitalTotal1;
    @FXML private Label kpiCapitalDelta1;
    @FXML private Label kpiCapitalTotal11;
    @FXML private Label kpiCapitalDelta11;

    // =========================================================
    // PIE CHART
    // =========================================================
    @FXML private PieChart globalPieChart;

    // =========================================================
    // SEARCH UI
    // =========================================================
    @FXML private TextField searchField;
    @FXML private ListView<Object> resultsList;

    @Setter
    private AdminViewController adminController;

    // =========================================================
    // STATE (DATA)
    // =========================================================
    @Getter private List<Asset> assets = new ArrayList<>();
    @Getter private List<Fund> funds = new ArrayList<>();
    @Getter private List<FundAssetPosition> positions = new ArrayList<>();

    private final ObservableList<Object> masterData = FXCollections.observableArrayList();
    private final ObservableList<Object> filteredData = FXCollections.observableArrayList();

    private double prevCapital = 0;
    private double prevFondos  = 0;
    private double prevActivos = 0;

    // =========================================================
    // SERVICES
    // =========================================================
    private final GlobalService globalService = new GlobalService();
    private final AssetDAO assetDAO = new AssetImpl();
    private final FundDAO fundDAO = new FundImpl();

    private final FundService fundService = FundService.getInstance();
    private final AssetService assetService = AssetService.getInstance();
    private final GestorService gestorService = GestorService.getInstance();
    private final ClientService clienteService = ClientService.getInstance();

    // =========================================================
    // INIT
    // =========================================================
    @FXML
    public void initialize() {

        // DATA LOAD
        assets = assetDAO.findAll();
        funds  = fundDAO.findAll();
        FundAssetPositionService.getInstance().load();

        positions = FundAssetPositionService.getInstance().getAll();

        globalService.bootstrapMarket();

        // KPI STATIC
        gestorService.getAll(); // asegura carga si lazy
        clienteService.load();  // ← necesario antes de .size()

        if (kpiGestores != null)
            kpiGestores.setText(String.valueOf(gestorService.getAll().size()));

        if (kpiClientes != null)
            kpiClientes.setText(String.valueOf(clienteService.getAll().size()));

        // SERVICE SYNC
        assetService.assets.clear();
        assetService.assets.addAll(assets);

        fundService.funds.clear();
        fundService.funds.addAll(funds);

        // SEARCH DATA
        masterData.addAll(funds);
        masterData.addAll(assets);

        setupSearch();
        setupCellFactory();

        resultsList.setItems(filteredData);
        filteredData.setAll(masterData);

        Platform.runLater(() -> {
            tick();
            MarketClock.getInstance()
                    .addListener(() -> Platform.runLater(this::tick));
        });
    }

    // =========================================================
    // MAIN LOOP
    // =========================================================
    private void tick() {

        GlobalSnapshot snap = globalService.calculateSnapshot(
                funds, assets, positions,
                prevCapital, prevFondos, prevActivos
        );

        prevCapital = snap.capitalTotal();
        prevFondos  = snap.totalFondos();
        prevActivos = snap.totalActivos();

        renderKpis(snap);
        renderPieChart(snap.fondos());
    }

    private void renderKpis(GlobalSnapshot snap) {

        if (kpiCapitalTotal == null) return;

        kpiCapitalTotal.setText(globalService.formatShort(snap.capitalTotal()));
        setDelta(kpiCapitalDelta, snap.deltaCapital());

        kpiRentabilidad.setText(String.format("%.2f%%", snap.rentabilidad()));
        setDelta(kpiRentabilidadDelta, snap.rentabilidad());

        if (kpiCapitalTotal1 != null)
            kpiCapitalTotal1.setText(String.valueOf(funds.size()));
        if (kpiCapitalDelta1 != null)
            kpiCapitalDelta1.setVisible(false);

        if (kpiCapitalTotal11 != null)
            kpiCapitalTotal11.setText(String.valueOf(assets.size()));
        if (kpiCapitalDelta11 != null)
            kpiCapitalDelta11.setVisible(false);
    }

    private void setDelta(Label label, double pct) {
        if (label == null) return;

        label.setText(String.format("%+.2f%%", pct));

        label.getStyleClass().removeAll(
                "kpi-delta-positive",
                "kpi-delta-negative"
        );

        label.getStyleClass().add(
                pct >= 0 ? "kpi-delta-positive" : "kpi-delta-negative"
        );
    }

    // =========================================================
    // SEARCH
    // =========================================================
    private void setupSearch() {

        searchField.textProperty().addListener((obs, old, val) -> {
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
                    setStyle("");
                    return;
                }

                if (item instanceof Fund f)
                    setText("FUND   " + f.getName());

                if (item instanceof Asset a)
                    setText("ASSET  " + a.getName());
            }
        });

        resultsList.setOnMouseClicked(e -> {

            Object selected = resultsList.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if (selected instanceof Fund fund)
                adminController.openFund(fund);

            if (selected instanceof Asset asset)
                adminController.openAsset(asset);
        });
    }

    // =========================================================
    // PIE CHART
    // =========================================================
    private void renderPieChart(List<FondoSnapshot> snapshots) {

        if (globalPieChart == null) return;

        double navTotal = snapshots.stream()
                .mapToDouble(FondoSnapshot::nav)
                .sum();

        if (navTotal <= 0) {
            System.out.println("⚠ PieChart: navTotal = 0, sin datos");
            return;
        }

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        snapshots.stream()
                .filter(s -> s.nav() > 0)
                .forEach(s -> {
                    System.out.printf("🍕 Fondo: %s  NAV: %.2f%n", s.nombre(), s.nav());
                    data.add(new PieChart.Data(
                            String.format("%s (%.1f%%)", s.nombre(), (s.nav() / navTotal) * 100),
                            s.nav()
                    ));
                });

        globalPieChart.setData(data);
        globalPieChart.setAnimated(false);
    }
}