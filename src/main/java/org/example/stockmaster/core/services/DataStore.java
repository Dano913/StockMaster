package org.example.stockmaster.core.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.stockmaster.core.model.Asset;
import org.example.stockmaster.core.model.Candle;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataStore {

    private static final Gson gson = new Gson();

    // -------------------------
    // FILESYSTEM FILES
    // -------------------------
    private static final String CANDLES_FILE = "candles.json";
    private static final String ASSETS_FILE = "assets.json";

    // -------------------------
    // DEBUG
    // -------------------------
    private static void debugResourcePath(String path) {

        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource(path);

        System.out.println("\n📦 [RESOURCE DEBUG]");
        System.out.println("🔎 Searching: " + path);
        System.out.println("📍 Result: " + (url != null ? url : "NULL"));
        System.out.println("🧠 ClassLoader: " + Thread.currentThread().getContextClassLoader());
    }

    // -------------------------
    // CANDLES (filesystem)
    // -------------------------
    public static void guardarCandles(List<Candle> candles) {

        try (FileWriter writer = new FileWriter(CANDLES_FILE)) {
            gson.toJson(candles, writer);
        } catch (Exception e) {
            System.out.println("❌ Error saving candles");
            e.printStackTrace();
        }
    }

    public static ArrayList<Candle> cargarCandles() {

        System.out.println("📂 Loading candles: " + CANDLES_FILE);

        try (FileReader reader = new FileReader(CANDLES_FILE)) {

            return gson.fromJson(
                    reader,
                    new TypeToken<ArrayList<Candle>>() {}.getType()
            );

        } catch (Exception e) {
            System.out.println("⚠ candles.json not found → returning empty list");
            return new ArrayList<>();
        }
    }

    // -------------------------
    // ASSETS (classpath resources)
    // -------------------------
    public static ArrayList<Asset> cargarAssets() {

        System.out.println("\n📦 [RESOURCE DEBUG]");
        System.out.println("🔎 Searching: /data/assets.json");

        InputStream input = DataStore.class.getResourceAsStream("/data/assets.json");

        System.out.println("📍 Result: " + (input != null ? "FOUND" : "NULL"));

        if (input == null) {
            System.out.println("❌ assets.json NOT FOUND in classpath");
            System.out.println("👉 Expected: target/classes/data/assets.json");
            return new ArrayList<>();
        }

        try (input) {
            return gson.fromJson(
                    new InputStreamReader(input, StandardCharsets.UTF_8),
                    new TypeToken<ArrayList<Asset>>() {}.getType()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // -------------------------
    // SAVE ASSETS (filesystem)
    // -------------------------
    public static void guardarAssets(List<Asset> assets) {

        try (FileWriter writer = new FileWriter(ASSETS_FILE)) {
            gson.toJson(assets, writer);
        } catch (Exception e) {
            System.out.println("❌ Error saving assets");
            e.printStackTrace();
        }
    }
}