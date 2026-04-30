package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.FundAssetPosition;
import java.util.List;
import java.util.Optional;

public interface FundAssetPositionDAO {

    void save(FundAssetPosition position);

    List<FundAssetPosition> findAll();

    Optional<FundAssetPosition> findById(String id);

    List<FundAssetPosition> findByFundId(String fundId);

    List<FundAssetPosition> findByAssetId(String assetId);

    boolean update(String id, FundAssetPosition position);

    boolean deleteById(String id);
}