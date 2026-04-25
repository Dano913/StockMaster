package org.example.paneljavafx.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paneljavafx.model.PriceRecord;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PriceRecordReader {

    private static final ObjectMapper mapper = new ObjectMapper();

    // -------------------------
    // API PÚBLICA
    // Devuelve el último precioCierre conocido para cada idActivo.
    // Si el archivo no existe o está corrupto devuelve un mapa vacío.
    // -------------------------
    public static Map<String, Double> loadLastPrices() {

        File file = new File(resolveDataPath());

        if (!file.exists() || file.length() == 0) {
            System.out.println("📂 PriceRecordReader: archivo vacío o inexistente — se usarán precios iniciales del asset.");
            return Collections.emptyMap();
        }

        try {
            List<PriceRecord> records = mapper.readValue(file, new TypeReference<>() {});

            if (records == null || records.isEmpty()) {
                return Collections.emptyMap();
            }

            // Para cada idActivo nos quedamos con el registro de mayor fecha_creacion
            // (los registros están en orden de inserción, así que el último del grupo ya es el más reciente,
            //  pero agrupamos por si hubiera desordenados)
            Map<String, Double> lastPrices = records.stream()
                    .collect(Collectors.toMap(
                            PriceRecord::getIdActivo,
                            PriceRecord::getPrecioCierre,
                            (existing, replacement) -> replacement  // ante duplicado, el más reciente gana
                    ));

            System.out.println("✅ PriceRecordReader: precios restaurados para " + lastPrices.size() + " activos.");
            lastPrices.forEach((id, price) ->
                    System.out.printf("   › %s → %.2f%n", id, price));

            return lastPrices;

        } catch (Exception e) {
            System.err.println("⚠️ PriceRecordReader: error leyendo precio_asset.json — " + e.getMessage());
            System.err.println("   Se usarán precios iniciales del asset como fallback.");
            return Collections.emptyMap();
        }
    }

    // -------------------------
    // Comodidad — precio único para un asset concreto
    // -------------------------
    public static Optional<Double> loadLastPrice(String assetId) {
        return Optional.ofNullable(loadLastPrices().get(assetId));
    }

    // -------------------------
    // Misma lógica de ruta que PriceRecordWriter para garantizar coherencia
    // -------------------------
    private static String resolveDataPath() {
        try {
            String classesPath = PriceRecordReader.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

            File projectRoot = new File(classesPath)
                    .getParentFile()  // target
                    .getParentFile(); // raíz del proyecto

            return projectRoot + "/src/main/resources/data/precio_asset.json";

        } catch (Exception e) {
            return "src/main/resources/data/precio_asset.json";
        }
    }
}