package org.example.paneljavafx.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;

import java.io.InputStream;
import java.util.List;

public class GlobalController {

    // -------------------------
    // UI
    // -------------------------
    @FXML
    private TextField searchField;

    @FXML
    private ListView<Object> resultsList;

    @Setter
    private AdminViewController adminController;

    // -------------------------
    // DATA
    // -------------------------
    private final ObservableList<Object> masterData = FXCollections.observableArrayList();
    private final ObservableList<Object> filteredData = FXCollections.observableArrayList();

    // -------------------------
    // INIT
    // -------------------------
    @FXML
    public void initialize() {

        System.out.println("GLOBAL CONTROLLER INIT: " + this);

        loadData();

        resultsList.setItems(filteredData);

        setupCellFactory();
        setupSearch();

        filteredData.setAll(masterData);
    }

    // -------------------------
    // LOAD DATA
    // -------------------------
    private void loadData() {

        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream fundsStream = getClass().getResourceAsStream("/data/funds.json");
            InputStream assetsStream = getClass().getResourceAsStream("/data/assets.json");

            if (fundsStream != null) {
                List<Fund> funds = mapper.readValue(fundsStream, new TypeReference<>() {});
                masterData.addAll(funds);
            }

            if (assetsStream != null) {
                List<Asset> assets = mapper.readValue(assetsStream, new TypeReference<>() {});
                masterData.addAll(assets);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    || f.getTipo().toLowerCase().contains(q)
                    || f.getCodigo_isin().toLowerCase().contains(q);
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

            System.out.println("SELECTED: " + selected);

            if (selected instanceof Fund fund) {
                adminController.openFund(fund);
            }

            if (selected instanceof Asset asset) {
                adminController.openAsset(asset);
            }
        });
    }
}