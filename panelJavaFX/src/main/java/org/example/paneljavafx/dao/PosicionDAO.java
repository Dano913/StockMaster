package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Posicion;

import java.util.List;

public interface PosicionDAO {

    List<Posicion> findByClienteId(int clienteId);

    Posicion save(int clienteId, Posicion posicion);

    void update(Posicion posicion);
}