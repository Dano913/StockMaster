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
            INSERT INTO candle (asset_id, open, high, low, close, timestamp)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, candle.getAssetId());
            stmt.setDouble(2, candle.getOpen());
            stmt.setDouble(3, candle.getHigh());
            stmt.setDouble(4, candle.getLow());
            stmt.setDouble(5, candle.getClose());
            stmt.setLong(6, candle.getTimestamp());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Candle", e);
        }
    }

    @Override
    public List<Candle> findAll() {
        List<Candle> list = new ArrayList<>();
        String sql = "SELECT * FROM candle ORDER BY asset_id, timestamp ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error reading candles", e);
        }
        return list;
    }

    @Override
    public List<Candle> findByAssetId(String assetId) {
        List<Candle> list = new ArrayList<>();
        String sql = "SELECT * FROM candle WHERE asset_id = ? ORDER BY timestamp ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, assetId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error reading candles by asset", e);
        }
        return list;
    }

    @Override
    public List<Candle> findByAssetIdBetween(String assetId, long from, long to) {
        List<Candle> list = new ArrayList<>();
        String sql = """
            SELECT * FROM candle
            WHERE asset_id = ? AND timestamp BETWEEN ? AND ?
            ORDER BY timestamp ASC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, assetId);
            stmt.setLong(2, from);
            stmt.setLong(3, to);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error reading candles by range", e);
        }
        return list;
    }

    @Override
    public Optional<Candle> findLastByAssetId(String assetId) {

        String sql = """
        SELECT * FROM candle
        WHERE asset_id = ?
        ORDER BY timestamp DESC
        LIMIT 1
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, assetId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Candle> findById(long timestamp) {
        String sql = "SELECT * FROM candle WHERE timestamp = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, timestamp);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private Candle map(ResultSet rs) throws SQLException {
        return new Candle(
                rs.getString("asset_id"),
                rs.getDouble("open"),
                rs.getDouble("high"),
                rs.getDouble("low"),
                rs.getDouble("close"),
                rs.getLong("timestamp")
        );
    }
}