package org.example.paneljavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Setter;
import org.example.paneljavafx.common.TabDataReceiver;

import java.util.HashMap;
import java.util.Map;

public class TabManagerController {

    @FXML
    @Setter
    private TabPane tabPane;

    private final Map<String, Tab> openTabs = new HashMap<>();

    public <T> void openTab(String key, String title, String fxmlPath, T data) {

        if (openTabs.containsKey(key)) {
            tabPane.getSelectionModel().select(openTabs.get(key));
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Object controller = loader.getController();

            // 🔥 INYECCIÓN TIPADA (sin magia negra)
            if (controller instanceof TabDataReceiver<?> receiver) {
                inject(receiver, data);
            }

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

    // cast seguro controlado
    @SuppressWarnings("unchecked")
    private <T> void inject(TabDataReceiver<?> receiver, T data) {
        ((TabDataReceiver<T>) receiver).loadData(data);
    }
}