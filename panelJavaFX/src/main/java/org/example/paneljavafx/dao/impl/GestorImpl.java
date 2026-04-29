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

    @Override
    public List<Gestor> findAll() {

        List<Gestor> list = new ArrayList<>();

        String sql = "SELECT * FROM gestor";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Gestor g = new Gestor(
                        rs.getInt("id_gestor"),
                        rs.getInt("id_empresa"),
                        rs.getInt("id_fondo"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getInt("anios_experiencia"),
                        Gestor.PerfilRiesgo.valueOf(
                                rs.getString("perfil_riesgo").toUpperCase()
                        ),
                        rs.getString("email"),
                        rs.getString("telefono")
                );

                list.add(g);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando gestores", e);
        }

        return list;
    }

    @Override
    public Gestor findById(int id) {

        String sql = "SELECT * FROM gestor WHERE id_gestor = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Gestor(
                        rs.getInt("id_gestor"),
                        rs.getInt("id_empresa"),
                        rs.getInt("id_fondo"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getInt("anios_experiencia"),
                        Gestor.PerfilRiesgo.valueOf(
                                rs.getString("perfil_riesgo").toUpperCase()
                        ),
                        rs.getString("email"),
                        rs.getString("telefono")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void save(Gestor g) {
        String sql = "INSERT INTO gestor VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, g.getIdGestor());
            ps.setInt(2, g.getIdEmpresa());
            ps.setInt(3, g.getIdFondo());
            ps.setString(4, g.getDni());
            ps.setString(5, g.getNombre());
            ps.setString(6, g.getApellidos());
            ps.setInt(7, g.getAniosExperiencia());
            ps.setString(8, g.getPerfilRiesgo().name());
            ps.setString(9, g.getEmail());
            ps.setString(10, g.getTelefono());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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
            ps.setString(8, g.getPerfilRiesgo().name());
            ps.setString(8, g.getEmail());
            ps.setString(9, g.getTelefono());
            ps.setInt(10, g.getIdGestor());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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
}