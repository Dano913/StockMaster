package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.example.paneljavafx.model.Gestor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GestorService {

    // =========================
    // SINGLETON
    // =========================
    private static final GestorService INSTANCE = new GestorService();

    public static GestorService getInstance() {
        return INSTANCE;
    }

    // =========================
    // PATH
    // =========================
    private final Path filePath = Paths.get(
            System.getProperty("user.home"),
            "StockMaster",
            "data",
            "gestores.json"
    );

    // =========================
    // DATA
    // =========================
    @Getter
    private final ObservableList<Gestor> gestores =
            FXCollections.observableArrayList();

    private final StorageService<Gestor> storageService =
            new StorageService<>(
                    filePath.toString(),
                    new com.google.gson.reflect.TypeToken<List<Gestor>>() {}.getType()
            );

    // =========================
    // INIT
    // =========================
    private GestorService() {

        try {
            Files.createDirectories(filePath.getParent());
        } catch (Exception e) {
            e.printStackTrace();
        }

        reload();

        System.out.println("📦 Gestores cargados: " + gestores.size());
        printGestores();
    }

    // =========================
    // CRUD
    // =========================
    public void addGestor(Gestor gestor) {

        if (gestor == null) return;

        if (existsEmail(gestor.getEmail())) {
            System.out.println("⚠️ Email duplicado");
            return;
        }

        gestores.add(gestor);
        save();
    }

    public boolean existsEmail(String email) {

        if (email == null) return false;

        return gestores.stream()
                .anyMatch(g -> email.equalsIgnoreCase(g.getEmail()));
    }

    public void removeGestor(Gestor gestor) {

        if (gestor == null) return;

        gestores.remove(gestor);
        save();
    }

    public void updateGestor(Gestor updated) {

        if (updated == null) return;

        for (int i = 0; i < gestores.size(); i++) {

            Gestor g = gestores.get(i);

            if (g.getIdGestor() == updated.getIdGestor()) {
                gestores.set(i, updated);
                save();
                return;
            }
        }

        System.out.println("⚠️ Gestor no encontrado");
    }

    // =========================
    // SAVE (IMPORTANTE)
    // =========================
    public void save() {

        // 🔥 NO bloquees vacío (esto era tu bug principal)
        storageService.save(List.copyOf(gestores));

        System.out.println("💾 Guardado OK → " + gestores.size());
    }

    // =========================
    // LOAD / RELOAD
    // =========================
    public void reload() {

        List<Gestor> loaded = storageService.load();

        gestores.setAll(loaded != null ? loaded : List.of());
    }

    // =========================
    // RESET REAL
    // =========================
    public void resetAll() {

        gestores.clear();
        storageService.save(List.of()); // solo si quieres borrar TODO

        System.out.println("🧨 RESET COMPLETO");
    }

    // =========================
    // DEBUG
    // =========================
    public void printGestores() {

        System.out.println("\n================ GESTORES ================");

        if (gestores.isEmpty()) {
            System.out.println("⚠️ vacío");
        }

        gestores.forEach(g -> {
            System.out.println("ID: " + g.getIdGestor());
            System.out.println("NOMBRE: " + g.getNombre());
            System.out.println("EMAIL: " + g.getEmail());
            System.out.println("----------------");
        });

        System.out.println("TOTAL: " + gestores.size());
        System.out.println("=========================================\n");
    }

    public ObservableList<Gestor> getAllGestores() {
        return null;
    }
}