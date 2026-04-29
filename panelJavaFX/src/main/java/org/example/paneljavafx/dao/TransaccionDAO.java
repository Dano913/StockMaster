package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Transaccion;

import java.util.List;

public interface TransaccionDAO {

    List<Transaccion> findByPosicionId(int posicionId);

    List<Transaccion> findByClienteId(int clienteId);

    Transaccion save(int posicionId, Transaccion transaccion);

}