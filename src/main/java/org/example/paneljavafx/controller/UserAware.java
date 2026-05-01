package org.example.paneljavafx.controller;

import org.example.paneljavafx.model.User;

public interface UserAware {
    void setUser(User user);
    default void setSelectedTarget(User target) {}

    void setMainController(MainController mc);
}