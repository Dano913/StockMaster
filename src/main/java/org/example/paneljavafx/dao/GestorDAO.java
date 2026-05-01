package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Gestor;

import java.util.List;
import java.util.Optional;

public interface GestorDAO {

    void save(Gestor gestor);

    List<Gestor> findAll();

    Optional<Gestor> findById(int id);

    Optional<Gestor> findByUserId(int userId);

    void update(Gestor gestor);

    void deleteById(int id);
}