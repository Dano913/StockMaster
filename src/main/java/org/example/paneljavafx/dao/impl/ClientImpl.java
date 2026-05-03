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
        String sql = "SELECT * FROM client";

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

    public Optional<Client> findByUserId(int userId) {

        String sql = """
        SELECT id, gestor_id, name, surname, email,
               national_id, join_date, country, user_id
        FROM client
        WHERE user_id = ?
    """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Client c = new Client();

                    c.setClientId(rs.getInt("id"));
                    c.setGestorId(rs.getInt("gestor_id"));
                    c.setName(rs.getString("name"));
                    c.setSurname(rs.getString("surname"));
                    c.setEmail(rs.getString("email"));
                    c.setNationalId(rs.getString("national_id"));
                    c.setJoinDate(LocalDate.parse(rs.getString("join_date")));
                    c.setCountry(rs.getString("country"));
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
            INSERT INTO client
            (gestor_id, name, surname, email, national_id, join_date, country)
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
            UPDATE client SET
                gestor_id=?,
                name=?,
                surname=?,
                email=?,
                national_id=?,
                join_date=?,
                country=?
            WHERE id=?
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

        String sql = "DELETE FROM client WHERE id = ?";

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

        c.setClientId(rs.getInt("id"));
        c.setGestorId(rs.getObject("gestor_id", Integer.class));

        c.setName(rs.getString("name"));
        c.setSurname(rs.getString("surname"));
        c.setEmail(rs.getString("email"));
        c.setNationalId(rs.getString("national_id"));
        c.setCountry(rs.getString("country"));

        Date sqlDate = rs.getDate("join_date");
        c.setJoinDate(sqlDate != null ? sqlDate.toLocalDate() : null);
        c.setUserId(rs.getInt("user_id"));
        return c;
    }
}