package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.PosicionDAO;
import org.example.paneljavafx.model.Posicion;
import org.example.paneljavafx.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PosicionImpl implements PosicionDAO {

    @Override
    public List<Posicion> findByClienteId(int clienteId) {

        List<Posicion> posiciones = new ArrayList<>();

        String sql = "SELECT * FROM posicion WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Posicion p = new Posicion();
                p.setIdPosicion(rs.getInt("id_posicion"));
                p.setClienteId(rs.getInt("id_cliente"));
                p.setIdFondo(rs.getString("id_fondo"));
                p.setCantidad(rs.getDouble("cantidad"));
                p.setValorActual(rs.getDouble("valor_actual"));

                posiciones.add(p);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error cargando posiciones", e);
        }

        return posiciones;
    }

    // =========================
    // SAVE
    // =========================
    @Override
    public Posicion save(int clienteId, Posicion posicion) {

        String sql = """
            INSERT INTO posicion (id_cliente, id_fondo, cantidad, valor_actual)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, clienteId);
            stmt.setString(2, posicion.getIdFondo());
            stmt.setDouble(3, posicion.getCantidad());
            stmt.setDouble(4, posicion.getValorActual());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                posicion.setIdPosicion(rs.getInt(1));
            }

            posicion.setClienteId(clienteId);

            return posicion;

        } catch (Exception e) {
            throw new RuntimeException("Error guardando posicion", e);
        }
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public void update(Posicion posicion) {

        String sql = """
            UPDATE posicion
            SET cantidad = ?, valor_actual = ?
            WHERE id_posicion = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, posicion.getCantidad());
            stmt.setDouble(2, posicion.getValorActual());
            stmt.setInt(3, posicion.getIdPosicion());

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando posicion", e);
        }
    }
}