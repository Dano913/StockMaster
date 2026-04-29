package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.TransaccionDAO;
import org.example.paneljavafx.dao.impl.TransaccionImpl;
import org.example.paneljavafx.model.Posicion;
import org.example.paneljavafx.model.Transaccion;

import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    private static TransactionService instance;

    private final TransaccionDAO transaccionDAO = new TransaccionImpl();

    public static TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
    }

    // ========================= ADD TRANSACTION =========================
    public void addTransaction(Posicion posicion, Transaccion t) {

        if (posicion == null || t == null) return;

        if (posicion.getTransacciones() == null) {
            posicion.setTransacciones(new ArrayList<>());
        }

        // 1. memoria (UI)
        posicion.getTransacciones().add(t);

        // 2. persistencia (BD) 🔥
        transaccionDAO.save(posicion.getIdPosicion(), t);
    }

    // ========================= LOAD FROM DB =========================
    public List<Transaccion> getTransacciones(Posicion posicion) {

        if (posicion == null) return new ArrayList<>();

        return transaccionDAO.findByPosicionId(posicion.getIdPosicion());
    }
}