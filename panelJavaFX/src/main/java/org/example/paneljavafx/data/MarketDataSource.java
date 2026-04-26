package org.example.paneljavafx.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarketDataSource {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Asset> loadAssets() {
        try {
            InputStream is = getClass().getResourceAsStream("/data/assets.json");

            if (is == null) {
                throw new RuntimeException("assets.json not found");
            }

            return mapper.readValue(is, new TypeReference<List<Asset>>() {});

        } catch (Exception e) {
            throw new RuntimeException("Error loading assets", e);
        }
    }

    public List<Fund> loadFunds() {
        try {
            InputStream is = getClass().getResourceAsStream("/data/funds.json");

            if (is == null) {
                throw new RuntimeException("funds.json not found");
            }

            return mapper.readValue(is, new TypeReference<List<Fund>>() {});

        } catch (Exception e) {
            throw new RuntimeException("Error loading funds", e);
        }
    }

    /**
     * Si quieres persistir precios iniciales o last state
     * lo puedes conectar aquí en el futuro.
     */
    public Map<String, Double> loadLastPrices() {
        return PriceRecordReader.loadLastPrices();
    }
}