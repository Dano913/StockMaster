package org.example.stockmaster.javafx.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Setter;
import org.example.stockmaster.javafx.model.TimeRecord;

import java.time.format.DateTimeFormatter;

public class TimeRecordPanelController {

    // =========================
    // CONSTANTS
    // =========================
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =========================
    // STATE
    // =========================
    @Setter private Runnable onCheckIn;
    @Setter private Runnable onCheckOut;

    // =========================
    // UI (FXML)
    // =========================
    @FXML private TableView<TimeRecord> timeRecordsTable;
    @FXML private TableColumn<TimeRecord, Integer> colCode;
    @FXML private TableColumn<TimeRecord, String> colDateTime;
    @FXML private TableColumn<TimeRecord, String> colType;

    @FXML private Label lblMessage;
    @FXML private Label lbl2Message;

    @FXML private javafx.scene.control.Button btnCheckIn;
    @FXML private javafx.scene.control.Button btnCheckOut;

    // =========================
    // INITIALIZE
    // =========================
    @FXML
    public void initialize() {

        timeRecordsTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        colCode.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getCode())
        );

        colDateTime.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getDateTime().format(formatter)
                )
        );

        colType.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getType())
        );
    }

    // =========================
    // PUBLIC METHODS
    // =========================
    public void bindTimeRecords(ObservableList<TimeRecord> list) {
        FXCollections.reverse(list);
        timeRecordsTable.setItems(list);
        updateButtonState(list);
    }

    public void scrollToTop() {
        if (!timeRecordsTable.getItems().isEmpty()) {
            timeRecordsTable.scrollTo(0);
            timeRecordsTable.getSelectionModel().select(0);
        }
    }

    public void showMessage(String message) {
        lblMessage.setText(message);
    }

    // =========================
    // PRIVATE LOGIC
    // =========================
    private void updateButtonState(ObservableList<TimeRecord> list) {

        if (list.isEmpty()) {
            btnCheckIn.setDisable(false);
            btnCheckOut.setDisable(true);
            return;
        }

        String lastType = list.get(0).getType();

        btnCheckIn.setDisable(lastType.equals("IN"));
        btnCheckOut.setDisable(lastType.equals("OUT"));

        if (lastType.equals("IN")) {
            lbl2Message.setText("Trabajando");
        } else {
            lbl2Message.setText("Fuera de turno");
        }
    }

    // =========================
    // UI HANDLERS
    // =========================
    @FXML
    private void handleCheckIn() {
        if (onCheckIn != null) onCheckIn.run();
        btnCheckIn.setDisable(true);
        btnCheckOut.setDisable(false);
    }

    @FXML
    private void handleCheckOut() {
        if (onCheckOut != null) onCheckOut.run();
        btnCheckOut.setDisable(true);
        btnCheckIn.setDisable(false);
    }
}