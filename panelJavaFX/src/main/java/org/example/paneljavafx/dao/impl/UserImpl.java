package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.UserDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.User;

import java.sql.*;

public class UserImpl implements UserDAO {

    @Override
    public User findByEmailAndPassword(String email, String password) {

        String sql = """
            SELECT * FROM users
            WHERE email = ? AND password = ?
            LIMIT 1
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error login user", e);
        }

        return null;
    }

    // =========================
    // FIND BY ID
    // =========================
    @Override
    public User findById(int id) {

        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user", e);
        }

        return null;
    }

    // =========================
    // CREATE USER
    // =========================
    @Override
    public User createUser(User user) {

        String sql = """
            INSERT INTO users (email, password, role, status)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getStatus());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    // =========================
    // UPDATE USER
    // =========================
    @Override
    public boolean updateUser(User user) {

        String sql = """
            UPDATE users
            SET email = ?, password = ?, role = ?, status = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getStatus());
            stmt.setInt(5, user.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    // =========================
    // UPDATE STATUS
    // =========================
    @Override
    public boolean updateStatus(int userId, String status) {

        String sql = "UPDATE users SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating status", e);
        }
    }

    // =========================
    // UPDATE ROLE
    // =========================
    @Override
    public boolean updateRole(int userId, String role) {

        String sql = "UPDATE users SET role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating role", e);
        }
    }

    // =========================
    // MAPPER
    // =========================
    private User mapUser(ResultSet rs) throws SQLException {

        User u = new User();

        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));

        return u;
    }
}