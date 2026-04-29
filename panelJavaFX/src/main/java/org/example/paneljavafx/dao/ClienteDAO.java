package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Cliente;

import java.util.List;

public interface ClienteDAO {

    List<Cliente> findAll();

    Cliente findById(int id);

    Cliente save(Cliente cliente);

    void update(Cliente cliente);

    void delete(int id);
}