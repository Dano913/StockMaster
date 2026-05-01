package org.example.paneljavafx.dao;

import org.example.paneljavafx.model.User;

public interface UserDAO {

    User findByEmailAndPassword(String email, String password);

    User findById(int id);

    boolean updateStatus(int userId, String status);

    boolean updateRole(int userId, String role);

    boolean updateUser(User user);

    User createUser(User user);
}