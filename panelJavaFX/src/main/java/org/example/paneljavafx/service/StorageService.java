package org.example.paneljavafx.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.paneljavafx.config.LocalDateTimeAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StorageService<T> {

    private final Gson gson;
    private final File file;
    private final Type type;

    public StorageService(String path, Type type) {
        this.file = new File(path);
        this.type = type;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        // 🔥 SOLO crear si no existe (NO escribir [])
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("🆕 Archivo creado: " + file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // =========================
    // LOAD (CORRECTO)
    // =========================
    public List<T> load() {
        try {

            String json = Files.readString(file.toPath());

            System.out.println("RAW JSON:");
            System.out.println(json);

            if (json == null || json.isBlank()) {
                return new ArrayList<>();
            }

            List<T> data = gson.fromJson(json, type);

            if (data == null) return new ArrayList<>();

            System.out.println("SIZE DESPUÉS DE PARSEO: " + data.size());

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // =========================
    // SAVE (SEGURO)
    // =========================
    public void save(List<T> data) {
        try (FileWriter writer = new FileWriter(file)) {

            gson.toJson(data, writer);

            System.out.println("💾 Guardado en: " + file.getPath());
            System.out.println("📦 Elementos guardados: " + data.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}