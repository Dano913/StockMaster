package org.example.stockmaster.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;

public class MainController {

    // =========================
    // UI (FXML)
    // =========================
    @FXML private ImageView background;
    @FXML private AnchorPane contentArea;

    // =========================
    // INITIALIZE
    // =========================
    @FXML
    public void initialize() {
        setupBackground();
    }

    // =========================
    // PUBLIC METHODS
    // =========================
    public void setContent(javafx.scene.Parent view) {

        contentArea.getChildren().setAll(view);

        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
    }

    // =========================
    // PRIVATE METHODS
    // =========================
    private void setupBackground() {

        Image img = new Image(
                Objects.requireNonNull(
                        getClass().getResource("/images/logo-stockmaster.png")
                ).toExternalForm()
        );

        background.setImage(img);
    }
}