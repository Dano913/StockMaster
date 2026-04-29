package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.ClienteDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.Cliente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteImpl implements ClienteDAO {

    // =========================
    // FIND ALL
    // =========================
    @Override
    public List<Cliente> findAll() {

        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT * FROM clientes";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapCliente(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando clientes", e);
        }

        return clientes;
    }

    // =========================
    // FIND BY ID
    // =========================
    @Override
    public Cliente findById(int id) {

        String sql = "SELECT * FROM clientes WHERE id_cliente=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCliente(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando cliente", e);
        }

        return null;
    }

    // =========================
    // SAVE
    // =========================
    @Override
    public Cliente save(Cliente c) {

        String sql = """
            INSERT INTO clientes
            (id_gestor, nombre, apellido, email, dni, fecha_alta, pais)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setObject(1, c.getIdGestor());
            stmt.setString(2, c.getNombre());
            stmt.setString(3, c.getApellido());
            stmt.setString(4, c.getEmail());
            stmt.setString(5, c.getDni());
            stmt.setString(6, String.valueOf(c.getFechaAlta()));
            stmt.setString(7, c.getPais());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                c.setIdCliente(rs.getInt(1));
            }

            return c;

        } catch (SQLException e) {
            throw new RuntimeException("Error guardando cliente", e);
        }
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public void update(Cliente c) {

        String sql = """
            UPDATE clientes
            SET id_gestor=?,
                nombre=?,
                apellido=?,
                email=?,
                dni=?,
                fecha_alta=?,
                pais=?
            WHERE id_cliente=?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, c.getIdGestor());
            stmt.setString(2, c.getNombre());
            stmt.setString(3, c.getApellido());
            stmt.setString(4, c.getEmail());
            stmt.setString(5, c.getDni());
            stmt.setString(6, String.valueOf(c.getFechaAlta()));
            stmt.setString(7, c.getPais());
            stmt.setInt(8, c.getIdCliente());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando cliente", e);
        }
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void delete(int id) {

        String sql = "DELETE FROM clientes WHERE id_cliente=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando cliente", e);
        }
    }

    // =========================
    // MAPPER
    // =========================
    private Cliente mapCliente(ResultSet rs) throws SQLException {

        Cliente c = new Cliente();

        c.setIdCliente(rs.getInt("id_cliente"));
        c.setIdGestor(rs.getObject("id_gestor") != null ? rs.getInt("id_gestor") : null);

        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));
        c.setEmail(rs.getString("email"));
        c.setDni(rs.getString("dni"));
        c.setPais(rs.getString("pais"));
        Date sqlDate = rs.getDate("fecha_alta");

        c.setFechaAlta(sqlDate != null ? sqlDate.toLocalDate() : null);

        return c;
    }
}