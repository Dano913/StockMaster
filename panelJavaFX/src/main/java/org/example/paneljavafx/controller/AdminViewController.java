package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;

import java.io.IOException;

public class AdminViewController {

    @FXML private TabPane tabPane;

    @FXML private Tab searchTab;
    @FXML private Tab fundTab;
    @FXML private Tab assetTab;

    @FXML private TabPane fundTabPane;
    @FXML private TabPane assetTabPane;

    @FXML
    public void initialize() {
        loadGlobalView();
        System.out.println("INIT ADMIN");

        System.out.println("FXML CLASS = " + getClass());

        System.out.println("fundTabPane = " + fundTabPane);
        System.out.println("assetTabPane = " + assetTabPane);
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
    // OPEN FUND (TAB DINÁMICO)
    // -------------------------
    public void openFund(Fund fund) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/fund-view.fxml")
            );

            AnchorPane view = loader.load();

            FundViewController controller = loader.getController();
            controller.loadFund(fund);

            Tab tab = new Tab(fund.getNombre());
            tab.setContent(view);
            tab.setClosable(true);

            fundTabPane.getTabs().add(tab);
            fundTabPane.getSelectionModel().select(tab);

            // 🔥 IMPORTANTE: activar tab padre
            tabPane.getSelectionModel().select(fundTab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // OPEN ASSET (TAB DINÁMICO)
    // -------------------------
    public void openAsset(Asset asset) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/asset-view.fxml")
            );

            AnchorPane view = loader.load();

            AssetViewController controller = loader.getController();
            controller.loadAsset(asset);

            Tab tab = new Tab(asset.getName());
            tab.setContent(view);
            tab.setClosable(true);

            assetTabPane.getTabs().add(tab);
            assetTabPane.getSelectionModel().select(tab);

            // 🔥 IMPORTANTE: activar tab padre
            tabPane.getSelectionModel().select(assetTab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}