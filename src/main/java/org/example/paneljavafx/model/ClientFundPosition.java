package org.example.paneljavafx.model;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ClientFundPosition {

    private Integer positionId;
    private Integer clientId;
    private String fundId;

    private double quantity;
    private double actualValue;

    private Instant openedAt;
    private Instant closedAt;

    private List<Transaction> transactions;

    public void addTransaction(Transaction transaction) {

        if (transactions == null) {
            transactions = new ArrayList<>();
        }

        transactions.add(transaction);
    }
}