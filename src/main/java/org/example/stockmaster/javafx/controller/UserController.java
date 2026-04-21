package org.example.stockmaster.javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import lombok.Setter;
import org.example.stockmaster.javafx.model.TimeRecord;
import org.example.stockmaster.javafx.model.User;
import org.example.stockmaster.javafx.service.TimeRecordService;

public class UserController {

    // =========================
    // SERVICES
    // =========================
    private final TimeRecordService timeRecordService =
            TimeRecordService.getInstance();

    // =========================
    // STATE
    // =========================
    @Setter private User user;
    @Setter private MainController mainController;

    private TimeRecordPanelController timeRecordPanelController;

    // =========================
    // UI (FXML)
    // =========================
    @FXML private VBox timeRecordContainer;
    @FXML private VBox profileContainer;
    private ProfileController profileViewController;

    // =========================
    // INITIALIZE
    // =========================
    @FXML
    public void initialize() {
        loadTimeRecordPanel();
    }

    // =========================
    // PUBLIC METHODS
    // =========================
    public void setUser(User user) {
        this.user = user;
        profileViewController.setUser(user);
        loadTimeRecords();
    }

    // =========================
    // PRIVATE - UI SETUP
    // =========================
    private void loadTimeRecordPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/stockmaster/time-record-view.fxml")
            );

            Parent panel = loader.load();
            timeRecordPanelController = loader.getController();

            timeRecordContainer.getChildren().setAll(panel);

            configureEvents();

            FXMLLoader loaderprofile = new FXMLLoader(
                    getClass().getResource("/org/example/stockmaster/profile-user-view.fxml")
            );

            Parent profile = loaderprofile.load();
            profileViewController = loaderprofile.getController();

            profileContainer.getChildren().setAll(profile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // PRIVATE - EVENTS
    // =========================
    private void configureEvents() {
        timeRecordPanelController.setOnCheckIn(this::handleCheckIn);
        timeRecordPanelController.setOnCheckOut(this::handleCheckOut);
    }

    // =========================
    // PRIVATE - ACTIONS
    // =========================
    private void handleCheckIn() {
        if (user == null) return;

        timeRecordService.checkIn(user);

        loadTimeRecords();
        timeRecordPanelController.scrollToTop();
        timeRecordPanelController.showMessage("Check-in registrado, comienza tu jornada!");
    }

    private void handleCheckOut() {
        if (user == null) return;

        timeRecordService.checkOut(user);

        loadTimeRecords();
        timeRecordPanelController.scrollToTop();
        timeRecordPanelController.showMessage("Check-out registrado, hasta el próximo día!");
    }

    // =========================
    // PRIVATE - DATA
    // =========================
    private void loadTimeRecords() {

        if (user == null || timeRecordPanelController == null) return;

        ObservableList<TimeRecord> userRecords =
                FXCollections.observableArrayList(user.getTimeRecords());

        timeRecordPanelController.bindTimeRecords(userRecords);
    }

    // =========================
    // UI HANDLERS
    // =========================
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/stockmaster/login-view.fxml"
            ));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setMainController(mainController);
            mainController.setContent(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}