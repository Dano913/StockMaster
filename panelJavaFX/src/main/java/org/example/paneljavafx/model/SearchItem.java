package org.example.paneljavafx.model;

public class SearchItem {

    public enum Type {
        FUND,
        ASSET
    }

    private final Type type;
    private final Object data;

    public SearchItem(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public String getTitle() {

        if (data instanceof Fund f) {
            return f.getNombre();
        }

        if (data instanceof Asset a) {
            return a.getName();
        }

        return "";
    }
}