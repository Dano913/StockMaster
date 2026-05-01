package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Fund;

import java.util.List;
import java.util.Optional;

public interface FundDAO {

    void save(Fund fund);

    List<Fund> findAll();

    Optional<Fund> findById(String idFondo);

    boolean update(String idFondo, Fund fund);

    boolean deleteById(String idFondo);
}