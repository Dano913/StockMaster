package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Asset;
import java.util.List;
import java.util.Optional;

public interface AssetDAO {

    List<Asset> findAll();

    Optional<Asset> findById(String id);
}