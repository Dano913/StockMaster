package org.example.paneljavafx.dao;

import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> findAll() {

        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT * FROM clientes";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Cliente c = new Cliente();

                c.setIdCliente(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setEmail(rs.getString("email"));
                c.setDni(rs.getString("dni"));
                c.setPais(rs.getString("pais"));

                // cuidado con fecha (string por ahora)
                c.setFechaAlta(rs.getString("fecha_alta"));

                clientes.add(c);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando clientes", e);
        }

        return clientes;
    }
}