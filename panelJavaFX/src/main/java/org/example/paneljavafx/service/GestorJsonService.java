package org.example.paneljavafx.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.example.paneljavafx.model.Gestor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GestorJsonService {

    private static GestorJsonService instance;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private static ObservableList<Gestor> lastLoadedGestores;

    private GestorJsonService() {}

    public static GestorJsonService getInstance() {
        if (instance == null) {
            instance = new GestorJsonService();
        }
        return instance;
    }

    public ObservableList<Gestor> loadFromJson(String jsonPath) {
        try {
            // 🔥 Maven resources: src/main/resources/data/gestores.json
            String resourcePath = "/" + jsonPath;  // /data/gestores.json
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);

            if (inputStream == null) {
                System.err.println("❌ Recurso NO encontrado: " + resourcePath);
                System.err.println("📁 Debe estar en: src/main/resources" + jsonPath);
                return FXCollections.observableArrayList();
            }

            String jsonContent = new String(inputStream.readAllBytes());
            inputStream.close();

            System.out.println("✅ Recurso cargado: " + resourcePath);

            List<Gestor> gestores = objectMapper.readValue(jsonContent,
                    new TypeReference<List<Gestor>>() {});

            // 🔥 IMPRIME
            System.out.println("\n📊 === DATOS CARGADOS DESDE JSON ===");
            System.out.println("📈 Total de gestores: " + gestores.size());
            System.out.println("====================================\n");

            for (int i = 0; i < gestores.size(); i++) {
                Gestor g = gestores.get(i);
                System.out.printf("👤 [%d] %s %s | Exp: %d | Riesgo: %s | %s%n",
                        g.getIdGestor(),
                        g.getNombre(),
                        g.getApellidos() != null ? g.getApellidos() : "",
                        g.getAniosExperiencia(),
                        g.getPerfilRiesgo(),
                        g.getEmail()
                );
            }
            System.out.println("====================================\n");

            lastLoadedGestores = FXCollections.observableArrayList(gestores);
            return lastLoadedGestores;

        } catch (IOException e) {
            System.err.println("❌ Error JSON: " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
}