package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.FundDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.Fund;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FundImpl implements FundDAO {

    @Override
    public List<Fund> findAll() {
        List<Fund> list = new ArrayList<>();
        String sql = "SELECT * FROM fund";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo funds", e);
        }

        return list;
    }

    @Override
    public Optional<Fund> findById(String idFondo) {
        String sql = "SELECT * FROM fund WHERE id_fondo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idFondo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    private Fund map(ResultSet rs) throws SQLException {
        return new Fund(
                rs.getString("id"),
                rs.getString("company_id"),
                rs.getString("name"),
                rs.getString("isin_code"),
                rs.getString("type"),
                rs.getString("category"),
                rs.getString("currency"),
                rs.getDate("createdAt").toLocalDate()
        );
    }
}