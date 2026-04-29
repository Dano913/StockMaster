package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.GestorDAO;
import org.example.paneljavafx.model.Gestor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestorImpl implements GestorDAO {

    private final String URL = "jdbc:mysql://127.0.0.1:3307/panel";
    private final String USER = "root";
    private final String PASS = "root1234";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // =========================
    // FIND ALL
    // =========================
    @Override
    public List<Gestor> findAll() {

        List<Gestor> list = new ArrayList<>();

        String sql = "SELECT * FROM gestor";

        try (Connection con = getConnection();
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

    // =========================
    // FIND BY ID
    // =========================
    @Override
    public Gestor findById(int id) {

        String sql = "SELECT * FROM gestor WHERE id_gestor = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapGestor(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // =========================
    // SAVE (AUTO_INCREMENT FIX)
    // =========================
    @Override
    public void save(Gestor g) {

        String sql = """
            INSERT INTO gestor
            (id_empresa, id_fondo, dni, nombre, apellidos,
             anios_experiencia, perfil_riesgo, email, telefono)
            VALUES (?,?,?,?,?,?,?,?,?)
        """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, g.getIdEmpresa());
            ps.setInt(2, g.getIdFondo());
            ps.setString(3, g.getDni());
            ps.setString(4, g.getNombre());
            ps.setString(5, g.getApellidos());
            ps.setInt(6, g.getAniosExperiencia());

            ps.setString(7,
                    g.getPerfilRiesgo() != null
                            ? g.getPerfilRiesgo().name()
                            : "BAJO"
            );

            ps.setString(8, g.getEmail());
            ps.setString(9, g.getTelefono());

            ps.executeUpdate();

            // 🔥 IMPORTANTE: recuperar ID generado
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    g.setIdGestor(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // UPDATE (FIX COMPLETO)
    // =========================
    @Override
    public void update(Gestor g) {

        String sql = """
            UPDATE gestor
            SET id_empresa=?, id_fondo=?, dni=?, nombre=?, apellidos=?,
                anios_experiencia=?, perfil_riesgo=?, email=?, telefono=?
            WHERE id_gestor=?
        """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, g.getIdEmpresa());
            ps.setInt(2, g.getIdFondo());
            ps.setString(3, g.getDni());
            ps.setString(4, g.getNombre());
            ps.setString(5, g.getApellidos());
            ps.setInt(6, g.getAniosExperiencia());

            ps.setString(7,
                    g.getPerfilRiesgo() != null
                            ? g.getPerfilRiesgo().name()
                            : "BAJO"
            );

            ps.setString(8, g.getEmail());
            ps.setString(9, g.getTelefono());
            ps.setInt(10, g.getIdGestor());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void delete(int id) {

        String sql = "DELETE FROM gestor WHERE id_gestor=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // MAPPER ROBUSTO
    // =========================
    private Gestor mapGestor(ResultSet rs) throws SQLException {

        String perfilStr = rs.getString("perfil_riesgo");

        Gestor.PerfilRiesgo perfil;

        if (perfilStr == null || perfilStr.isBlank()) {
            perfil = Gestor.PerfilRiesgo.CONSERVADOR;
        } else {
            try {
                perfil = Gestor.PerfilRiesgo.valueOf(perfilStr.toUpperCase());
            } catch (Exception e) {
                perfil = Gestor.PerfilRiesgo.CONSERVADOR;
            }
        }

        return new Gestor(
                rs.getInt("id_gestor"),
                rs.getInt("id_empresa"),
                rs.getInt("id_fondo"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellidos"),
                rs.getInt("anios_experiencia"),
                perfil,
                rs.getString("email"),
                rs.getString("telefono")
        );
    }
}