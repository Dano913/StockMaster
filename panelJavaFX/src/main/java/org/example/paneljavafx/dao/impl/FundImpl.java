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
    public void save(Fund fund) {
        String sql = """
            INSERT INTO fund (
                id_fondo, id_empresa, nombre, codigo_isin,
                tipo, categoria, moneda_base, fecha_creacion
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fund.getIdFondo());
            stmt.setString(2, fund.getIdEmpresa());
            stmt.setString(3, fund.getNombre());
            stmt.setString(4, fund.getCodigoIsin());
            stmt.setString(5, fund.getTipo());
            stmt.setString(6, fund.getCategoria());
            stmt.setString(7, fund.getMonedaBase());
            stmt.setDate(8, Date.valueOf(fund.getFechaCreacion()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Fund", e);
        }
    }

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

    @Override
    public boolean update(String idFondo, Fund fund) {
        String sql = """
            UPDATE fund SET
                id_empresa=?, nombre=?, codigo_isin=?,
                tipo=?, categoria=?, moneda_base=?, fecha_creacion=?
            WHERE id_fondo=?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fund.getIdEmpresa());
            stmt.setString(2, fund.getNombre());
            stmt.setString(3, fund.getCodigoIsin());
            stmt.setString(4, fund.getTipo());
            stmt.setString(5, fund.getCategoria());
            stmt.setString(6, fund.getMonedaBase());
            stmt.setDate(7, Date.valueOf(fund.getFechaCreacion()));
            stmt.setString(8, idFondo);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(String idFondo) {
        String sql = "DELETE FROM fund WHERE id_fondo=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idFondo);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Fund map(ResultSet rs) throws SQLException {
        return new Fund(
                rs.getString("id_fondo"),
                rs.getString("id_empresa"),
                rs.getString("nombre"),
                rs.getString("codigo_isin"),
                rs.getString("tipo"),
                rs.getString("categoria"),
                rs.getString("moneda_base"),
                rs.getString("fecha_creacion")
        );
    }
}