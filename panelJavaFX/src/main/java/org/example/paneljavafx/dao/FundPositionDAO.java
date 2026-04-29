package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.FundPosition;
import java.util.List;
import java.util.Optional;

public interface FundPositionDAO {
    void save(FundPosition position);
    List<FundPosition> findAll();
    Optional<FundPosition> findById(String id);
    List<FundPosition> findByFundId(String fundId);
    List<FundPosition> findByAssetId(String assetId);
    boolean update(String id, FundPosition position);
    boolean deleteById(String id);
}