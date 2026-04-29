package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.dao.ClienteDAO;
import org.example.paneljavafx.dao.impl.ClienteImpl;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.model.Posicion;
import org.example.paneljavafx.model.Transaccion;

import java.util.Collections;
import java.util.List;

public class ClienteService {

    // =========================
    // SINGLETON
    // =========================
    private static final ClienteService INSTANCE = new ClienteService();

    public static ClienteService getInstance() {
        return INSTANCE;
    }

    private ClienteService() {}

    // =========================
    // DEPENDENCIAS
    // =========================
    private final ClienteDAO clienteDAO = new ClienteImpl();
    private final GestorService gestorService = GestorService.getInstance();

    // =========================
    // CACHE UI (JavaFX)
    // =========================
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();

    // =========================
    // LOAD
    // =========================
    public void load() {

        List<Cliente> data = clienteDAO.findAll();

        // Evitar nulls en relaciones
        data.forEach(c -> {
            if (c.getPosiciones() == null) {
                c.setPosiciones(Collections.emptyList());
            }
        });

        clientes.setAll(data);
    }

    // =========================
    // GETTERS
    // =========================
    public ObservableList<Cliente> getAll() {
        return clientes;
    }

    public Cliente getById(int id) {
        return clientes.stream()
                .filter(c -> c.getIdCliente() == id)
                .findFirst()
                .orElse(null);
    }

    public long count() {
        return clientes.size();
    }

    // =========================
    // SEARCH
    // =========================
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

    // =========================
    // RELACIONES
    // =========================
    public List<Posicion> getPosicionesByClienteId(int clienteId) {

        return clientes.stream()
                .filter(c -> c.getIdCliente() == clienteId)
                .flatMap(c ->
                        (c.getPosiciones() == null
                                ? Collections.<Posicion>emptyList()
                                : c.getPosiciones()
                        ).stream()
                )
                .toList();
    }

    public double calcularCartera(Cliente cliente) {

        if (cliente.getPosiciones() == null) return 0;

        return cliente.getPosiciones().stream()
                .flatMap(p -> p.getTransacciones().stream())
                .mapToDouble(Transaccion::getImporte)
                .sum();
    }

    public long contarFondosUnicos(Cliente cliente) {

        if (cliente.getPosiciones() == null) return 0;

        return cliente.getPosiciones().stream()
                .map(Posicion::getIdFondo)
                .distinct()
                .count();
    }

    public Gestor getGestorByClientId(int clientId) {

        return clientes.stream()
                .filter(c -> c.getIdCliente() == clientId)
                .findFirst()
                .map(c -> c.getIdGestor() != null
                        ? gestorService.getById(c.getIdGestor())
                        : null
                )
                .orElse(null);
    }

    // =========================
    // CRUD
    // =========================
    public void save(Cliente cliente) {

        Cliente saved = clienteDAO.save(cliente);
        clientes.add(saved);
    }

    public void update(Cliente cliente) {

        clienteDAO.update(cliente);

        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getIdCliente() == cliente.getIdCliente()) {
                clientes.set(i, cliente);
                return;
            }
        }
    }

    public void delete(int idCliente) {

        clienteDAO.delete(idCliente);

        clientes.removeIf(c -> c.getIdCliente() == idCliente);
    }
}