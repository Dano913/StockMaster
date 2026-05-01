package org.example.paneljavafx.service;

import javafx.scene.chart.PieChart;
import org.example.paneljavafx.dao.ClientFundPositionDAO;
import org.example.paneljavafx.dao.impl.ClientFundPositionImpl;
import org.example.paneljavafx.model.ClientFundPosition;
import org.example.paneljavafx.model.Transaction;
import org.example.paneljavafx.service.dto.PortfolioSummary;
import org.example.paneljavafx.viewmodel.TransactionRowView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// PositionService.java
public class ClientFundPositionService {

    private static final ClientFundPositionService INSTANCE = new ClientFundPositionService();
    private final ClientFundPositionDAO clientFundPositionDAO = new ClientFundPositionImpl();

    private ClientFundPositionService() {}

    public static ClientFundPositionService getInstance() {
        return INSTANCE;
    }

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

    // ========================= CALCULO TOTAL CARTERA =========================
    public double getTotalValue(List<ClientFundPosition> posiciones) {
        return posiciones.stream()
                .mapToDouble(ClientFundPosition::getActualValue)
                .sum();
    }

    // ========================= CALCULO RENTABILIDAD =========================
    public double calculateReturn(double totalValue) {

        double invested = totalValue * 0.9;

        if (invested == 0) return 0;

        return ((totalValue - invested) / invested) * 100;
    }

    public PortfolioSummary calculatePortfolio(List<ClientFundPosition> posiciones) {

        double total = posiciones.stream()
                .mapToDouble(ClientFundPosition::getActualValue)
                .sum();

        double invertido = total * 0.9;

        double rentabilidad = invertido == 0 ? 0 :
                ((total - invertido) / invertido) * 100;

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
}