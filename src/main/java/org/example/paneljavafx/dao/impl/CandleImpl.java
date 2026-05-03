package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.CandleDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.Candle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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