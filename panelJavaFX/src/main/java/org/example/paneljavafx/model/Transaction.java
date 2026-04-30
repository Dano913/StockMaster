package org.example.paneljavafx.model;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Transaction {

    private Integer transactionId;
    private Integer positionId;
    private String type;

    private double amount;

    private Instant executedAt;
}