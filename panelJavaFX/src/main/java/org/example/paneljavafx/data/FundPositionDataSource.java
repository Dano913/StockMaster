package org.example.paneljavafx.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paneljavafx.model.FundPosition;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

public class FundPositionDataSource {

    public static List<FundPosition> load() {

        List<FundPosition> positions = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream is = FundPositionDataSource.class.getResourceAsStream("/data/fund_positions.json");

            if (is == null) {
                throw new IllegalStateException("No se encontró /data/fund_positions.json");
            }

            JsonNode root = mapper.readTree(is);

            // -------------------------
            // asset -> (fund -> weight)
            // -------------------------
            Map<String, Map<String, Double>> matrix = new HashMap<>();

            for (JsonNode node : root) {

                String fundId = node.get("id_fondo").asText();
                String assetId = node.get("id_activo").asText();

                double weight = node.get("peso_porcentual").asDouble();

                matrix
                        .computeIfAbsent(assetId, k -> new HashMap<>())
                        .put(fundId, weight);

                FundPosition fp = new FundPosition();

                fp.setIdFundPosition(node.get("id_cartera_fondo").asText());
                fp.setIdFund(fundId);
                fp.setIdAsset(assetId);

                fp.setPesoPorcentual(weight);
                fp.setInvestedValue(node.get("valor_invertido").asDouble());
                fp.setQuantity(node.get("cantidad").asDouble());

                fp.setCurrency(node.get("moneda").asText());
                fp.setAddedRisk(node.get("riesgo_aportado").asText());

                fp.setStartDate(LocalDate.parse(node.get("fecha_inicio").asText()));

                JsonNode end = node.get("fecha_fin");
                if (end != null && !end.isNull() && !end.asText().isEmpty()) {
                    fp.setFinishDate(LocalDate.parse(end.asText()));
                }

                positions.add(fp);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando fund positions", e);
        }

        return positions;
    }

    public static void printPositions(List<FundPosition> positions) {

        System.out.println("\n====================================================================================");
        System.out.println(positions.size() + " POSICIONES CARGADAS EN SERVICE");
        System.out.println("====================================================================================\n");
        System.out.printf("| %-12s | %-8s | %-8s | %-10s | %-12s | %-10s |\n",
                "ID_POS", "FUND", "ASSET", "INVESTED", "WEIGHT", "QTY");
        System.out.println("====================================================================================");

        for (FundPosition p : positions) {

            System.out.printf("| %-12s | %-8s | %-8s | %-10.2f | %-12.2f | %-10.2f |\n",
                    p.getIdFundPosition(),
                    p.getIdFund(),
                    p.getIdAsset(),
                    p.getInvestedValue(),
                    p.getPesoPorcentual(),
                    p.getQuantity()
            );
        }
    }
}