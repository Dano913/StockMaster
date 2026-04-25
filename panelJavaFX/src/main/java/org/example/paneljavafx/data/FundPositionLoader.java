package org.example.paneljavafx.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paneljavafx.model.FundPosition;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

public class FundPositionLoader {

    public static List<FundPosition> load() {

        List<FundPosition> positions = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream is = FundPositionLoader.class.getResourceAsStream("/data/fund_positions.json");

            if (is == null) {
                throw new IllegalStateException("❌ No se encontró /data/fund_positions.json");
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

            // -------------------------
            // ORDENAR ASSETS
            // -------------------------
            List<String> assets = new ArrayList<>(matrix.keySet());
            Collections.sort(assets);

            // -------------------------
            // EXTRAER TODOS LOS FONDOS
            // -------------------------
            Set<String> fundsSet = new TreeSet<>();
            for (Map<String, Double> fundMap : matrix.values()) {
                fundsSet.addAll(fundMap.keySet());
            }
            List<String> funds = new ArrayList<>(fundsSet);

            // -------------------------
            // PRINT TABLA
            // -------------------------
//            System.out.println("\n========== ASSET EXPOSURE MATRIX ==========\n");
//
//            // header
//            System.out.print(String.format("%-15s", "ASSET"));
//            for (String f : funds) {
//                System.out.print(String.format("%-15s", f));
//            }
//            System.out.println();
//
//            System.out.println("=".repeat(15 + funds.size() * 15));
//
//            // rows
//            for (String asset : assets) {
//
//                System.out.print(String.format("%-15s", asset));
//
//                Map<String, Double> fundMap = matrix.get(asset);
//
//                for (String fund : funds) {
//
//                    double value = fundMap != null && fundMap.containsKey(fund)
//                            ? fundMap.get(fund)
//                            : 0.0;
//
//                    System.out.print(String.format("%-15.2f", value));
//                }
//
//                System.out.println();
//            }
//
//            System.out.println("\n==========================================\n");

        } catch (Exception e) {
            throw new RuntimeException("❌ Error cargando fund positions", e);
        }

        return positions;
    }
}