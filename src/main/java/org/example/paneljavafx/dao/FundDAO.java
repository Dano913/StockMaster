package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Fund;

import java.util.List;
import java.util.Optional;

public interface FundDAO {

    List<Fund> findAll();

    Optional<Fund> findById(String idFondo);


}