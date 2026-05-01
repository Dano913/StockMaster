package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.TransaccionDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.Transaction;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TransaccionImpl implements TransaccionDAO {

    private Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    // ========================= FIND BY POSITION =========================
    @Override
    public List<Transaction> findByPositionId(int positionId) {

        List<Transaction> list = new ArrayList<>();

        String sql = """
            SELECT id_transaccion, id_posicion, tipo, importe, fecha
            FROM transaccion
            WHERE id_posicion = ?
            ORDER BY fecha DESC
        """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, positionId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading transactions", e);
        }

        return list;
    }

    // ========================= SAVE =========================
    @Override
    public Transaction save(Transaction transaction) {

        String sql = """
            INSERT INTO transaccion (id_posicion, tipo, importe, fecha)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, transaction.getPositionId());
            ps.setString(2, transaction.getType());
            ps.setDouble(3, transaction.getAmount());
            ps.setTimestamp(4, Timestamp.from(
                    transaction.getExecutedAt() != null
                            ? transaction.getExecutedAt()
                            : Instant.now()
            ));

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                transaction.setTransactionId(rs.getInt(1));
            }

            return transaction;

        } catch (Exception e) {
            throw new RuntimeException("Error saving transaction", e);
        }
    }

    @Override
    public void deleteById(int id) {

        String sql = "DELETE FROM transaccion WHERE id_transaccion = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting transaction", e);
        }
    }

    // ========================= MAPPER =========================
    private Transaction mapRow(ResultSet rs) throws SQLException {

        Transaction t = new Transaction();

        t.setTransactionId(rs.getInt("id_transaccion"));
        t.setPositionId(rs.getInt("id_posicion"));
        t.setType(rs.getString("tipo"));
        t.setAmount(rs.getDouble("importe"));

        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) {
            t.setExecutedAt(ts.toInstant());
        }

        return t;
    }
}