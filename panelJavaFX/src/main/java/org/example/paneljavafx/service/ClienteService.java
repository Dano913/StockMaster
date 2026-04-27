package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.data.ClienteDataSource;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.model.Posicion;

import java.util.List;

public class ClienteService {

    // =========================
    // SINGLETON
    // =========================
    private static final ClienteService INSTANCE = new ClienteService();
    private final GestorService gestorService = GestorService.getInstance();

    public static ClienteService getInstance() {
        return INSTANCE;
    }

    private ClienteService() {}

    private final ClienteDataSource clienteDataSource = new ClienteDataSource();
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();

    // =========================
    // LOAD
    // =========================
    public void load() {

        List<Cliente> loadedData = clienteDataSource.load();

        clientes.setAll(loadedData);

        clienteDataSource.printClientesDetallado(clientes);
    }

    // =========================
    // GETTERS
    // =========================
    public ObservableList<Cliente> getAll() {
        return clientes;
    }

    public Cliente getById(int id) {

        return clientes.stream()
                .filter(g -> g.getIdCliente() == id)
                .findFirst()
                .orElse(null);
    }

    // =========================
    // POSICIONES
    // =========================
    public List<Posicion> getPosicionesByClienteId(int clienteId) {

        return clientes.stream()
                .filter(c -> c.getIdCliente() == clienteId)
                .flatMap(c -> c.getPosiciones().stream())
                .toList();
    }

    public Gestor getGestorByClientId(int clientId) {

        return clientes.stream()
                .filter(c -> c.getIdCliente() == clientId)
                .findFirst()
                .map(c -> GestorService.getInstance()
                        .getById(c.getIdGestor()))
                .orElse(null);
    }

    public List<Cliente> search(String query) {

        if (query == null || query.isBlank()) {
            return clientes;
        }

        String q = query.toLowerCase();

        return clientes.stream()
                .filter(c ->
                        c.getNombre().toLowerCase().contains(q) ||
                                c.getApellido().toLowerCase().contains(q) ||
                                c.getEmail().toLowerCase().contains(q)
                )
                .toList();
    }

    public boolean existsEmail(String email) {

        return clientes.stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
    }

    public long count() {
        return clientes.size();
    }
}