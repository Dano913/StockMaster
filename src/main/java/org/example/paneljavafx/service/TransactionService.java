package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.TransaccionDAO;
import org.example.paneljavafx.dao.impl.TransaccionImpl;
import org.example.paneljavafx.model.ClientFundPosition;
import org.example.paneljavafx.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    // ========================= SINGLETON =========================
    private static TransactionService instance;

    public static TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
    }

    private final TransaccionDAO transaccionDAO = new TransaccionImpl();

    // ========================= ADD =========================
    public Transaction addTransaction(int positionId, Transaction transaction) {

        if (transaction == null) return null;

        transaction.setPositionId(positionId);

        return transaccionDAO.save(transaction);
    }

    // ========================= OVERLOAD (OBJETO) =========================
    public Transaction addTransaction(ClientFundPosition position, Transaction transaction) {

        if (position == null || transaction == null) return null;

        transaction.setPositionId(position.getPositionId());

        return transaccionDAO.save(transaction);
    }

    // ========================= GET =========================
    public List<Transaction> getTransactions(ClientFundPosition position) {

        if (position == null || position.getPositionId() == null) {
            return new ArrayList<>();
        }

        return transaccionDAO.findByPositionId(position.getPositionId());
    }
}