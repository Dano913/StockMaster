package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.data.GestorDataSource;
import org.example.paneljavafx.model.Gestor;

public class GestorService {

    private final ObservableList<Gestor> gestores = FXCollections.observableArrayList();
    private final GestorDataSource dataSource = new GestorDataSource();

    public ObservableList<Gestor> getAll() {
        return gestores;
    }

    public void load() {
        gestores.setAll(dataSource.load());
    }

    public void add(Gestor g) {
        if (g == null) return;
        if (existsEmail(g.getEmail())) return;

        gestores.add(g);
    }

    public boolean existsEmail(String email) {
        return gestores.stream()
                .anyMatch(g -> g.getEmail().equalsIgnoreCase(email));
    }

    public void remove(Gestor g) {
        gestores.remove(g);
    }

    public void update(Gestor updated) {
        for (int i = 0; i < gestores.size(); i++) {
            if (gestores.get(i).getIdGestor() == updated.getIdGestor()) {
                gestores.set(i, updated);
                return;
            }
        }
    }
}