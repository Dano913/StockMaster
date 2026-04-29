package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.CandleDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.Candle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CandleImpl implements CandleDAO {

    @Override
    public void save(Candle candle) {
        String sql = """
            INSERT INTO candle (
                timestamp, open, high, low, close
            ) VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, candle.getTimestamp());
            stmt.setDouble(2, candle.getOpen());
            stmt.setDouble(3, candle.getHigh());
            stmt.setDouble(4, candle.getLow());
            stmt.setDouble(5, candle.getClose());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Candle", e);
        }
    }

    @Override
    public List<Candle> findAll() {
        List<Candle> list = new ArrayList<>();

        String sql = "SELECT * FROM candle ORDER BY timestamp ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo candles", e);
        }

        return list;
    }

    @Override
    public Optional<Candle> findByTimestamp(long timestamp) {
        String sql = "SELECT * FROM candle WHERE timestamp = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, timestamp);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public boolean update(long timestamp, Candle candle) {
        String sql = """
            UPDATE candle SET
                open=?, high=?, low=?, close=?
            WHERE timestamp=?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, candle.getOpen());
            stmt.setDouble(2, candle.getHigh());
            stmt.setDouble(3, candle.getLow());
            stmt.setDouble(4, candle.getClose());
            stmt.setLong(5, timestamp);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(long timestamp) {
        String sql = "DELETE FROM candle WHERE timestamp=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, timestamp);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Candle map(ResultSet rs) throws SQLException {
        return new Candle(
                rs.getDouble("open"),
                rs.getDouble("high"),
                rs.getDouble("low"),
                rs.getDouble("close"),
                rs.getLong("timestamp")
        );
    }
}