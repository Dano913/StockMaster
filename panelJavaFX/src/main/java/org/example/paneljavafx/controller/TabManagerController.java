package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class TabManagerController {

    @FXML
    @Setter
    private TabPane tabPane;

    private final Map<String, Tab> openTabs = new HashMap<>();

    public void openTab(String key, String title, String fxmlPath, Object data) {

        // evitar duplicados
        if (openTabs.containsKey(key)) {
            tabPane.getSelectionModel().select(openTabs.get(key));
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );

            Parent view = loader.load();

            Object controller = loader.getController();

            // 🔥 INYECCIÓN FLEXIBLE SIN INTERFACES
            injectData(controller, data);

            Tab tab = new Tab(title);
            tab.setContent(view);
            tab.setClosable(true);

            tab.setOnClosed(e -> openTabs.remove(key));

            openTabs.put(key, tab);

            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 aquí está la magia sin acoplar tipos
    private void injectData(Object controller, Object data) {

        try {
            // FUND
            controller.getClass()
                    .getMethod("loadFund", data.getClass())
                    .invoke(controller, data);
            return;
        } catch (Exception ignored) {}

        try {
            // ASSET
            controller.getClass()
                    .getMethod("loadAsset", data.getClass())
                    .invoke(controller, data);
        } catch (Exception ignored) {}
    }
}