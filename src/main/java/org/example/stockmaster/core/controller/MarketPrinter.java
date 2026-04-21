package org.example.stockmaster.core.controller;

import java.util.List;

public class MarketPrinter {

    public static void print(List<MarketEngine> engines) {

        System.out.println("\n================= MARKET VIEW =================");

        System.out.printf("%-8s %-12s %-10s\n",
                "TICKER", "PRICE", "CHANGE");

        System.out.println("---------------------------------------------");

        for (MarketEngine engine : engines) {

            String ticker = engine.getAsset().getTicker();

            double price = engine.getLastPrice();

            // fake change visual (puedes mejorar luego)
            double change = (Math.random() - 0.5) * 2;

            System.out.printf("%-8s %-12.2f %+.2f%%\n",
                    ticker,
                    price,
                    change
            );
        }

        System.out.println("=============================================\n");
    }
}