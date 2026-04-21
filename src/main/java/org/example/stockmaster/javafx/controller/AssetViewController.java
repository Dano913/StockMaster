package org.example.stockmaster.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import org.example.stockmaster.core.controller.Controller; // TU MOTOR

public class AssetViewController {

    @FXML private Pane canvasContainer;

    private Controller marketController;

    @FXML
    public void initialize() {

        // 1. Crear motor
        marketController = new Controller();

        // 2. Crear canvas y conectarlo
        Canvas canvas = new Canvas(1000, 600);

        // 3. Inyectar canvas al motor (CLAVE)
        injectCanvasIntoCore(canvas);

        // 4. Meterlo en la UI
        canvasContainer.getChildren().add(canvas);
    }

    private void injectCanvasIntoCore(Canvas canvas) {

        try {
            java.lang.reflect.Field field =
                    marketController.getClass().getDeclaredField("canvas");

            field.setAccessible(true);
            field.set(marketController, canvas);

            // inicializar lógica (como si fuera JavaFX)
            marketController.initialize();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}