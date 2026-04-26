package org.example.paneljavafx.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paneljavafx.model.Gestor;

import java.io.InputStream;
import java.util.List;

public class GestorDataSource {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String path = "/data/gestores.json";

    public List<Gestor> load() {
        try (InputStream is = getClass().getResourceAsStream(path)) {

            if (is == null) return List.of();

            return mapper.readValue(is, new TypeReference<List<Gestor>>() {});

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}