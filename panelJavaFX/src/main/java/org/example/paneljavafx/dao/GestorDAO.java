package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Gestor;

import java.util.List;

public interface GestorDAO {

    List<Gestor> findAll();

    Gestor findById(int id);

    void save(Gestor gestor);

    void update(Gestor gestor);

    void delete(int id);
}