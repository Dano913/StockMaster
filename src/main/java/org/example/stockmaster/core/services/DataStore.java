package org.example.stockmaster.core.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.stockmaster.core.model.Candle;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class DataStore {

    private static final String FILE_NAME = "candles.json";
    private static final Gson gson = new Gson();

    // -------------------------
    // GUARDAR
    // -------------------------
    public static void guardar(ArrayList<Candle> candles) {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(candles, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // CARGAR
    // -------------------------
    public static ArrayList<Candle> cargar() {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            return gson.fromJson(
                    reader,
                    new TypeToken<ArrayList<Candle>>() {}.getType()
            );
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}