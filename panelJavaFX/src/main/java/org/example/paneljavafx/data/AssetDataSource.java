package org.example.paneljavafx.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paneljavafx.model.Asset;

import java.io.InputStream;
import java.util.List;

public class AssetDataSource {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Asset> load() {

        try (InputStream is = getClass().getResourceAsStream("/data/assets.json")) {

            if (is == null) {
                throw new RuntimeException("❌ No se encontró /data/assets.json en resources");
            }

            List<Asset> assets = mapper.readValue(is, new TypeReference<List<Asset>>() {});

            return assets;

        } catch (Exception e) {
            throw new RuntimeException("❌ Error cargando assets desde JSON", e);
        }
    }

    // =========================
    // DEBUG
    // =========================
    public void printAssets(List<Asset> assets) {

        System.out.println("\n========================================");
        System.out.println(assets.size() + " ASSETS CARGADOS EN SERVICE");
        System.out.println("====================================================================================");
        System.out.printf("| %-10s | %-40s | %-10s | %-30s | %-20s |\n",
                "TICKER", "NOMBRE", "PRECIO", "SECTOR", "RIESGO");
        System.out.println("====================================================================================");

        for (Asset a : assets) {

            System.out.printf("| %-10s | %-40s | %-10.2f | %-30s | %-20s |\n",
                    a.getTicker(),
                    a.getName(),
                    a.getInitialPrice(),
                    a.getSector(),
                    a.getRisk()
            );
        }
    }
}