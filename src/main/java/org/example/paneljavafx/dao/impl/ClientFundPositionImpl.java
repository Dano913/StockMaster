package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.ClientFundPositionDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.ClientFundPosition;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientFundPositionImpl implements ClientFundPositionDAO {

    // ========================= FIND BY CLIENT =========================
    @Override
    public List<ClientFundPosition> findByClientId(int clientId) {

        List<ClientFundPosition> positions = new ArrayList<>();

        String sql = "SELECT * FROM posicion WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clientId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    positions.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading client positions", e);
        }

        return positions;
    }

    // ========================= FIND ALL =========================
    @Override
    public List<ClientFundPosition> findAll() {

        List<ClientFundPosition> positions = new ArrayList<>();

        String sql = "SELECT * FROM posicion";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                positions.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading positions", e);
        }

        return positions;
    }

    // ========================= FIND BY ID =========================
    @Override
    public Optional<ClientFundPosition> findById(int id) {

        String sql = "SELECT * FROM posicion WHERE id_posicion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding position by id", e);
        }

        return Optional.empty();
    }

    // ========================= SAVE =========================
    @Override
    public ClientFundPosition save(ClientFundPosition position) {

        String sql = """
            INSERT INTO posicion (id_cliente, id_fondo, cantidad, valor_actual)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, position.getClientId());
            stmt.setString(2, position.getFundId());
            stmt.setDouble(3, position.getQuantity());
            stmt.setDouble(4, position.getActualValue());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    position.setPositionId(rs.getInt(1));
                }
            }

            return position;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving position", e);
        }
    }

    // ========================= UPDATE =========================
    @Override
    public void update(ClientFundPosition position) {

        String sql = """
            UPDATE posicion
            SET cantidad = ?, valor_actual = ?
            WHERE id_posicion = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, position.getQuantity());
            stmt.setDouble(2, position.getActualValue());
            stmt.setInt(3, position.getPositionId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating position", e);
        }
    }

    // ========================= DELETE =========================
    @Override
    public void deleteById(int id) {

        String sql = "DELETE FROM posicion WHERE id_posicion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting position", e);
        }
    }

    // ========================= MAPPER =========================
    private ClientFundPosition mapRow(ResultSet rs) throws SQLException {

        ClientFundPosition position = new ClientFundPosition();

        position.setPositionId(rs.getInt("id_posicion"));
        position.setClientId(rs.getInt("id_cliente"));
        position.setFundId(rs.getString("id_fondo"));
        position.setQuantity(rs.getDouble("cantidad"));
        position.setActualValue(rs.getDouble("valor_actual"));

        return position;
    }
}