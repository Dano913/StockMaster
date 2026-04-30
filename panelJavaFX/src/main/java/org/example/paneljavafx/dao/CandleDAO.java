package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.Candle;

import java.util.List;
import java.util.Optional;

public interface CandleDAO {

    void save(Candle candle);

    List<Candle> findAll();

    Optional<Candle> findById(long timestamp);

    boolean update(Candle candle);

    boolean deleteById(long timestamp);
}