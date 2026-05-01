package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.dao.ClientDAO;
import org.example.paneljavafx.dao.ClientFundPositionDAO;
import org.example.paneljavafx.dao.UserDAO;
import org.example.paneljavafx.dao.impl.ClientImpl;
import org.example.paneljavafx.dao.impl.ClientFundPositionImpl;
import org.example.paneljavafx.dao.impl.UserImpl;
import org.example.paneljavafx.model.Client;
import org.example.paneljavafx.model.ClientFundPosition;
import org.example.paneljavafx.model.Transaction;
import org.example.paneljavafx.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.time.LocalDate;

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
    private final UserDAO userDAO = new UserImpl();
    private User loggedUser;

    // ========================= LOAD =========================

    public void load() {

        try {
            List<Client> clientList = clientDAO.findAll();

            System.out.println("📦 CLIENTES DESDE BD:");
            clientList.forEach(c ->
                    System.out.println(c.getClientId() + " - " + c.getName())
            );

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

    public Optional<Client> getById(int clientId) {

        System.out.println("🔍 BUSCANDO CLIENTE ID: " + clientId);

        Optional<Client> client = clients.stream()
                .filter(c -> c.getClientId() == clientId)
                .findFirst();

        client.ifPresentOrElse(
                c -> {
                    System.out.println("✔ CLIENTE ENCONTRADO");
                    System.out.println("ID: " + c.getClientId());
                    System.out.println("Nombre: " + c.getName());
                    System.out.println("Email: " + c.getEmail());
                },
                () -> System.out.println("❌ CLIENTE NO ENCONTRADO")
        );

        return client;
    }

    // ========================= POSITIONS =========================

    public List<ClientFundPosition> getPositionsByClientId(int clientId) {

        try {
            List<ClientFundPosition> positions =
                    clientFundPositionDAO.findByClientId(clientId);

            System.out.println("📊 POSICIONES CLIENTE " + clientId + ": " + positions.size());

            return positions;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ========================= CRUD CLIENT =========================
    public void save(Client client) {

        if (client == null) return;

        if (client.getJoinDate() == null) {
            client.setJoinDate(LocalDate.now());
        }

        clientDAO.save(client);
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

    public Optional<Client> getLoggedClient() {

        User user = UserService.getInstance().getLoggedUser();

        if (user == null) {
            System.out.println("❌ No hay usuario logeado");
            return Optional.empty();
        }

        int userId = user.getId();

        System.out.println("🔍 BUSCANDO CLIENTE POR USER ID: " + userId);

        try {
            Optional<Client> clientOpt = clientDAO.findByUserId(userId);

            if (clientOpt.isEmpty()) {
                System.out.println("❌ NO EXISTE CLIENTE PARA USER ID: " + userId);
                return Optional.empty();
            }

            Client client = clientOpt.get();

            System.out.println("✔ CLIENTE ENCONTRADO");
            System.out.println("ClientId: " + client.getClientId());
            System.out.println("UserId: " + userId);

            return Optional.of(client);

        } catch (Exception e) {
            System.out.println("❌ ERROR en findByUserId");
            e.printStackTrace();
            return Optional.empty();
        }
    }

}