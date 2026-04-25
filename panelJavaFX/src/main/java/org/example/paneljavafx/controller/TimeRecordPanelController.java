package org.example.paneljavafx.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Setter;
import org.example.paneljavafx.model.TimeRecord;

import java.time.format.DateTimeFormatter;

public class TimeRecordPanelController {

    // =========================
    // FORMAT
    // =========================
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =========================
    // STATE
    // =========================
    @Setter private Runnable onCheckIn;
    @Setter private Runnable onCheckOut;

    // =========================
    // UI
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
    // INIT
    // =========================
    @FXML
    public void initialize() {

        timeRecordsTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        colCode.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getCode()));

        colDateTime.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getDateTime().format(formatter)));

        colType.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getType()));

        // Estado inicial seguro
        lblMessage.setText("");
        lbl2Message.setText("Sin actividad");

        btnCheckIn.setDisable(false);
        btnCheckOut.setDisable(true);
    }

    // =========================
    // PUBLIC API
    // =========================
    public void bindTimeRecords(ObservableList<TimeRecord> list) {

        if (list == null || list.isEmpty()) {
            timeRecordsTable.setItems(FXCollections.observableArrayList());
            updateButtonState(FXCollections.observableArrayList());
            return;
        }

        // NO mutar la lista original
        ObservableList<TimeRecord> reversed =
                FXCollections.observableArrayList(list);

        FXCollections.reverse(reversed);

        timeRecordsTable.setItems(reversed);
        updateButtonState(reversed);
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
    // LOGIC
    // =========================
    private void updateButtonState(ObservableList<TimeRecord> list) {

        String lastType = (list == null || list.isEmpty())
                ? ""
                : list.get(0).getType();

        btnCheckIn.setDisable("IN".equals(lastType));
        btnCheckOut.setDisable("OUT".equals(lastType));

        if ("IN".equals(lastType)) {
            lbl2Message.setText("Trabajando");
        } else if ("OUT".equals(lastType)) {
            lbl2Message.setText("Fuera de turno");
        } else {
            lbl2Message.setText("Sin actividad");
        }
    }

    // =========================
    // HANDLERS
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