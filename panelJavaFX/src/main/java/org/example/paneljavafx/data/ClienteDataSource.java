package org.example.paneljavafx.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paneljavafx.model.Cliente;

import java.io.InputStream;
import java.util.List;

public class ClienteDataSource {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Cliente> load(String path) {

        try (InputStream is = getClass().getResourceAsStream("/" + path)) {

            if (is == null) {
                throw new RuntimeException("❌ JSON no encontrado: " + path);
            }

            return mapper.readValue(is, new TypeReference<List<Cliente>>() {});

        } catch (Exception e) {
            throw new RuntimeException("❌ Error cargando clientes desde JSON", e);
        }
    }
}