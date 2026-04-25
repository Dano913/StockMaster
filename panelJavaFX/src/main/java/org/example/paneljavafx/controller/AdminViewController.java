package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.example.paneljavafx.data.FundPositionLoader;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundPosition;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AdminViewController {

    @FXML private TabPane tabPane;

    @FXML private Tab searchTab;
    @FXML private Tab fundTab;
    @FXML private Tab assetTab;

    @FXML private TabPane fundTabPane;
    @FXML private TabPane assetTabPane;

    private List<FundPosition> cachedPositions;

    @FXML
    public void initialize() {
        loadGlobalView();
        cachedPositions = FundPositionLoader.load();
    }

    // -------------------------
    // GLOBAL VIEW
    // -------------------------
    private void loadGlobalView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/global-view.fxml")
            );

            AnchorPane content = loader.load();

            GlobalController controller = loader.getController();
            controller.setAdminController(this);

            searchTab.setContent(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // OPEN FUND
    // -------------------------
    public void openFund(Fund fund) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/fund-view.fxml")
            );

            AnchorPane view = loader.load();

            FundViewController controller = loader.getController();
            // Filtramos las posiciones que pertenecen a este fondo específico
            List<FundPosition> fundSpecificPositions = cachedPositions.stream()
                    .filter(p -> fund.getIdFondo().equals(p.getIdFund())) // El objeto 'fund' ya sabemos que no es nulo aquí

                    .collect(Collectors.toList());

            controller.loadFund(fund, fundSpecificPositions);


            Tab tab = new Tab(fund.getNombre());
            tab.setContent(view);
            tab.setClosable(true);

            fundTabPane.getTabs().add(tab);
            fundTabPane.getSelectionModel().select(tab);

            tabPane.getSelectionModel().select(fundTab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // OPEN ASSET
    // -------------------------
    public void openAsset(Asset asset) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/asset-view.fxml")
            );

            AnchorPane view = loader.load();

            AssetViewController controller = loader.getController();
            controller.loadAssetExposure(asset, cachedPositions);
            controller.loadAsset(asset);

            Tab tab = new Tab(asset.getName());
            tab.setContent(view);
            tab.setClosable(true);

            // ← limpiar engine y listener cuando el tab se cierra
            tab.setOnClosed(event -> controller.onClose());

            assetTabPane.getTabs().add(tab);
            assetTabPane.getSelectionModel().select(tab);

            tabPane.getSelectionModel().select(assetTab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}