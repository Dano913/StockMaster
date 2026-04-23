package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.example.paneljavafx.model.Fund;

import java.io.IOException;

public class AdminViewController {

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab searchTab;

    @FXML
    private Tab fundTab;

    private FundViewController fundViewController;

    @FXML
    public void initialize() {
        loadFundViewController();
        loadSearchController();
    }

    // ----------------------------
    // FUND TAB
    // ----------------------------
    private void loadFundViewController() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/fund-view.fxml")
            );

            AnchorPane content = loader.load();

            fundViewController = loader.getController();

            fundTab.setContent(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------
    // SEARCH TAB
    // ----------------------------
    private void loadSearchController() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/fund-search-view.fxml")
            );

            AnchorPane content = loader.load();

            FundSearchController searchController = loader.getController();

            // 🔥 INYECCIÓN IMPORTANTE
            searchController.setAdminController(this);

            searchTab.setContent(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------
    // NAVEGACIÓN
    // ----------------------------
    public void openFund(Fund fund) {

        if (fund == null) return;

        tabPane.getSelectionModel().select(fundTab);

        if (fundViewController != null) {
            fundViewController.loadFund(fund);
        }
    }
}