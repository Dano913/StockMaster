package org.example.paneljavafx.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.example.paneljavafx.model.*;
import org.example.paneljavafx.service.ClientFundPositionService;
import org.example.paneljavafx.service.ClientService;
import org.example.paneljavafx.service.TransactionService;

import java.util.List;

public class AddPositionController {

    @FXML private ComboBox<Fund> fundCombo;
    @FXML private TextField amountField;
    @FXML private Button deleteButton;

    private final ClientService clientService = ClientService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();
    private final ClientFundPositionService clientFundPositionService = ClientFundPositionService.getInstance();

    private Integer clientId;
    private Client client;

    private List<Fund> funds;

    @Setter
    private ClienteViewController parent;

    // ========================= INIT =========================
    @FXML
    public void initialize() {
        deleteButton.setVisible(false);
    }

    public void initCreate() {
        deleteButton.setVisible(false);
        loadFundsIfNeeded();
        clearForm();
    }

    // ========================= SETTERS =========================
    public void setFunds(List<Fund> funds) {
        this.funds = funds;
        loadFundsIfNeeded();
    }

    public void setCliente(Client client) {
        this.client = client;
        this.clientId = client.getClientId();
    }

    // ========================= FUNDS =========================
    private void loadFundsIfNeeded() {

        if (funds == null || fundCombo == null) return;

        fundCombo.setItems(FXCollections.observableArrayList(funds));

        fundCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Fund item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        fundCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Fund item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
    }

    // ========================= BUY =========================
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

        ClientFundPosition position =
                clientFundPositionService.getByClientId(client.getClientId())
                        .stream()
                        .filter(p -> p.getFundId().equals(fund.getFundId()))
                        .findFirst()
                        .orElse(null);

        // 2. Si no existe, crearla → el save() devuelve la posición con su ID generado
        if (position == null) {
            position = new ClientFundPosition();
            position.setClientId(client.getClientId());
            position.setFundId(fund.getFundId());
            position.setQuantity(amount);
            position.setActualValue(amount);
            position = clientFundPositionService.save(position); // ← importante: reasignar para tener el positionId
        }

        // 3. Registrar la transacción con el positionId ya disponible
        Transaction transaction = new Transaction();
        transaction.setPositionId(position.getPositionId());
        transaction.setType("BUY");
        transaction.setAmount(amount);

        transactionService.addTransaction(position.getPositionId(), transaction);

        close(true);
    }

    // ========================= SELL =========================
    @FXML
    private void sell() {

        Fund fund = fundCombo.getValue();
        if (fund == null) return;

        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (Exception e) {
            showError("Cantidad inválida");
            return;
        }

        int clientId = client.getClientId();

        ClientFundPosition position =
                clientService.getPositionsByClientId(clientId)
                        .stream()
                        .filter(p -> p.getFundId().equals(fund.getFundId()))
                        .findFirst()
                        .orElse(null);

        if (position == null) return;

        Transaction transaction = new Transaction();
        transaction.setType("SELL");
        transaction.setAmount(amount);

        clientService.addTransactionToPosition(position.getPositionId(), transaction);

        close(true);
    }

    // ========================= CLOSE =========================
    private void close(boolean refresh) {

        if (parent != null) {
            parent.closeOverlay();

            if (refresh) {
                parent.refresh();
            }
        }

        clearForm();
    }

    private void clearForm() {

        fundCombo.getSelectionModel().clearSelection();
        amountField.clear();
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