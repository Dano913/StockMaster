package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.AssetDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.Asset;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssetImpl implements AssetDAO {

    @Override
    public List<Asset> findAll() {
        List<Asset> list = new ArrayList<>();

        String sql = "SELECT * FROM asset";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo assets", e);
        }

        return list;
    }

    private Asset map(ResultSet rs) throws SQLException {
        return new Asset(
                rs.getString("id"),
                rs.getString("ticker"),
                rs.getString("name"),
                rs.getString("isin"),
                rs.getString("type"),
                rs.getString("sector"),
                rs.getString("risk"),
                rs.getString("liquidity"),
                rs.getString("change"),
                rs.getDouble("initialPrice"),
                rs.getDouble("market_cap"),
                rs.getDouble("volatility")
        );
    }
}