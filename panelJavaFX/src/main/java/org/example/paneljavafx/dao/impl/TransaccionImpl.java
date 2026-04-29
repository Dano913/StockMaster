package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.TransaccionDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.Transaccion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaccionImpl implements TransaccionDAO {

    private Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    // ========================= FIND BY POSICION =========================
    @Override
    public List<Transaccion> findByPosicionId(int posicionId) {

        List<Transaccion> list = new ArrayList<>();

        String sql = """
            SELECT id_transaccion, tipo, importe, fecha
            FROM transaccion
            WHERE id_posicion = ?
            ORDER BY fecha DESC
        """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, posicionId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Transaccion t = new Transaccion();

                t.setIdTransaccion(rs.getInt("id_transaccion"));
                t.setTipo(rs.getString("tipo"));
                t.setImporte(rs.getDouble("importe"));
                t.setFecha(rs.getTimestamp("fecha").toLocalDateTime());

                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ========================= FIND BY CLIENTE =========================
    @Override
    public List<Transaccion> findByClienteId(int clienteId) {

        List<Transaccion> list = new ArrayList<>();

        String sql = """
            SELECT t.id_transaccion, t.tipo, t.importe, t.fecha
            FROM transaccion t
            JOIN posiciones p ON t.id_posicion = p.id_posicion
            WHERE p.id_cliente = ?
        """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, clienteId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Transaccion t = new Transaccion();

                t.setIdTransaccion(rs.getInt("id_transaccion"));
                t.setTipo(rs.getString("tipo"));
                t.setImporte(rs.getDouble("importe"));
                t.setFecha(rs.getTimestamp("fecha").toLocalDateTime());

                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ========================= SAVE =========================
    @Override
    public Transaccion save(int posicionId, Transaccion t) {

        String sql = """
            INSERT INTO transaccion (id_posicion, tipo, importe, fecha)
            VALUES (?, ?, ?, NOW())
        """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, posicionId);
            ps.setString(2, t.getTipo());
            ps.setDouble(3, t.getImporte());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                t.setIdTransaccion(rs.getInt(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;
    }
}