package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.dao.GestorDAO;
import org.example.paneljavafx.dao.impl.GestorImpl;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.model.Transaccion;

import java.util.List;

public class GestorService {

    private final ClienteService clienteService = ClienteService.getInstance();
    private static final GestorService INSTANCE = new GestorService();

    public static GestorService getInstance() {
        return INSTANCE;
    }

    private GestorService() {}

    // =========================
    // DAO (implementación concreta)
    // =========================
    private final GestorDAO gestorDAO = new GestorImpl();

    // =========================
    // STATE UI
    // =========================
    private final ObservableList<Gestor> gestores =
            FXCollections.observableArrayList();

    private boolean loaded = false;

    // =========================
    // LOAD
    // =========================
    public void load() {

        if (loaded) return;
        loaded = true;

        gestores.setAll(gestorDAO.findAll());
    }

    // =========================
    // GETTERS
    // =========================
    public ObservableList<Gestor> getAll() {
        return gestores;
    }

    public Gestor getById(int id) {
        return gestorDAO.findById(id);
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public List<Cliente> getClientesByGestorId(int gestorId, List<Cliente> clientes) {
        return clientes.stream()
                .filter(c -> c.getIdGestor() == gestorId)
                .toList();
    }

    public double calcularPatrimonioGestionado(int gestorId, List<Cliente> clientes) {

        return clientes.stream()
                .filter(c -> c.getIdGestor() == gestorId)
                .flatMap(c -> c.getPosiciones().stream())
                .flatMap(p -> p.getTransacciones().stream())
                .mapToDouble(Transaccion::getImporte)
                .sum();
    }

    public List<Gestor> search(String query) {

        if (query == null || query.isBlank()) return gestores;

        String q = query.toLowerCase();

        return gestores.stream()
                .filter(g ->
                        g.getNombre().toLowerCase().contains(q) ||
                                g.getApellidos().toLowerCase().contains(q) ||
                                g.getEmail().toLowerCase().contains(q) ||
                                g.getPerfilRiesgo().name().contains(q.toUpperCase())
                )
                .toList();
    }

    public int countClientsByGestor(int gestorId) {

        return clienteService.getAll() == null
                ? 0
                : (int) clienteService.getAll().stream()
                .filter(c -> c.getIdGestor() != null)
                .filter(c -> c.getIdGestor() == gestorId)
                .count();
    }

    public Gestor getGestorWithLessClients() {

        return gestores.stream()
                .min((g1, g2) ->
                        Integer.compare(
                                countClientsByGestor(g1.getIdGestor()),
                                countClientsByGestor(g2.getIdGestor())
                        )
                )
                .orElse(null);
    }
}