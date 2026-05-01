package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDAO {

    Client save(Client client);

    List<Client> findAll();

    Optional<Client> findById(int id);

    Optional<Client> findByUserId(int userId);

    void update(Client client);

    void deleteById(int id);
}