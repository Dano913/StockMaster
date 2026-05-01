package org.example.paneljavafx.service;

import org.example.paneljavafx.model.User;

public class MainSessionHolder {

    private static MainSessionHolder instance;
    private User currentUser;

    private MainSessionHolder() {}

    public static MainSessionHolder getInstance() {
        if (instance == null) instance = new MainSessionHolder();
        return instance;
    }

    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser()          { return currentUser; }
    public boolean isLoggedIn()           { return currentUser != null; }

    public void clear() {
        System.out.println("🔒 Sesión cerrada: " +
                (currentUser != null ? currentUser.getEmail() : "-"));
        this.currentUser = null;
    }
}