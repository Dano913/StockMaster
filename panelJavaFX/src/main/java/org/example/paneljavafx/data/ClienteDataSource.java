package org.example.paneljavafx.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Posicion;
import org.example.paneljavafx.model.Transaccion;

import java.io.InputStream;
import java.util.List;

public class ClienteDataSource {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String PATH = "/data/clientes.json";

    public List<Cliente> load() {

        try (InputStream is = getClass().getResourceAsStream(PATH)) {

            if (is == null) {
                throw new RuntimeException("JSON no encontrado: " + PATH);
            }

            List<Cliente> clientes = mapper.readValue(is, new TypeReference<List<Cliente>>() {});

            return clientes;

        } catch (Exception e) {
            throw new RuntimeException("Error cargando clientes desde JSON", e);
        }
    }

    // =========================
    // DEBUG
    // =========================
    public void printClientesDetallado(List<Cliente> clientes) {

        System.out.println("\n========================================");
        System.out.println(clientes.size() + " CLIENTES CARGADOS EN SERVICE");
        System.out.println("========================================");

        System.out.printf("| %-3s | %-15s | %-15s | %-25s | %-10s |\n",
                "ID", "NOMBRE", "APELLIDO", "EMAIL", "PAIS");
        System.out.println("========================================================================================");

        for (Cliente c : clientes) {

            System.out.printf("| %-3d | %-15s | %-15s | %-25s | %-10s |\n",
                    c.getIdCliente(),
                    c.getNombre(),
                    c.getApellido(),
                    c.getEmail(),
                    c.getPais()
            );

            // =========================
            // POSICIONES
            // =========================
            if (c.getPosiciones() != null && !c.getPosiciones().isEmpty()) {

                System.out.println("      📊 POSICIONES:");

                for (Posicion p : c.getPosiciones()) {

                    System.out.println("      ├─ Fondo: " + p.getIdFondo());

                    double total = p.getTransacciones().stream()
                            .mapToDouble(Transaccion::getImporte)
                            .sum();

                    System.out.println("      │   💰 Total: " + total);

                    // =========================
                    // TRANSACCIONES
                    // =========================
                    System.out.println("      │   📄 Transacciones:");

                    for (Transaccion t : p.getTransacciones()) {
                        System.out.println("      │      - " +
                                t.getTipo() +
                                " | " +
                                t.getImporte());
                    }
                }
            }

            System.out.println("----------------------------------------------------------------------------------------");
        }
    }
}