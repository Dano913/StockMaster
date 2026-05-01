package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.ClientFundPosition;

import java.util.List;
import java.util.Optional;

public interface ClientFundPositionDAO {

    List<ClientFundPosition> findByClientId(int clientId);

    List<ClientFundPosition> findAll();

    Optional<ClientFundPosition> findById(int id);

    ClientFundPosition save(ClientFundPosition position);

    void update(ClientFundPosition position);

    void deleteById(int id);
}