package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.GestorDAO;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestorImpl implements GestorDAO {

    // ========================= FIND ALL =========================
    @Override
    public List<Gestor> findAll() {

        List<Gestor> list = new ArrayList<>();

        String sql = "SELECT * FROM gestor";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapGestor(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando gestores", e);
        }

        return list;
    }

    // ========================= FIND BY ID =========================
    @Override
    public Optional<Gestor> findById(int id) {

        String sql = "SELECT * FROM gestor WHERE id_gestor = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapGestor(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando gestor", e);
        }

        return Optional.empty();
    }

    // ========================= FIND BY USER ID (IMPORTANTE PARA PERFIL) =========================
    public Optional<Gestor> findByUserId(int userId) {

        String sql = """
            SELECT id_gestor, id_empresa, id_fondo,
                   dni,
                   nombre, apellidos,
                   anios_experiencia,
                   perfil_riesgo,
                   email, telefono,
                   user_id
            FROM gestor
            WHERE user_id = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    Gestor g = new Gestor();

                    g.setGestorId(rs.getInt("id_gestor"));
                    g.setCompanyId(rs.getInt("id_empresa"));
                    g.setFundId(rs.getInt("id_fondo"));
                    g.setNationalId(rs.getString("dni"));

                    g.setName(rs.getString("nombre"));
                    g.setSurname(rs.getString("apellidos"));

                    g.setYearsOfExperience(rs.getInt("anios_experiencia"));

                    String perfilStr = rs.getString("perfil_riesgo");
                    g.setRiskProfile(
                            perfilStr != null
                                    ? Gestor.RiskProfile.valueOf(perfilStr.toUpperCase())
                                    : Gestor.RiskProfile.CONSERVADOR
                    );

                    g.setEmail(rs.getString("email"));
                    g.setPhone(rs.getString("telefono"));
                    g.setUserId(rs.getInt("user_id"));

                    return Optional.of(g);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando gestor por userId", e);
        }

        return Optional.empty();
    }

    // ========================= SAVE =========================
    @Override
    public void save(Gestor g) {

        String sql = """
            INSERT INTO gestor
            (id_empresa, id_fondo, dni, nombre, apellidos,
             anios_experiencia, perfil_riesgo, email, telefono, user_id)
            VALUES (?,?,?,?,?,?,?,?,?,?)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, g.getCompanyId());
            ps.setInt(2, g.getFundId());
            ps.setString(3, g.getNationalId());
            ps.setString(4, g.getName());
            ps.setString(5, g.getSurname());
            ps.setInt(6, g.getYearsOfExperience());
            ps.setString(7, g.getRiskProfile() != null ? g.getRiskProfile().name() : "CONSERVADOR");
            ps.setString(8, g.getEmail());
            ps.setString(9, g.getPhone());
            ps.setInt(10, g.getUserId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    g.setGestorId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error guardando gestor", e);
        }
    }

    // ========================= UPDATE =========================
    @Override
    public void update(Gestor g) {

        String sql = """
            UPDATE gestor
            SET id_empresa=?, id_fondo=?, dni=?, nombre=?, apellidos=?,
                anios_experiencia=?, perfil_riesgo=?, email=?, telefono=?, user_id=?
            WHERE id_gestor=?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, g.getCompanyId());
            ps.setInt(2, g.getFundId());
            ps.setString(3, g.getNationalId());
            ps.setString(4, g.getName());
            ps.setString(5, g.getSurname());
            ps.setInt(6, g.getYearsOfExperience());
            ps.setString(7, g.getRiskProfile() != null ? g.getRiskProfile().name() : "CONSERVADOR");
            ps.setString(8, g.getEmail());
            ps.setString(9, g.getPhone());
            ps.setInt(10, g.getUserId());
            ps.setInt(11, g.getGestorId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando gestor", e);
        }
    }

    // ========================= DELETE =========================
    @Override
    public void deleteById(int id) {

        String sql = "DELETE FROM gestor WHERE id_gestor=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando gestor", e);
        }
    }

    // ========================= MAPPER =========================
    private Gestor mapGestor(ResultSet rs) throws SQLException {

        Gestor.RiskProfile perfil;

        String perfilStr = rs.getString("perfil_riesgo");

        try {
            perfil = (perfilStr == null)
                    ? Gestor.RiskProfile.CONSERVADOR
                    : Gestor.RiskProfile.valueOf(perfilStr.toUpperCase());
        } catch (Exception e) {
            perfil = Gestor.RiskProfile.CONSERVADOR;
        }

        Gestor g = new Gestor();

        g.setGestorId(rs.getInt("id_gestor"));
        g.setCompanyId(rs.getInt("id_empresa"));
        g.setFundId(rs.getInt("id_fondo"));

        g.setNationalId(rs.getString("dni"));

        g.setName(rs.getString("nombre"));
        g.setSurname(rs.getString("apellidos"));

        g.setYearsOfExperience(rs.getInt("anios_experiencia"));

        g.setRiskProfile(perfil);

        g.setEmail(rs.getString("email"));
        g.setPhone(rs.getString("telefono"));

        g.setUserId(rs.getInt("user_id"));

        return g;
    }
}