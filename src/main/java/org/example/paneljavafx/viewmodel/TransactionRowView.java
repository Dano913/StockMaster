package org.example.paneljavafx.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class TransactionRowView {

    private final int transactionId;
    private final String fundName;
    private final String type;
    private final double amount;
    private final double totalPosition;
    private final String fecha; // ← añadir
}