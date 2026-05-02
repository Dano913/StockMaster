package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import org.example.paneljavafx.dao.FundAssetPositionDAO;
import org.example.paneljavafx.dao.impl.FundAssetPositionImpl;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.service.AdminService;
import org.example.paneljavafx.service.AssetService;
import org.example.paneljavafx.service.FundService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminViewController {

    private static final Logger logger =
            Logger.getLogger(AdminViewController.class.getName());

    // ========================= UI =========================
    @FXML private TabPane tabPane;

    @FXML private Tab searchTab;
    @FXML private Tab fundTab;
    @FXML private Tab assetTab;
    @FXML private Tab personalTab;
    @FXML private Tab clienteTab;

    @FXML private TabPane fundTabPane;
    @FXML private TabPane assetTabPane;

    // ========================= SERVICES =========================
    private final FundAssetPositionDAO positionDAO =
            new FundAssetPositionImpl();

    private final AdminService viewService =
            new AdminService(
                    FundService.getInstance(),
                    AssetService.getInstance()
            );

    // ========================= INIT =========================
    @FXML
    public void initialize() {

        viewService.initialize(positionDAO.findAll());

        loadGlobalView();
        loadPersonalView();
        loadClientesView();
    }

    // ========================= GENERIC LOADER =========================
    private <T> T loadView(String fxml, Tab tab) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxml)
            );

            Parent view = loader.load();
            tab.setContent(view);

            return loader.getController();

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Error cargando vista: " + fxml, e);
            return null;
        }
    }

    // ========================= GLOBAL VIEW =========================
    private void loadGlobalView() {

        GlobalController globalController = loadView(
                "/org/example/paneljavafx/global-view.fxml",
                searchTab
        );

        if (globalController != null) {
            globalController.setAdminController(this);
        }
    }

    // ========================= PERSONAL VIEW =========================
    private void loadPersonalView() {

        loadView(
                "/org/example/paneljavafx/personal-view.fxml",
                personalTab
        );
    }

    // ========================= CLIENTS VIEW =========================
    private void loadClientesView() {

        loadView(
                "/org/example/paneljavafx/clientesGestion-view.fxml",
                clienteTab
        );
    }


    // ========================= FUNDS VIEW =========================
    public void openFund(Fund fund) {

        try {
            var ctx = viewService.prepareFundView(fund);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/fund-view.fxml")
            );

            Parent view = loader.load();

            FundViewController controller = loader.getController();
            controller.loadData(ctx.fund());
            controller.loadPositions(ctx.positions());

            Tab tab = new Tab(fund.getName());
            tab.setContent(view);
            tab.setClosable(true);

            fundTabPane.getTabs().add(tab);
            fundTabPane.getSelectionModel().select(tab);
            tabPane.getSelectionModel().select(fundTab);

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Error al cargar fondo", e);
        }
    }

    // ========================= ASSETS VIEW =========================
    public void openAsset(Asset asset) {

        try {
            var ctx = viewService.prepareAssetView(asset);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/paneljavafx/asset-view.fxml")
            );

            Parent view = loader.load();

            AssetViewController controller = loader.getController();
            controller.loadPositions(ctx.positions());
            controller.loadData(ctx.asset());

            Tab tab = new Tab(asset.getName());
            tab.setContent(view);
            tab.setClosable(true);
            tab.setOnClosed(e -> controller.onClose());

            assetTabPane.getTabs().add(tab);
            assetTabPane.getSelectionModel().select(tab);
            tabPane.getSelectionModel().select(assetTab);

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Error al cargar activo", e);
        }
    }
}