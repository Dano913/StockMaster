package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Candle;

import java.util.List;
import java.util.Optional;

public interface CandleDAO {

    void save(Candle candle);

    List<Candle> findAll();

    List<Candle> findByAssetId(String assetId);

    List<Candle> findByAssetIdBetween(String assetId, long from, long to);

    Optional<Candle> findLastByAssetId(String assetId);

    Optional<Candle> findById(long timestamp);
}