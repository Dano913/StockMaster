package org.example.paneljavafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.example.paneljavafx.model.Fund;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

public class FundSearchController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Fund> resultsList;

    private final ObservableList<Fund> masterData = FXCollections.observableArrayList();
    private final ObservableList<Fund> filteredData = FXCollections.observableArrayList();

    private FundViewController fundViewController;
    @Setter
    private AdminViewController adminController;

    @FXML
    public void initialize() {
        System.out.println("searchField = " + searchField);
        System.out.println("resultsList = " + resultsList);
        loadFunds();

        resultsList.setItems(filteredData);

        setupCellFactory();
        setupSearch();

        filteredData.setAll(masterData);
    }

    // 1. cargar JSON
    private void loadFunds() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream is = getClass()
                    .getResourceAsStream("/data/funds.json");

            if (is == null) {
                throw new RuntimeException("No se encuentra funds.json en /data");
            }

            List<Fund> funds = mapper.readValue(
                    is,
                    new TypeReference<List<Fund>>() {}
            );

            masterData.setAll(funds);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. búsqueda reactiva
    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal == null || newVal.isBlank()) {
                filteredData.setAll(masterData);
                return;
            }

            String q = newVal.toLowerCase();

            filteredData.setAll(
                    masterData.stream()
                            .filter(f -> matches(f, q))
                            .toList()
            );
        });
    }

    // 3. filtro
    private boolean matches(Fund f, String q) {
        return f.getNombre().toLowerCase().contains(q)
                || f.getTipo().toLowerCase().contains(q)
                || f.getCodigo_isin().toLowerCase().contains(q);
    }

    // 4. UI de cada fila (IMPORTANTE)
    private void setupCellFactory() {

        resultsList.setCellFactory(listView -> new ListCell<>() {

            @Override
            protected void updateItem(Fund fund, boolean empty) {
                super.updateItem(fund, empty);

                if (empty || fund == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(fund.getNombre());

                    setStyle(
                            "-fx-background-color: transparent;" +
                                    "-fx-text-fill: cyan;" +
                                    "-fx-font-size: 16px;" +
                                    "-fx-border-color: cyan;" +
                                    "-fx-margin: 10;"
                    );
                }
            }
        });

        resultsList.setOnMouseClicked(event -> {
            Fund selected = resultsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openFund(selected);
            }
        });
    }

    private void openFund(Fund fund) {
        System.out.println("Abrir fondo: " + fund.getNombre());
        if (adminController != null) {
            adminController.openFund(fund);
        }
    }
}