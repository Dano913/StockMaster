package org.example.paneljavafx.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertUtils {

    public static boolean confirm(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);

        return alert.showAndWait()
                .map(btn -> btn == ButtonType.OK)
                .orElse(false);
    }
}