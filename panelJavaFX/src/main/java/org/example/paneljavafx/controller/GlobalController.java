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
import org.example.paneljavafx.data.PriceRecordReader;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.simulation.MarketClock;
import org.example.paneljavafx.simulation.MarketEngine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GlobalController {

    // -------------------------
    // UI
    // -------------------------
    @FXML private TextField searchField;
    @FXML private ListView<Object> resultsList;

    @Setter
    private AdminViewController adminController;

    // -------------------------
    // DATA
    // -------------------------
    private final ObservableList<Object> masterData   = FXCollections.observableArrayList();
    private final ObservableList<Object> filteredData = FXCollections.observableArrayList();

    @Getter private List<Asset> assets = new ArrayList<>();
    @Getter private List<Fund>  funds  = new ArrayList<>();

    // -------------------------
    // INIT
    // -------------------------
    @FXML
    public void initialize() {

        loadData();         // carga assets + funds desde JSON
        bootstrapMarket();  // registra todos los engines y arranca el clock

        resultsList.setItems(filteredData);
        setupCellFactory();
        setupSearch();
        filteredData.setAll(masterData);

        DataStore.assets.clear();
        DataStore.assets.addAll(assets);

        DataStore.funds.clear();
        DataStore.funds.addAll(funds);
    }

    // -------------------------
    // LOAD DATA
    // -------------------------
    private void loadData() {

        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream fundsStream  = getClass().getResourceAsStream("/data/funds.json");
            InputStream assetsStream = getClass().getResourceAsStream("/data/assets.json");

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

    // -------------------------
    // MARKET BOOTSTRAP
    // Crea un MarketEngine por cada asset y los registra en el clock global.
    // Desde este momento todos los precios se simulan en background,
    // aunque ninguna vista esté abierta todavía.
    // -------------------------
    private void bootstrapMarket() {

        MarketClock clock = MarketClock.getInstance();

        // carga una sola vez el mapa { idActivo → último precioCierre }
        Map<String, Double> lastPrices = PriceRecordReader.loadLastPrices();

        for (Asset asset : assets) {

            double startPrice = lastPrices.getOrDefault(
                    asset.getId(),
                    asset.getInitialPrice()   // fallback si es la primera vez
            );

            MarketEngine engine = new MarketEngine(asset, List.of(), startPrice);
            clock.register(engine);
            DataStore.engines.put(asset.getId(), engine);
        }

        clock.start();

        System.out.println("✅ MarketClock arrancado con " + assets.size() + " activos.");
    }

    // -------------------------
    // SEARCH
    // -------------------------
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

    // -------------------------
    // CELL FACTORY
    // -------------------------
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

            if (selected instanceof Fund fund)   adminController.openFund(fund);
            if (selected instanceof Asset asset) adminController.openAsset(asset);
        });
    }
}