package org.example.paneljavafx.data;

import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.simulation.MarketEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    public static final List<Asset> assets = new ArrayList<>();
    public static final List<Fund>  funds  = new ArrayList<>();

    public static final Map<String, MarketEngine> engines = new HashMap<>();

    private DataStore() {}
}