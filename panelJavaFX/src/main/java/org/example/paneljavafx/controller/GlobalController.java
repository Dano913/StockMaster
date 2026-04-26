package org.example.paneljavafx.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import lombok.Getter;
import lombok.Setter;

import org.example.paneljavafx.data.DataStore;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.service.MarketService;

import java.util.ArrayList;
import java.util.List;

public class GlobalController {

    // =========================
    // UI
    // =========================
    @FXML private TextField searchField;
    @FXML private ListView<Object> resultsList;

    @Setter
    private AdminViewController adminController;

    // =========================
    // DATA UI
    // =========================
    private final ObservableList<Object> masterData   = FXCollections.observableArrayList();
    private final ObservableList<Object> filteredData = FXCollections.observableArrayList();

    @Getter private List<Asset> assets = new ArrayList<>();
    @Getter private List<Fund> funds = new ArrayList<>();

    // =========================
    // SERVICES
    // =========================
    private final MarketService marketService = new MarketService();

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {

        loadData();         // solo JSON UI data
        bootstrapMarket();  // delega al service

        resultsList.setItems(filteredData);

        setupCellFactory();
        setupSearch();

        filteredData.setAll(masterData);

        // 🔥 Sync global state
        DataStore.assets.clear();
        DataStore.assets.addAll(assets);

        DataStore.funds.clear();
        DataStore.funds.addAll(funds);
    }

    // =========================
    // DATA LOADING (solo UI layer)
    // =========================
    private void loadData() {

        try {
            ObjectMapper mapper = new ObjectMapper();

            var fundsStream  = getClass().getResourceAsStream("/data/funds.json");
            var assetsStream = getClass().getResourceAsStream("/data/assets.json");

            if (fundsStream != null) {
                funds = mapper.readValue(fundsStream, new TypeReference<>() {});
                masterData.addAll(funds);
            }

            if (assetsStream != null) {
                assets = mapper.readValue(assetsStream, new TypeReference<>() {});
                masterData.addAll(assets);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // MARKET BOOTSTRAP (DELEGATED)
    // =========================
    private void bootstrapMarket() {

        marketService.bootstrapMarket()
                .forEach(engine ->
                        DataStore.engines.put(engine.getAsset().getId(), engine)
                );

        System.out.println("🚀 GlobalController: Market inicializado");
    }

    // =========================
    // SEARCH (UI ONLY)
    // =========================
    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal == null || newVal.isBlank()) {
                filteredData.setAll(masterData);
                return;
            }

            String q = newVal.toLowerCase();

            filteredData.setAll(
                    masterData.stream()
                            .filter(item -> matches(item, q))
                            .toList()
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
    // CELL FACTORY (UI ONLY)
    // =========================
    private void setupCellFactory() {

        resultsList.setCellFactory(listView -> new ListCell<>() {

            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                if (item instanceof Fund f) {
                    setText("💰 FUND - " + f.getNombre());
                } else if (item instanceof Asset a) {
                    setText("📈 ASSET - " + a.getName());
                }

                setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: cyan;
                    -fx-font-size: 16px;
                    -fx-padding: 8;
                    -fx-border-color: rgba(0,255,255,0.2);
                """);
            }
        });

        resultsList.setOnMouseClicked(event -> {

            Object selected = resultsList.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if (selected instanceof Fund fund) {
                adminController.openFund(fund);
            }

            if (selected instanceof Asset asset) {
                adminController.openAsset(asset);
            }
        });
    }
}