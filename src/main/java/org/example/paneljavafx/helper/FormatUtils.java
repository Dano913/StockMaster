package org.example.paneljavafx.helper;

public class FormatUtils {

    public static String euro(double value) {
        return String.format("€%.2f", value);
    }

    public static String percent(double value) {
        return String.format("%.2f%%", value);
    }
}