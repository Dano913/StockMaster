package org.example.paneljavafx.viewmodel;

public class TransactionRowView {

    private final String fundName;
    private final String type;
    private final double amount;
    private final double totalPosition;

    public TransactionRowView(String fundName, String type, double amount, double totalPosition) {
        this.fundName = fundName;
        this.type = type;
        this.amount = amount;
        this.totalPosition = totalPosition;
    }

    public String getFundName() {
        return fundName;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getTotalPosition() {
        return totalPosition;
    }
}