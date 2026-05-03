package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.FundAssetPosition;
import java.util.List;
import java.util.Optional;

public interface FundAssetPositionDAO {

    List<FundAssetPosition> findAll();
}