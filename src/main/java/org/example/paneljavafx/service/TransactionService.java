package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.TransactionDAO;
import org.example.paneljavafx.dao.impl.TransactionImpl;
import org.example.paneljavafx.model.ClientFundPosition;
import org.example.paneljavafx.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    private static TransactionService instance;

    public static TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
    }

    private final TransactionDAO dao = new TransactionImpl();

    // ========================= BASIC CRUD =========================

    public Transaction addTransaction(int positionId, Transaction transaction) {

        if (transaction == null) return null;

        transaction.setPositionId(positionId);
        return dao.save(transaction);
    }

    public Transaction addTransaction(ClientFundPosition position, Transaction transaction) {

        if (position == null || transaction == null) return null;

        transaction.setPositionId(position.getPositionId());
        return dao.save(transaction);
    }

    public List<Transaction> getTransactions(ClientFundPosition position) {

        if (position == null || position.getPositionId() == null) {
            return new ArrayList<>();
        }

        return dao.findByPositionId(position.getPositionId());
    }

    public void deleteById(int transactionId) {
        dao.deleteById(transactionId);
    }
}