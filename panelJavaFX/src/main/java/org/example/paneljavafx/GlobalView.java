package org.example.paneljavafx;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class GlobalView {

    @FXML
    private VBox leftSidebar;

    private boolean expanded = true;

    @FXML
    private void toggleSidebar() {
        if (expanded) {
            collapseSidebar();
        } else {
            expandSidebar();
        }
    }

    private void collapseSidebar() {
        leftSidebar.setPrefWidth(60);
        leftSidebar.setMinWidth(60);
        leftSidebar.setMaxWidth(60);
        expanded = false;
    }

    private void expandSidebar() {
        leftSidebar.setPrefWidth(250);
        leftSidebar.setMinWidth(250);
        leftSidebar.setMaxWidth(250);
        expanded = true;
    }
}