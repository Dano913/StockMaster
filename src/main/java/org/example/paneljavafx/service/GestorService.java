package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.dao.ClientDAO;
import org.example.paneljavafx.dao.ClientFundPositionDAO;
import org.example.paneljavafx.dao.GestorDAO;
import org.example.paneljavafx.dao.impl.ClientFundPositionImpl;
import org.example.paneljavafx.dao.impl.ClientImpl;
import org.example.paneljavafx.dao.impl.GestorImpl;
import org.example.paneljavafx.model.Client;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.model.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GestorService {

    // ========================= SINGLETON =========================
    private static final GestorService INSTANCE = new GestorService();
    public static GestorService getInstance() { return INSTANCE; }
    private GestorService() {}

    // ========================= DAO =========================
    private final GestorDAO gestorDAO = new GestorImpl();
    private final ClientDAO clientDAO = new ClientImpl();
    private final ClientFundPositionDAO clientFundPositionDAO =
            new ClientFundPositionImpl();

    // ========================= CACHE =========================
        private final ObservableList<Gestor> gestores = FXCollections.observableArrayList();
    private boolean loaded = false;

    // ========================= LOAD =========================
    public void load() {
        if (loaded) return;
        loaded = true;

        gestores.setAll(gestorDAO.findAll());
    }

    // ========================= GET GESTOR =========================
    public ObservableList<Gestor> getAll() {
        return gestores;
    }

    public Optional<Gestor> getById(int id) {
        return gestorDAO.findById(id);
    }

    // ========================= GET CLIENT =========================
    public List<Client> getClientesByGestorId(int gestorId, List<Client> clientes) {

        return clientes.stream()
                .filter(c -> c.getGestorId() != null)
                .filter(c -> c.getGestorId() == gestorId)
                .toList();
    }

    // ========================= GET WALLET ========================
    public double calculateManagedWallet(int gestorId) {

        return clientDAO.findAll().stream()
                .filter(client -> Objects.equals(client.getGestorId(), gestorId))
                .map(Client::getClientId)
                .flatMap(clientId ->
                        clientFundPositionDAO.findByClientId(clientId).stream()
                )
                .filter(position -> position.getTransactions() != null)
                .flatMap(position -> position.getTransactions().stream())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // ========================= CRUD GESTOR =========================
    public void save(Gestor gestor) {

        gestorDAO.save(gestor);

        gestores.add(gestor);
    }

    public void update(Gestor gestor) {

        gestorDAO.update(gestor);

        for (int i = 0; i < gestores.size(); i++) {

            if (Objects.equals(gestores.get(i).getGestorId(), gestor.getGestorId())) {

                gestores.set(i, gestor);

                return;
            }
        }
    }

    public void delete(Integer id) {

        if (id == null || id == 0) return;

        gestorDAO.deleteById(id);

        gestores.removeIf(g -> id.equals(g.getGestorId()));
    }
}