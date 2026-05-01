package org.example.paneljavafx.helper;

import org.example.paneljavafx.model.Transaction;
import org.example.paneljavafx.viewmodel.TransactionRowView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionMapper {

    public static List<TransactionRowView> toRowView(
            List<Transaction> transactions,
            String fundName,
            double actualValue
    ) {
        return transactions.stream()
                .map(t -> new TransactionRowView(
                        t.getTransactionId(),
                        fundName,
                        t.getType(),
                        t.getAmount(),
                        actualValue,
                        t.getExecutedAt()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                ))
                .toList();
    }

    private static String formatDate(LocalDateTime date) {
        if (date == null) return "-";

        return date.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}