package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Transaction;

import java.util.List;

public interface TransactionDAO {

    Transaction save(Transaction transaction);

    List<Transaction> findByPositionId(int positionId);

    void deleteById(int id);
}