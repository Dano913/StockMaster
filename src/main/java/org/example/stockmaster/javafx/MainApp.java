package org.example.stockmaster.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.stockmaster.javafx.controller.LoginController;
import org.example.stockmaster.javafx.controller.MainController;

import java.util.Objects;
import java.util.logging.LogManager;

public class MainApp extends Application {

    private MainController mainController;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader mainLoader = new FXMLLoader(
                Objects.requireNonNull(
                        getClass().getResource("/org/example/stockmaster/main.fxml")
                )
        );
        Parent root = mainLoader.load();
        mainController = mainLoader.getController();

        FXMLLoader loginLoader = new FXMLLoader(
                Objects.requireNonNull(
                        getClass().getResource("/org/example/stockmaster/login-view.fxml")
                )
        );
        Parent loginView = loginLoader.load();
        LoginController loginController = loginLoader.getController();

        System.out.println("loginController: " + loginController);
        System.out.println("mainController: " + mainController);

        loginController.setMainController(mainController);

        System.out.println("mainController set: " + loginController);

        mainController.setContent(loginView);
        stage.show();

        mainController.setContent(loginView);

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        launch();
    }
}