package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.dao.*;
import org.example.paneljavafx.dao.impl.*;
import org.example.paneljavafx.model.*;
import org.example.paneljavafx.service.dto.PortfolioSummary;
import org.example.paneljavafx.model.ClientFundPosition;
import org.example.paneljavafx.dao.ClientFundPositionDAO;
import org.example.paneljavafx.dao.impl.ClientFundPositionImpl;

import java.util.*;
import java.util.stream.Collectors;

public class GestorService {

    private static final GestorService INSTANCE = new GestorService();
    public static GestorService getInstance() { return INSTANCE; }
    private GestorService() {}

    private final GestorDAO gestorDAO = new GestorImpl();
    private final ClientDAO clientDAO = new ClientImpl();
    private final ClientFundPositionService positionService =
            ClientFundPositionService.getInstance();

    private final ObservableList<Client> myClients =
            FXCollections.observableArrayList();

    // ========================= GESTOR LOGUEADO =========================

    public List<Gestor> load() {
        return gestorDAO.findAll();
    }

    private boolean isAdmin() {
        User user = MainSessionHolder.getInstance().getCurrentUser();
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    public Optional<Gestor> getLoggedGestor() {

        User user = MainSessionHolder.getInstance().getCurrentUser();

        if (user == null) return Optional.empty();

        return gestorDAO.findByUserId(user.getId());
    }

    public int getLoggedGestorId() {
        return getLoggedGestor()
                .map(Gestor::getGestorId)
                .orElse(-1);
    }

    public List<Gestor> getAll() {
        return gestorDAO.findAll();
    }

    public String getGestorFullName(int gestorId) {

        return gestorDAO.findById(gestorId)
                .map(g -> g.getName() + " " + g.getSurname())
                .orElse("Sin gestor");
    }

    // ========================= CLIENTES =========================

    public ObservableList<Client> getVisibleMyClientsObservable() {

        if (isAdmin()) {
            myClients.setAll(clientDAO.findAll());
            return myClients;
        }

        int gestorId = getLoggedGestorId();

        if (gestorId == -1) {
            myClients.clear();
            return myClients;
        }

        myClients.setAll(
                clientDAO.findAll().stream()
                        .filter(c -> Objects.equals(c.getGestorId(), gestorId))
                        .toList()
        );

        return myClients;
    }

    public List<Client> getClientsByGestorId(int gestorId) {

        return clientDAO.findAll().stream()
                .filter(c -> Objects.equals(c.getGestorId(), gestorId))
                .toList();
    }

    public double getClientTotalValue(int clientId) {

        return positionService.getByClientId(clientId).stream()
                .mapToDouble(ClientFundPosition::getActualValue)
                .sum();
    }

    // ========================= CLIENTE SUMMARY (REUTILIZA MOTOR) =========================

    public Map<Client, PortfolioSummary> getClientSummaries(int gestorId) {

        return clientDAO.findAll().stream()
                .filter(c -> Objects.equals(c.getGestorId(), gestorId))
                .collect(Collectors.toMap(
                        c -> c,
                        c -> positionService.calculatePortfolio(
                                positionService.getByClientId(c.getClientId())
                        )
                ));
    }

    public double calculateManagedWalletByGestor(int gestorId) {

        if (gestorId == -1) return 0;

        return getClientSummaries(gestorId).values().stream()
                .mapToDouble(PortfolioSummary::getTotal)
                .sum();
    }

    // ========================= WALLET TOTAL =========================

    public double calculateManagedWallet() {

        int gestorId = getLoggedGestorId();

        if (gestorId == -1) return 0;

        return getClientSummaries(gestorId).values().stream()
                .mapToDouble(PortfolioSummary::getTotal)
                .sum();
    }

    // ========================= CLIENT COUNT =========================

    public int getClientCount() {
        return getVisibleMyClientsObservable().size();
    }

    // ========================= CRUD =========================

    public void save(Gestor gestor) {
        if (gestor == null) return;
        gestorDAO.save(gestor);
    }

    public void update(Gestor gestor) {
        if (gestor == null) return;
        gestorDAO.update(gestor);
    }

    public void delete(Integer id) {
        if (id == null) return;
        gestorDAO.deleteById(id);
    }

    public void deleteClient(int clientId) {
        clientDAO.deleteById(clientId);
    }
}