package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.ClientDAO;
import org.example.paneljavafx.dao.GestorDAO;
import org.example.paneljavafx.dao.UserDAO;
import org.example.paneljavafx.dao.impl.ClientImpl;
import org.example.paneljavafx.dao.impl.GestorImpl;
import org.example.paneljavafx.dao.impl.UserImpl;
import org.example.paneljavafx.model.User;

import java.util.List;

public class UserService {

    private static UserService instance;

    private final UserDAO userDAO   = new UserImpl();
    private final GestorDAO gestorDAO = new GestorImpl();
    private final ClientDAO clientDAO = new ClientImpl();

    private UserService() {}

    public static UserService getInstance() {
        if (instance == null) instance = new UserService();
        return instance;
    }

    public User login(String email, String password) {
        return userDAO.findByEmailAndPassword(email, password);
    }

    public void logout() {
        MainSessionHolder.getInstance().clear();
    }

    public User findById(int id) {
        return userDAO.findById(id);
    }

    public boolean cambiarEstado(int userId, String estado) {
        return userDAO.updateStatus(userId, estado);
    }

    public boolean cambiarRol(int userId, String rol) {
        return userDAO.updateRole(userId, rol);
    }

    public User crearUsuario(User user) {
        return userDAO.createUser(user);
    }

    public enum PasswordResult {
        OK,
        ERROR_VACIA,
        ERROR_NO_COINCIDE,
        ERROR_DEMASIADO_CORTA,
        ERROR_ACTUAL_INCORRECTA;

        public String getMensaje() {
            return switch (this) {
                case OK                      -> "Contraseña actualizada correctamente.";
                case ERROR_VACIA             -> "La nueva contraseña no puede estar vacía.";
                case ERROR_NO_COINCIDE       -> "Las contraseñas no coinciden.";
                case ERROR_DEMASIADO_CORTA   -> "Mínimo 6 caracteres.";
                case ERROR_ACTUAL_INCORRECTA -> "La contraseña actual es incorrecta.";
            };
        }
    }
}