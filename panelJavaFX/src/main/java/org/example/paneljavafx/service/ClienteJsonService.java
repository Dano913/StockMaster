package org.example.paneljavafx.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Posicion;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteJsonService {

    private static ClienteJsonService instance;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private static ObservableList<Cliente> lastLoadedClientes;
    @Getter
    private static ObservableList<Posicion> allPosiciones;

    private ClienteJsonService() {}

    public static ClienteJsonService getInstance() {
        if (instance == null) {
            instance = new ClienteJsonService();
        }
        return instance;
    }

    public ObservableList<Cliente> loadFromJson(String jsonPath) {
        try {
            String resourcePath = "/" + jsonPath;
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);

            if (inputStream == null) {
                System.err.println("❌ Recurso NO encontrado: " + resourcePath);
                return FXCollections.observableArrayList();
            }

            String jsonContent = new String(inputStream.readAllBytes());
            inputStream.close();

            System.out.println("✅ Clientes cargados: " + resourcePath);

            List<Cliente> clientes = objectMapper.readValue(jsonContent,
                    new TypeReference<List<Cliente>>() {});

            // 🔥 IMPRIME POR CONSOLA
            System.out.println("\n📊 === CLIENTES CARGADOS DESDE JSON ===");
            System.out.println("📈 Total de clientes: " + clientes.size());
            System.out.println("====================================\n");

            for (int i = 0; i < clientes.size(); i++) {
                Cliente c = clientes.get(i);
                System.out.printf("👤 [%d] %s %s | Gestor: %d | Fondos: %d | %s%n",
                        c.getIdCliente(),
                        c.getNombre(),
                        c.getApellido(),
                        c.getGestor(),
                        c.getPosiciones().size(),
                        c.getEmail()
                );
            }
            System.out.println("====================================\n");

            lastLoadedClientes = FXCollections.observableArrayList(clientes);
            allPosiciones = FXCollections.observableArrayList(
                    clientes.stream()
                            .flatMap(c -> c.getPosiciones().stream())
                            .collect(Collectors.toList())
            );

            return lastLoadedClientes;

        } catch (IOException e) {
            System.err.println("❌ Error JSON: " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public Collection<Object> getClientePosiciones(int idCliente) {
        return List.of();
    }

    // Agrega estos métodos al ClienteJsonService existente:
    public static List<Posicion> getPosicionesByClienteId(int clienteId) {
        return lastLoadedClientes != null ?
                lastLoadedClientes.stream()
                        .filter(c -> c.getIdCliente() == clienteId)
                        .flatMap(c -> c.getPosiciones().stream())
                        .collect(Collectors.toList()) :
                new ArrayList<>();
    }
}