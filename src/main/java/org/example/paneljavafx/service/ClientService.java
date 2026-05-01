package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.dao.ClientDAO;
import org.example.paneljavafx.dao.ClientFundPositionDAO;
import org.example.paneljavafx.dao.impl.ClientImpl;
import org.example.paneljavafx.dao.impl.ClientFundPositionImpl;
import org.example.paneljavafx.model.Client;
import org.example.paneljavafx.model.ClientFundPosition;
import org.example.paneljavafx.model.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// - INSTANCE CLIENT
// 0 DAO CLIENT WITH OBSERVABLE CLIENTS AND POSITION
// 1 LOAD POSITIONS BY CLIENT ID
// 2 GET CLIENTS AND POSITIONS BY CLIENT ID
// 3 CRUD CLIENTS
// 4 CRUD POSITIONS
// 5 GET FUNDS
// 6 GET WALLET

public class ClientService {

    // ========================= SINGLETON =========================

    private static final ClientService INSTANCE = new ClientService();
    public static ClientService getInstance() {
        return INSTANCE;
    }
    private ClientService() {}

    // ========================= DAO =========================

    private final ClientDAO clientDAO = new ClientImpl();
    private final ClientFundPositionDAO clientFundPositionDAO = new ClientFundPositionImpl();
    private final ObservableList<Client> clients = FXCollections.observableArrayList();

    // ========================= LOAD =========================

    public void load() {

        try {
            List<Client> clientList = clientDAO.findAll();
            clients.setAll(clientList);

        } catch (Exception exception) {
            System.err.println("Error loading clients from DB");
            exception.printStackTrace();
        }
    }

    // ========================= GET =========================

    public ObservableList<Client> getAll() {
        return clients;
    }

    public Client getById(int clientId) {
        return clients.stream()
                .filter(client -> client.getClientId() == clientId)
                .findFirst()
                .orElse(null);
    }

    // ========================= POSITIONS =========================

    public List<ClientFundPosition> getPositionsByClientId(int clientId) {

        try {
            List<ClientFundPosition> positions =
                    clientFundPositionDAO.findByClientId(clientId);

            return positions != null ? positions : new ArrayList<>();

        } catch (Exception exception) {
            exception.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ========================= CRUD CLIENT =========================
    public void save(Client client) {

        Client saved = clientDAO.save(client);

        if (saved != null) {
            clients.add(saved);
        }
    }

    public void update(Client client) {

        clientDAO.update(client);

        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getClientId() == client.getClientId()) {
                clients.set(i, client);
                return;
            }
        }
    }

    public void delete(int clientId) {

        clientDAO.deleteById(clientId);
        clients.removeIf(c -> c.getClientId() == clientId);
    }

    // ========================= CRUD POSITION =========================

    public void addClientFundPosition(int clientId, ClientFundPosition position) {

        try {
            position.setClientId(clientId);

            ClientFundPosition saved = clientFundPositionDAO.save(position);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void addTransactionToPosition(int positionId, Transaction transaction) {

        ClientFundPosition position = clientFundPositionDAO.findById(positionId).orElse(null);

        if (position == null) return;

        position.addTransaction(transaction);

        clientFundPositionDAO.update(position);
    }

    // ========================= EXTRA =========================

    public long countFund(List<ClientFundPosition> positions) {

        if (positions == null) return 0;

        return positions.stream()
                .map(ClientFundPosition::getFundId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }

    public double calculateWallet(List<ClientFundPosition> positions) {

        if (positions == null) return 0;

        return positions.stream()
                .filter(position -> position.getTransactions() != null)
                .flatMap(position -> position.getTransactions().stream())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public long count() {
        return clients.size();
    }
}