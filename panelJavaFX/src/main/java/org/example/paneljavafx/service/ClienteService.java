package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.data.ClienteDataSource;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Posicion;

import java.util.List;

public class ClienteService {

    // =========================
    // DATA SOURCE
    // =========================
    private final ClienteDataSource dataSource = new ClienteDataSource();

    // =========================
    // STATE (MEMORIA)
    // =========================
    private final ObservableList<Cliente> clientes =
            FXCollections.observableArrayList();

    // =========================
    // LOAD
    // =========================
    public void load() {
        List<Cliente> loaded = dataSource.load("data/clientes.json");
        clientes.setAll(loaded);
    }

    // =========================
    // GETTERS
    // =========================
    public ObservableList<Cliente> getAll() {
        return clientes;
    }

    // =========================
    // POSICIONES (helper lógico)
    // =========================
    public List<Posicion> getPosicionesByClienteId(int clienteId) {

        return clientes.stream()
                .filter(c -> c.getIdCliente() == clienteId)
                .flatMap(c -> c.getPosiciones().stream())
                .toList();
    }

    // =========================
    // SEARCH LOGIC (opcional para controller limpio)
    // =========================
    public boolean matches(Cliente c, String q) {

        if (q == null || q.isBlank()) return true;

        String query = q.toLowerCase();

        return c.getNombre().toLowerCase().contains(query)
                || c.getApellido().toLowerCase().contains(query)
                || c.getEmail().toLowerCase().contains(query);
    }
}