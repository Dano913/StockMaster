package org.example.stockmaster.javafx.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.stockmaster.javafx.config.LocalDateTimeAdapter;
import org.example.stockmaster.javafx.model.TimeRecord;
import org.example.stockmaster.javafx.model.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StorageService {

    // =========================
    // JSON CONFIG
    // =========================
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private final String PATH = "data/users.json";

    // =========================
    // LOAD DATA
    // =========================
    public List<User> load() {

        File file = new File(PATH);

        System.out.println("🔍 [LOAD] Path: " + file.getAbsolutePath());
        System.out.println("🔍 [LOAD] Exists: " + file.exists());

        try {

            // =========================
            // CREATE DIRECTORY
            // =========================
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                System.out.println("📁 Creating directory...");
                file.getParentFile().mkdirs();
            }

            // =========================
            // CREATE FILE
            // =========================
            if (!file.exists()) {
                System.out.println("📄 File does not exist → creating...");
                file.createNewFile();
                return new ArrayList<>();
            }

            System.out.println("📖 Reading JSON file...");

            FileReader reader = new FileReader(file);

            Type type = new TypeToken<List<User>>() {}.getType();

            List<User> data = gson.fromJson(reader, type);

            reader.close();

            // =========================
            // NULL CHECK
            // =========================
            if (data == null) {
                System.out.println("⚠ JSON is empty or null → returning empty list");
                data = new ArrayList<>();
            }
            System.out.println("✅ FINAL USERS LOADED: " + data.size());

            int totalRecords = data.stream()
                    .mapToInt(u -> u.getTimeRecords() == null ? 0 : u.getTimeRecords().size())
                    .sum();

            System.out.println("📊 TOTAL TIME RECORDS LOADED: " + totalRecords);

            return data;

        } catch (Exception e) {

            System.out.println("❌ ERROR DURING LOAD()");
            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    // =========================
    // SAVE DATA
    // =========================
    public void save(List<User> users) {

        try {

            File file = new File(PATH);
            file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);
            gson.toJson(users, writer);
            writer.close();

            System.out.println("💾 Saving to: " + file.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("❌ Error in save()");
            e.printStackTrace();
        }
    }

    private void printUsersDebug(List<User> data) {

        for (User user : data) {

            System.out.println("\n==========================");
            System.out.println("👤 USER: " + user.getName());
            System.out.println("📧 EMAIL: " + user.getEmail());

            if (user.getTimeRecords() == null) {
                System.out.println("⚠ TIME RECORDS = NULL → creando lista nueva");
                user.setTimeRecords(new ArrayList<>());
            } else {
                System.out.println("📦 TIME RECORDS FOUND: " + user.getTimeRecords().size());
            }

            printTimeRecords(user);
        }

        System.out.println("==========================\n");
    }

    private void printTimeRecords(User user) {

        for (TimeRecord tr : user.getTimeRecords()) {

            System.out.println("   --------------------");
            System.out.println("   🧾 CODE: " + tr.getCode());
            System.out.println("   👤 USER: " + tr.getUser());
            System.out.println("   ⏱ DATE: " + tr.getDateTime());
            System.out.println("   🔵 TYPE: " + tr.getType());
        }
    }
}