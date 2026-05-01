package org.example.paneljavafx.data;

import org.example.paneljavafx.dao.AssetDAO;
import org.example.paneljavafx.dao.FundDAO;
import org.example.paneljavafx.dao.impl.AssetImpl;
import org.example.paneljavafx.dao.impl.FundImpl;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;

import java.util.List;
import java.util.Map;

public class MarketDataSource {

    private final AssetDAO assetDAO = new AssetImpl();
    private final FundDAO  fundDAO  = new FundImpl();

    public List<Asset> loadAssets() {
        return assetDAO.findAll();
    }

    public List<Fund> loadFunds() {
        return fundDAO.findAll();
    }

    public Map<String, Double> loadLastPrices() {
        return PriceRecordReader.loadLastPrices();
    }
}