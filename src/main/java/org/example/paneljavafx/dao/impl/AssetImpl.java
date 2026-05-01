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
    public void save(Asset asset) {
        String sql = """
            INSERT INTO asset (
                id_activo, precio_inicial, ticker, nombre, isin,
                tipo, sector,
                market_cap, volatilidad, riesgo, liquidez, variacion
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, asset.getId());
            stmt.setDouble(2, asset.getInitialPrice());
            stmt.setString(3, asset.getTicker());
            stmt.setString(4, asset.getName());
            stmt.setString(5, asset.getIsinCode());

            stmt.setString(6, asset.getType());
            stmt.setString(7, asset.getSector());

            stmt.setDouble(8, asset.getMarketCap());
            stmt.setDouble(9, asset.getVolatility());
            stmt.setString(10, asset.getRisk());
            stmt.setString(11, asset.getLiquidity());
            stmt.setString(12, asset.getChange());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Asset", e);
        }
    }

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

    @Override
    public Optional<Asset> findById(String id) {
        String sql = "SELECT * FROM asset WHERE id_activo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

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
    public boolean update(String id, Asset asset) {
        String sql = """
            UPDATE asset SET
                precio_inicial=?, ticker=?, nombre=?, isin=?,
                tipo=?, sector=?,
                market_cap=?, volatilidad=?, riesgo=?, liquidez=?, variacion=?
            WHERE id_activo=?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, asset.getInitialPrice());
            stmt.setString(2, asset.getTicker());
            stmt.setString(3, asset.getName());
            stmt.setString(4, asset.getIsinCode());

            stmt.setString(5, asset.getType());
            stmt.setString(6, asset.getSector());

            stmt.setDouble(7, asset.getMarketCap());
            stmt.setDouble(8, asset.getVolatility());
            stmt.setString(9, asset.getRisk());
            stmt.setString(10, asset.getLiquidity());
            stmt.setString(11, asset.getChange());

            stmt.setString(12, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM asset WHERE id_activo=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Asset map(ResultSet rs) throws SQLException {
        return new Asset(
                rs.getString("id_activo"),
                rs.getString("ticker"),
                rs.getString("nombre"),
                rs.getString("isin"),
                rs.getString("tipo"),
                rs.getString("sector"),
                rs.getString("riesgo"),
                rs.getString("liquidez"),
                rs.getString("variacion"),
                rs.getDouble("precio_inicial"),
                rs.getDouble("market_cap"),
                rs.getDouble("volatilidad")
        );
    }
}