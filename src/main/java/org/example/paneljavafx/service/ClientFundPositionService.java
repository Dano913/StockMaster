package org.example.paneljavafx.service;

import javafx.scene.chart.PieChart;
import org.example.paneljavafx.dao.ClientDAO;
import org.example.paneljavafx.dao.ClientFundPositionDAO;
import org.example.paneljavafx.dao.impl.ClientFundPositionImpl;
import org.example.paneljavafx.dao.impl.ClientImpl;
import org.example.paneljavafx.model.Client;
import org.example.paneljavafx.model.ClientFundPosition;
import org.example.paneljavafx.service.dto.PortfolioSummary;

import java.util.*;
import java.util.stream.Collectors;

public class ClientFundPositionService {

    private static final ClientFundPositionService INSTANCE =
            new ClientFundPositionService();

    private final ClientFundPositionDAO clientFundPositionDAO =
            new ClientFundPositionImpl();

    private final ClientDAO clientDAO = new ClientImpl();

    private ClientFundPositionService() {}

    public static ClientFundPositionService getInstance() {
        return INSTANCE;
    }

    // =========================================================
    // BASE (YA EXISTENTE - NO TOCAR)
    // =========================================================

    public List<ClientFundPosition> getByClientId(int clientId) {
        return clientFundPositionDAO.findByClientId(clientId);
    }

    public ClientFundPosition save(ClientFundPosition position) {
        return clientFundPositionDAO.save(position);
    }

    public void update(ClientFundPosition position) {
        clientFundPositionDAO.update(position);
    }

    public void deleteById(int positionId) {
        clientFundPositionDAO.deleteById(positionId);
    }

    public double getTotalValue(List<ClientFundPosition> posiciones) {
        return posiciones.stream()
                .mapToDouble(ClientFundPosition::getActualValue)
                .sum();
    }

    public double calculateReturn(double totalValue) {

        double invested = totalValue * 0.9;
        if (invested == 0) return 0;

        return ((totalValue - invested) / invested) * 100;
    }

    public PortfolioSummary calculatePortfolio(List<ClientFundPosition> posiciones) {

        double total = getTotalValue(posiciones);
        double invested = total * 0.9;

        double rentabilidad = invested == 0 ? 0 :
                ((total - invested) / invested) * 100;

        return new PortfolioSummary(total, rentabilidad);
    }

    public List<PieChart.Data> buildPortfolioChart(
            List<ClientFundPosition> posiciones,
            Map<String, String> fundNames
    ) {
        List<PieChart.Data> data = new ArrayList<>();

        for (ClientFundPosition pos : posiciones) {

            double value = pos.getActualValue();
            if (value <= 0) continue;

            String fundName = fundNames.getOrDefault(
                    pos.getFundId(),
                    pos.getFundId()
            );

            data.add(new PieChart.Data(fundName, value));
        }

        return data;
    }

    public void sellPosition(ClientFundPosition position) {
        deleteById(position.getPositionId());
    }

    // =========================================================
    // 🧠 NUEVO: CAPA GESTOR (AGREGACIÓN)
    // =========================================================

    public Map<Client, PortfolioSummary> getClientSummaries(int gestorId) {

        return clientDAO.findAll().stream()
                .filter(c -> c.getGestorId() == gestorId)
                .collect(Collectors.toMap(
                        c -> c,
                        c -> calculatePortfolio(
                                clientFundPositionDAO.findByClientId(c.getClientId())
                        )
                ));
    }

    // =========================================================
    // 💰 TOTAL MANAGED WALLET
    // =========================================================

    public double calculateManagedWallet(int gestorId) {

        return getClientSummaries(gestorId).values().stream()
                .mapToDouble(PortfolioSummary::getTotal)
                .sum();
    }

    // =========================================================
    // 👥 CLIENT COUNT
    // =========================================================

    public int getClientCount(int gestorId) {
        return getClientSummaries(gestorId).size();
    }

    // =========================================================
    // 🥧 PIE CHART GESTOR (POR CLIENTE)
    // =========================================================

    public List<PieChart.Data> buildManagedChart(int gestorId) {

        Map<Client, PortfolioSummary> data = getClientSummaries(gestorId);

        double total = data.values().stream()
                .mapToDouble(PortfolioSummary::getTotal)
                .sum();

        return data.entrySet().stream()
                .map(e -> {

                    double percent = total == 0 ? 0 :
                            (e.getValue().getTotal() / total) * 100;

                    return new PieChart.Data(
                            e.getKey().getName() + " " + e.getKey().getSurname()
                                    + " (" + String.format("%.1f", percent) + "%)",
                            e.getValue().getTotal()
                    );
                })
                .toList();
    }

    // =========================================================
    // 🔝 RANKING CLIENTES
    // =========================================================

    public List<Map.Entry<Client, PortfolioSummary>> getTopClients(int gestorId) {

        return getClientSummaries(gestorId).entrySet().stream()
                .sorted((a, b) ->
                        Double.compare(
                                b.getValue().getTotal(),
                                a.getValue().getTotal()
                        )
                )
                .toList();
    }
}