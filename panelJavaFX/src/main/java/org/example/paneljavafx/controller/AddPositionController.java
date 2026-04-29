package org.example.paneljavafx.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.example.paneljavafx.model.*;
import org.example.paneljavafx.service.ClienteService;
import org.example.paneljavafx.service.TransactionService;

import java.util.List;

public class AddPositionController {

    @FXML private ComboBox<Fund> fundCombo;
    @FXML private TextField amountField;
    @FXML private Button deleteButton;

    private final ClienteService clienteService = ClienteService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();

    private Integer clienteId;
    private Cliente cliente;

    private Posicion posicion; // 🔥 única fuente de verdad
    private List<Fund> funds;

    @Setter
    private ClientePrivadoViewController parent;

    // ========================= INIT =========================
    @FXML
    public void initialize() {
        this.posicion = new Posicion();
    }

    public void initCreate() {

        this.posicion = new Posicion();

        deleteButton.setVisible(false);

        loadFundsIfNeeded();
        clearForm();
    }

    // ========================= INYECCIONES =========================
    public void setFunds(List<Fund> funds) {
        this.funds = funds;
        loadFundsIfNeeded();
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        this.clienteId = cliente.getIdCliente();
    }

    // ========================= FUNDS =========================
    private void loadFundsIfNeeded() {

        if (funds == null || fundCombo == null) return;

        fundCombo.setItems(FXCollections.observableArrayList(funds));

        fundCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Fund item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        fundCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Fund item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
    }

    // ========================= SAVE =========================
    @FXML
    private void save() {

        Fund fund = fundCombo.getValue();
        if (fund == null) return;

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (Exception e) {
            showError("Cantidad inválida");
            return;
        }

        // =========================
        // BUSCAR POSICIÓN EXISTENTE
        // =========================
        Posicion existing = findExistingPosition(fund.getIdFondo());

        if (existing != null) {

            // 🔥 usar existente como estado activo
            this.posicion = existing;

        } else {

            // 🔥 crear nueva posición
            this.posicion = new Posicion();
            this.posicion.setIdFondo(fund.getIdFondo());
            this.posicion.setCantidad(amount);

            clienteService.addPosition(clienteId, posicion);

            System.out.println("NEW POSITION → " + fund.getIdFondo());
        }

        // =========================
        // SIEMPRE: TRANSACCIÓN
        // =========================
        Transaccion t = new Transaccion();
        t.setTipo("BUY");
        t.setImporte(amount);

        transactionService.addTransaction(posicion, t);

        clienteService.updatePosition(clienteId, posicion);

        System.out.println("POSITION UPDATED → " + posicion.getIdFondo());

        close(true);
    }

    // ========================= SELL =========================
    @FXML
    private void sell() {

        if (posicion == null) return;

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (Exception e) {
            showError("Cantidad inválida");
            return;
        }

        Transaccion t = new Transaccion();
        t.setTipo("SELL");
        t.setImporte(amount);

        transactionService.addTransaction(posicion, t);

        clienteService.updatePosition(clienteId, posicion);

        System.out.println("SELL → " + posicion.getIdFondo());

        close(true);
    }

    // ========================= FIND POSITION =========================
    private Posicion findExistingPosition(String fundId) {

        if (cliente == null) return null;

        return clienteService.getPosicionesByClienteId(clienteId)
                .stream()
                .filter(p -> p.getIdFondo().equals(fundId))
                .findFirst()
                .orElse(null);
    }

    // ========================= CLOSE =========================
    private void close(boolean refresh) {

        if (parent != null) {
            parent.closeOverlay();

            if (refresh) {
                parent.reloadCliente();
            }
        }
    }

    // ========================= HELPERS =========================
    private void clearForm() {

        if (fundCombo != null) {
            fundCombo.getSelectionModel().clearSelection();
            fundCombo.setValue(null);
        }

        if (amountField != null) {
            amountField.clear();
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void cancel() {
        close(false);
    }
}