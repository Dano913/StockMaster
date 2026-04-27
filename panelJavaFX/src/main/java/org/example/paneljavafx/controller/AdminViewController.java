package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import org.example.paneljavafx.data.FundPositionDataSource;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundPosition;
import org.example.paneljavafx.service.AssetService;
import org.example.paneljavafx.service.FundService;

import java.util.List;

public class AdminViewController {

    @FXML private TabPane tabPane;

    @FXML private Tab searchTab;
    @FXML private Tab fundTab;
    @FXML private Tab assetTab;

    @FXML private TabPane fundTabPane;
    @FXML private TabPane assetTabPane;

    private List<FundPosition> cachedPositions;

    private final FundService fundService = FundService.getInstance();
    private final AssetService assetService = AssetService.getInstance();

    @FXML
    public void initialize() {
        loadGlobalView();
        cachedPositions = FundPositionDataSource.load();

        fundService.load();
        assetService.load();
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

        } catch (Exception e) {
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

            List<FundPosition> fundSpecificPositions =
                    fundService.getPositionsByFund(cachedPositions, fund.getIdFondo());

            controller.loadData(fund);
            controller.loadPositions(fundSpecificPositions);

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

            controller.loadData(asset);
            controller.loadPositions(cachedPositions);

            Tab tab = new Tab(asset.getName());
            tab.setContent(view);
            tab.setClosable(true);

            tab.setOnClosed(event -> controller.onClose());

            assetTabPane.getTabs().add(tab);
            assetTabPane.getSelectionModel().select(tab);

            tabPane.getSelectionModel().select(assetTab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}