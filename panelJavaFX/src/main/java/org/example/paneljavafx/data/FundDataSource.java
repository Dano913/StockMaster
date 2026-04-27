package org.example.paneljavafx.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paneljavafx.model.Fund;

import java.io.InputStream;
import java.util.List;

public class FundDataSource {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Fund> load() {

        try (InputStream is = getClass().getResourceAsStream("/data/funds.json")) {

            if (is == null) {
                throw new RuntimeException("JSON de fondos no encontrado");
            }

            List<Fund> funds = mapper.readValue(is, new TypeReference<List<Fund>>() {});

            return funds;

        } catch (Exception e) {
            throw new RuntimeException("Error cargando fondos", e);
        }
    }

    // =========================
    // DEBUG PRINT
    // =========================
    public void printFunds(List<Fund> funds) {

        System.out.println("\n========================================");
        System.out.println(funds.size() + " FONDOS CARGADOS EN SERVICE");
        System.out.println("========================================");
        System.out.printf("| %-10s | %-40s | %-30s |\n",
                "ID", "NOMBRE", "TIPO");
        System.out.println("====================================================");

        for (Fund f : funds) {

            System.out.printf("| %-10s | %-40s | %-30s |\n",
                    f.getIdFondo(),
                    f.getNombre(),
                    f.getTipo()
            );
        }
    }
}