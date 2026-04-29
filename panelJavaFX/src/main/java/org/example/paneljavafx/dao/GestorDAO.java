package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Gestor;

import java.util.List;

public interface GestorDAO {

    List<Gestor> findAll();

    Gestor findById(int id);

    void save(Gestor g);

    void update(Gestor g);

    void delete(int id);
}