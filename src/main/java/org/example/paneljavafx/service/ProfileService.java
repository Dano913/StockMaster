package org.example.paneljavafx.service;

import org.example.paneljavafx.model.User;

public class ProfileService {

    public User getProfileData(User user) {

        switch (user.getRole().toLowerCase()) {
            case "admin":
                return getAdminData(user);
            case "gestor":
                return getGestorData(user);
            case "cliente":
                return getClienteData(user);
            default:
                return user;
        }
    }

    private User getAdminData(User user) {
        return user;
    }

    private User getGestorData(User user) {
        return user;
    }

    private User getClienteData(User user) {
        return user;
    }
}