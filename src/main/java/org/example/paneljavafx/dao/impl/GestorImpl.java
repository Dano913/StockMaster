package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.GestorDAO;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestorImpl implements GestorDAO {

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

    @Override
    public Optional<Gestor> findById(int id) {

        String sql = "SELECT * FROM gestor WHERE id = ?";

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

    public Optional<Gestor> findByUserId(int userId) {

        String sql = """
            SELECT id, company_id, fund_id,
                   national_id,
                   name, surname,
                   years_of_experience,
                   risk_profile,
                   email, phone,
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

                    g.setGestorId(rs.getInt("id"));
                    g.setCompanyId(rs.getInt("company_id"));
                    g.setFundId(rs.getInt("fund_id"));
                    g.setNationalId(rs.getString("national_id"));

                    g.setName(rs.getString("name"));
                    g.setSurname(rs.getString("surname"));

                    g.setYearsOfExperience(rs.getInt("years_of_experience"));

                    String perfilStr = rs.getString("risk_profile");
                    g.setRiskProfile(
                            perfilStr != null
                                    ? Gestor.RiskProfile.valueOf(perfilStr.toUpperCase())
                                    : Gestor.RiskProfile.CONSERVADOR
                    );

                    g.setEmail(rs.getString("email"));
                    g.setPhone(rs.getString("phone"));
                    g.setUserId(rs.getInt("user_id"));

                    return Optional.of(g);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando gestor por userId", e);
        }

        return Optional.empty();
    }

    @Override
    public void save(Gestor g) {

        String sql = """
            INSERT INTO gestor
            (company_id, fund_id, national_id, name, surname,
             years_of_experience, risk_profile, email, phone, user_id)
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

    @Override
    public void update(Gestor g) {

        String sql = """
            UPDATE gestor
            SET company_id=?, fund_id=?, national_id=?, name=?, surname=?,
                years_of_experience=?, risk_profile=?, email=?, phone=?, user_id=?
            WHERE id=?
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

    @Override
    public void deleteById(int id) {

        String sql = "DELETE FROM gestor WHERE id=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando gestor", e);
        }
    }

    private Gestor mapGestor(ResultSet rs) throws SQLException {

        Gestor.RiskProfile profile;

        String profileStr = rs.getString("risk_profile");

        try {
            profile = (profileStr == null)
                    ? Gestor.RiskProfile.CONSERVADOR
                    : Gestor.RiskProfile.valueOf(profileStr.toUpperCase());
        } catch (Exception e) {
            profile = Gestor.RiskProfile.CONSERVADOR;
        }

        Gestor g = new Gestor();

        g.setGestorId(rs.getInt("id"));
        g.setCompanyId(rs.getInt("company_id"));
        g.setFundId(rs.getInt("fund_id"));

        g.setNationalId(rs.getString("national_id"));

        g.setName(rs.getString("name"));
        g.setSurname(rs.getString("surname"));

        g.setYearsOfExperience(rs.getInt("years_of_experience"));

        g.setRiskProfile(profile);

        g.setEmail(rs.getString("email"));
        g.setPhone(rs.getString("phone"));

        g.setUserId(rs.getInt("user_id"));

        return g;
    }
}