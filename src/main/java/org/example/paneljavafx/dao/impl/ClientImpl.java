package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.ClientDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.Client;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientImpl implements ClientDAO {

    // =========================
    // FIND ALL
    // =========================
    @Override
    public List<Client> findAll() {

        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clients.add(mapClient(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading clients", e);
        }

        return clients;
    }

    // =========================
    // FIND BY ID
    // =========================
    @Override
    public Optional<Client> findById(int id) {

        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapClient(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding client", e);
        }

        return Optional.empty();
    }

    public Optional<Client> findByUserId(int userId) {

        String sql = """
        SELECT id_cliente, id_gestor, nombre, apellido, email,
               dni, fecha_alta, pais, user_id
        FROM cliente
        WHERE user_id = ?
    """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Client c = new Client();

                    c.setClientId(rs.getInt("id_cliente"));
                    c.setGestorId(rs.getInt("id_gestor"));
                    c.setName(rs.getString("nombre"));
                    c.setSurname(rs.getString("apellido"));
                    c.setEmail(rs.getString("email"));
                    c.setNationalId(rs.getString("dni"));
                    c.setJoinDate(LocalDate.parse(rs.getString("fecha_alta")));
                    c.setCountry(rs.getString("pais"));
                    c.setUserId(rs.getInt("user_id"));

                    return Optional.of(c);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando cliente por userId", e);
        }

        return Optional.empty();
    }

    // =========================
    // SAVE
    // =========================
    @Override
    public Client save(Client c) {

        String sql = """
            INSERT INTO cliente
            (id_gestor, nombre, apellido, email, dni, fecha_alta, pais)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setObject(1, c.getGestorId());
            stmt.setString(2, c.getName());
            stmt.setString(3, c.getSurname());
            stmt.setString(4, c.getEmail());
            stmt.setString(5, c.getNationalId());
            stmt.setDate(6, c.getJoinDate() != null ? Date.valueOf(c.getJoinDate()) : null);
            stmt.setString(7, c.getCountry());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setClientId(rs.getInt(1));
                }
            }

            return c;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving client", e);
        }
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public void update(Client c) {

        String sql = """
            UPDATE cliente SET
                id_gestor=?,
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

            stmt.setObject(1, c.getGestorId());
            stmt.setString(2, c.getName());
            stmt.setString(3, c.getSurname());
            stmt.setString(4, c.getEmail());
            stmt.setString(5, c.getNationalId());
            stmt.setDate(6, c.getJoinDate() != null ? Date.valueOf(c.getJoinDate()) : null);
            stmt.setString(7, c.getCountry());
            stmt.setInt(8, c.getClientId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating client", e);
        }
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void deleteById(int id) {

        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting client", e);
        }
    }

    // =========================
    // MAPPER
    // =========================
    private Client mapClient(ResultSet rs) throws SQLException {

        Client c = new Client();

        c.setClientId(rs.getInt("id_cliente"));
        c.setGestorId(rs.getObject("id_gestor", Integer.class));

        c.setName(rs.getString("nombre"));
        c.setSurname(rs.getString("apellido"));
        c.setEmail(rs.getString("email"));
        c.setNationalId(rs.getString("dni"));
        c.setCountry(rs.getString("pais"));

        Date sqlDate = rs.getDate("fecha_alta");
        c.setJoinDate(sqlDate != null ? sqlDate.toLocalDate() : null);

        return c;
    }
}