package org.example.paneljavafx.service;

import lombok.Getter;
import org.example.paneljavafx.dao.ClientDAO;
import org.example.paneljavafx.dao.GestorDAO;
import org.example.paneljavafx.dao.UserDAO;
import org.example.paneljavafx.dao.impl.ClientImpl;
import org.example.paneljavafx.dao.impl.GestorImpl;
import org.example.paneljavafx.dao.impl.UserImpl;
import org.example.paneljavafx.model.User;

public class UserService {

    private static UserService instance;

    private final UserDAO userDAO = new UserImpl();

    @Getter
    private User loggedUser;

    private UserService() {}

    public static UserService getInstance() {
        if (instance == null) instance = new UserService();
        return instance;
    }

    public User login(String email, String password) {

        User user = userDAO.findByEmailAndPassword(email, password);
        return user;
    }

    public void logout() {
        MainSessionHolder.getInstance().clear();
        loggedUser = null;
    }

    public User findById(int id) {
        return userDAO.findById(id);
    }
}