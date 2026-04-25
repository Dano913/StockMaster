package org.example.paneljavafx.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.paneljavafx.config.LocalDateTimeAdapter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StorageService<T> {

    private final Gson gson;
    private final String path;
    private final Type type;

    public StorageService(String path, Type type) {
        this.path = path;
        this.type = type;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    // =========================
    // LOAD FROM RESOURCES
    // =========================
    public List<T> load() {

        try (InputStream is =
                     getClass().getClassLoader().getResourceAsStream(path)) {

            if (is == null) {
                System.out.println("❌ No se encontró el archivo en resources: " + path);
                System.out.println(
                        getClass().getClassLoader().getResource("data/gestores.json")
                );
                return new ArrayList<>();
            }



            InputStreamReader reader = new InputStreamReader(is);

            List<T> data = gson.fromJson(reader, type);

            if (data == null) return new ArrayList<>();

            System.out.println("📦 Cargados: " + data.size());

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // =========================
    // SAVE TO FILE SYSTEM
    // =========================
    public void save(List<T> data) {

        try {
            java.io.File file = new java.io.File("data/gestores.json");
            file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);
            gson.toJson(data, writer);
            writer.close();

            System.out.println("💾 Guardado OK");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}