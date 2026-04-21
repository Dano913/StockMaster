package org.example.stockmaster.javafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.example.stockmaster.javafx.model.User;

import java.util.List;

public class UserService {

    // =========================
    // SINGLETON
    // =========================
    private static final UserService INSTANCE = new UserService();

    public static UserService getInstance() {
        return INSTANCE;
    }

    // =========================
    // DATA
    // =========================
    @Getter
    private final ObservableList<User> users =
            FXCollections.observableArrayList();

    private final StorageService storageService = new StorageService();

    // =========================
    // INIT
    // =========================
    private UserService() {

        List<User> loadedUsers = storageService.load();

        if (loadedUsers != null && !loadedUsers.isEmpty()) {

            users.setAll(loadedUsers);

        } else {

            System.out.println("No JSON found, creating seed data");

            seedData();
            save();
        }
    }

    // =========================
    // ACCESS
    // =========================
    public ObservableList<User> getAllUsers() {
        return users;
    }

    // =========================
    // CRUD
    // =========================
    public void addUser(User user) {
        users.add(user);
        save();
    }

    public boolean existsEmail(String email) {
        return users.stream()
                .anyMatch(u -> u.getEmail() != null
                        && u.getEmail().equalsIgnoreCase(email));
    }

    // =========================
    // AUTH
    // =========================
    public User login(String email, String password) {

        return users.stream()
                .filter(u ->
                        u.getEmail() != null &&
                                u.getPassword() != null &&
                                u.getEmail().equalsIgnoreCase(email) &&
                                u.getPassword().equals(password)
                )
                .findFirst()
                .orElse(null);
    }

    // =========================
    // SEED
    // =========================
    private void seedData() {

        User admin = new User();
        admin.setId("1");
        admin.setName("Jorge");
        admin.setEmail("admin@gmail.com");
        admin.setPassword("password1");
        admin.setRole("admin");

        User user = new User();
        user.setId("2");
        user.setName("Maria");
        user.setEmail("maria@gmail.com");
        user.setPassword("password2");
        user.setRole("user");

        users.addAll(admin, user);
    }

    // =========================
    // SAVE
    // =========================
    public void save() {
        storageService.save(users);
        System.out.println("💾 Users saved");
    }

    public void printUsers() {

        System.out.println("\n================ USERS ================");

        users.forEach(u -> {
            System.out.println("ID: " + u.getId());
            System.out.println("NAME: " + u.getName());
            System.out.println("EMAIL: " + u.getEmail());
            System.out.println("ROLE: " + u.getRole());
            System.out.println("----------------------------");
        });

        System.out.println("TOTAL USERS: " + users.size());
        System.out.println("======================================\n");
    }
}