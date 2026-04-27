package org.example.paneljavafx.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paneljavafx.model.Gestor;

import java.io.InputStream;
import java.util.List;

public class GestorDataSource {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String PATH = "/data/gestores.json";

    public List<Gestor> load() {

        try (InputStream is = getClass().getResourceAsStream(PATH)) {

            if (is == null) {
                throw new RuntimeException("JSON de gestores no encontrado: " + PATH);
            }

            List<Gestor> gestores = mapper.readValue(is, new TypeReference<List<Gestor>>() {});

            return gestores;

        } catch (Exception e) {
            throw new RuntimeException("Error cargando gestores", e);
        }
    }

    // =========================
    // DEBUG PRINT
    // =========================
    public void printGestoresDetallado(List<Gestor> gestores) {

        System.out.println("\n========================================");
        System.out.println(gestores.size() + " GESTORES CARGADOS EN SERVICE");
        System.out.println("========================================");

        for (Gestor g : gestores) {

            System.out.println(
                    g.getIdGestor() + " | " +
                            g.getNombre() + " " +
                            g.getApellidos() + " | " +
                            g.getEmail() + " | " +
                            g.getTelefono()
            );

            System.out.println("   ├─ Empresa ID: " + g.getIdEmpresa());
            System.out.println("   ├─ Fondo ID: " + g.getIdFondo());
            System.out.println("   ├─ Experiencia: " + g.getAniosExperiencia() + " años");
            System.out.println("   └─ Perfil riesgo: " + g.getPerfilRiesgo());

            System.out.println("----------------------------------------");
        }
    }
}