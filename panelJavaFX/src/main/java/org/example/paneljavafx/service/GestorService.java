package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.example.paneljavafx.model.Gestor;

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
    // DATA
    // =========================
    @Getter
    private final ObservableList<Gestor> gestores =
            FXCollections.observableArrayList();

    private final StorageService<Gestor> storageService =
            new StorageService<>(
                    "data/gestores.json",
                    new com.google.gson.reflect.TypeToken<List<Gestor>>() {}.getType()
            );

    // =========================
    // INIT
    // =========================
    private GestorService() {

        List<Gestor> loaded = storageService.load();

        if (loaded != null) {
            gestores.setAll(loaded);
        }

        printGestores();
    }

    // =========================
    // ACCESS
    // =========================
    public ObservableList<Gestor> getAllGestores() {
        return gestores;
    }

    // =========================
    // CRUD
    // =========================
    public void addGestor(Gestor gestor) {
        gestores.add(gestor);
        save();
    }

    public boolean existsEmail(String email) {
        return gestores.stream()
                .anyMatch(g -> g.getEmail() != null
                        && g.getEmail().equalsIgnoreCase(email));
    }

    // =========================
    // SAVE
    // =========================
    public void save() {
        storageService.save(gestores);
        System.out.println("💾 Gestores saved");
    }

    // =========================
    // DEBUG
    // =========================
    public void printGestores() {

        System.out.println("\n================ GESTORES ================");

        gestores.forEach(g -> {
            System.out.println("ID GESTOR: " + g.getId_gestor());
            System.out.println("NOMBRE: " + g.getNombre());
            System.out.println("EMAIL: " + g.getEmail());
            System.out.println("RIESGO: " + g.getPerfil_riesgo());
            System.out.println("----------------------------");
        });

        System.out.println("TOTAL GESTORES: " + gestores.size());
        System.out.println("=========================================\n");
    }
}