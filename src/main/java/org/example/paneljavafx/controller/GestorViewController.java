package org.example.paneljavafx.controller;

import org.example.paneljavafx.model.User;

public class GestorViewController implements UserAware {

    @Override
    public void setUser(User user) {

        if (user == null) return;

        initData();
    }

    private void initData() {}

    @Override
    public void setMainController(MainController mainController) {}
}