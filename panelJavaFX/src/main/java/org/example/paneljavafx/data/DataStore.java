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
    public static final List<Fund> funds = new ArrayList<>();

    public static final Map<String, Fund> fundsById = new HashMap<>();
    public static final Map<String, Fund> fundsByName = new HashMap<>();

    public static final Map<String, MarketEngine> engines = new HashMap<>();

    private DataStore() {}

    public static void addFund(Fund fund) {
        funds.add(fund);
        fundsById.put(fund.getIdFondo(), fund);
        fundsByName.put(fund.getNombre(), fund);
    }

    public static void printFundNames() {
        System.out.println("=== FUND NAMES ===");

        funds.forEach(fund -> System.out.println(fund.getNombre()));
        funds.forEach(fund -> {
            String nombre = fund.getNombre();
            String id = fund.getIdFondo();

            System.out.println(nombre + " → " + id);
        });
    }
}