package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.data.GestorDataSource;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Gestor;

import java.util.List;
import java.util.stream.Collectors;

public class GestorService {

    // =========================
    // SINGLETON
    // =========================
    private static final GestorService INSTANCE = new GestorService();

    public static GestorService getInstance() {
        return INSTANCE;
    }

    private GestorService() {}

    // =========================
    // STATE
    // =========================
    private boolean loaded = false;

    private final GestorDataSource gestorDataSource = new GestorDataSource();

    private final ObservableList<Gestor> gestores =
            FXCollections.observableArrayList();

    // =========================
    // LOAD
    // =========================
    public void load() {

        if (loaded) return;
        loaded = true;

        List<Gestor> loadedData = gestorDataSource.load();

        gestores.setAll(loadedData);

        gestorDataSource.printGestoresDetallado(gestores);

    }

    // =========================
    // GET ALL
    // =========================
    public ObservableList<Gestor> getAll() {
        return gestores;
    }

    public Gestor getById(int id) {

        return gestores.stream()
                .filter(g -> g.getIdGestor() == id)
                .findFirst()
                .orElse(null);
    }

    // =========================
    // CLIENTES POR GESTOR ID
    // =========================
    public List<Cliente> getClientesByGestorId(int gestorId, List<Cliente> allClientes) {

        return allClientes.stream()
                .filter(c -> c.getIdGestor() == gestorId)
                .collect(Collectors.toList());
    }

    public List<Gestor> search(String query) {

        if (query == null || query.isBlank()) {
            return gestores;
        }

        String q = query.toLowerCase();

        return gestores.stream()
                .filter(g ->
                        g.getNombre().toLowerCase().contains(q) ||
                                g.getApellidos().toLowerCase().contains(q) ||
                                g.getEmail().toLowerCase().contains(q) ||
                                g.getPerfilRiesgo().toLowerCase().contains(q)
                )
                .toList();
    }

    public boolean existsEmail(String email) {

        return gestores.stream()
                .anyMatch(g -> g.getEmail().equalsIgnoreCase(email));
    }

    public long countByPerfil(String perfil) {

        return gestores.stream()
                .filter(g -> g.getPerfilRiesgo().equalsIgnoreCase(perfil))
                .count();
    }
}