package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetDAO {

    void save(Asset asset);

    List<Asset> findAll();

    Optional<Asset> findById(String id);

    boolean update(String id, Asset asset);

    boolean deleteById(String id);
}