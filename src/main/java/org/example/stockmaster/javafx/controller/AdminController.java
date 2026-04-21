package org.example.stockmaster.javafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import lombok.Setter;
import org.example.stockmaster.javafx.model.TimeRecord;
import org.example.stockmaster.javafx.model.User;
import org.example.stockmaster.javafx.service.TimeRecordService;
import org.example.stockmaster.javafx.service.UserService;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class AdminController {

    // =========================
    // CONSTANTS
    // =========================
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =========================
    // SERVICES (DEPENDENCIES)
    // =========================
    private final UserService userService = UserService.getInstance();
    private final TimeRecordService timeRecordService = TimeRecordService.getInstance();

    // =========================
    // STATE
    // =========================
    @Setter private User user;
    @Setter private MainController mainController;

    @FXML private VBox profileContainer;
    private ProfileController profileViewController;
    private TimeRecordPanelController timeRecordPanelController;

    private javafx.collections.transformation.FilteredList<User> filteredUsers;
    private javafx.collections.transformation.FilteredList<TimeRecord> filteredRecords;
    private javafx.collections.ObservableList<TimeRecord> globalRecords;

    // =========================
    // UI (FXML)
    // =========================
    @FXML private VBox timeRecordContainer;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField ageField;
    @FXML private TextField genderField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private TextField dniField;
    @FXML private ComboBox<String> roleBox;

    @FXML private Label registerStatus;
    @FXML private TabPane tabPane;

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;

    @FXML private TableView<TimeRecord> timeRecordsTable;
    @FXML private TableColumn<TimeRecord, String> colUser;
    @FXML private TableColumn<TimeRecord, String> colDateTime;
    @FXML private TableColumn<TimeRecord, String> colType;
    @FXML private TableColumn<User, String> colAge;
    @FXML private TableColumn<User, String> colPhone;
    @FXML private TableColumn<User, String> colDni;

    @FXML private TextField searchField;

    // =========================
    // INITIALIZE
    // =========================
    @FXML
    public void initialize() {
        loadTimeRecordPanel();
        setupUsersTable();
        setupTimeRecordsTable();
        setupSearch();
    }

    // =========================
    // PUBLIC METHODS
    // =========================
    public void setUser(User user) {
        this.user = user;
        System.out.println("🧑 Admin logged: " + user.getName());
        profileViewController.setUser(user);
        loadPanel();
    }

    // =========================
    // PRIVATE - UI SETUP
    // =========================
    private void loadTimeRecordPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/stockmaster/time-record-view.fxml")
            );

            Parent panel = loader.load();
            timeRecordPanelController = loader.getController();

            timeRecordContainer.getChildren().setAll(panel);

            configureEvents();

            FXMLLoader loaderprofile = new FXMLLoader(
                    getClass().getResource("/org/example/stockmaster/profile-user-view.fxml")
            );

            Parent profile = loaderprofile.load();
            profileViewController = loaderprofile.getController();

            profileContainer.getChildren().setAll(profile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupUsersTable() {

        usersTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User selected = usersTable.getSelectionModel().getSelectedItem();
                if (selected == null) return;

                TabPane tabPane = (TabPane) usersTable.getScene().lookup(".tab-pane");
                navigateToUser(selected, tabPane);
            }
        });

        usersTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-cursor: hand;");
                }
            });
            row.setOnMouseExited(e -> row.setStyle("-fx-cursor: default;"));
            return row;
        });

        roleBox.setItems(FXCollections.observableArrayList("admin", "user"));
        roleBox.setValue("user");

        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getName()));

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail()));

        colAge.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getAge())));

        colPhone.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPhone()));

        colDni.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDni()));

        filteredUsers = new javafx.collections.transformation.FilteredList<>(userService.getAllUsers());
        usersTable.setItems(filteredUsers);

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected == null) {
                filteredRecords.setPredicate(null);
            } else {
                filteredRecords.setPredicate(r -> r.getUser().equals(selected.getName()));
            }
        });
    }

    private void setupTimeRecordsTable() {
        colUser.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getUser()));

        colDateTime.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDateTime().format(formatter)));

        colType.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getType()));

        refreshGlobalTable();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            filteredUsers.setPredicate(u ->
                    newVal == null || newVal.isEmpty()
                            || u.getName().toLowerCase().contains(newVal.toLowerCase())
                            || u.getEmail().toLowerCase().contains(newVal.toLowerCase())
            );

            if (newVal == null || newVal.isEmpty()) {
                filteredRecords.setPredicate(null);
                usersTable.getSelectionModel().clearSelection();
            } else {
                filteredRecords.setPredicate(r ->
                        r.getUser().toLowerCase().contains(newVal.toLowerCase())
                );
            }
        });
    }

    // =========================
    // PRIVATE - PANEL LOGIC
    // =========================
    private void loadPanel() {
        if (timeRecordPanelController == null) return;
        refreshPanel();
    }

    private void refreshPanel() {
        if (user == null) return;
        var records = FXCollections.observableArrayList(user.getTimeRecords());
        timeRecordPanelController.bindTimeRecords(records);
    }

    // =========================
    // PRIVATE - DATA
    // =========================
    private void refreshGlobalTable() {
        var sorted = userService.getAllUsers().stream()
                .flatMap(u -> u.getTimeRecords().stream())
                .sorted(Comparator.comparing(TimeRecord::getDateTime).reversed())
                .toList();

        if (globalRecords == null) {
            globalRecords = FXCollections.observableArrayList(sorted);
            filteredRecords = new javafx.collections.transformation.FilteredList<>(globalRecords);
            timeRecordsTable.setItems(filteredRecords);
        } else {
            globalRecords.setAll(sorted);
        }
    }

    // =========================
    // PRIVATE - EVENTS
    // =========================
    private void configureEvents() {

        timeRecordPanelController.setOnCheckIn(() -> {
            if (user == null) return;

            timeRecordService.checkIn(user);

            refreshPanel();
            refreshGlobalTable();

            Platform.runLater(() -> timeRecordPanelController.scrollToTop());

            timeRecordPanelController.showMessage("Check-in registrado, comienza tu jornada!");
        });

        timeRecordPanelController.setOnCheckOut(() -> {
            if (user == null) return;

            timeRecordService.checkOut(user);

            refreshPanel();
            refreshGlobalTable();

            Platform.runLater(() -> timeRecordPanelController.scrollToTop());

            timeRecordPanelController.showMessage("Check-out registrado, hasta el próximo día!");
        });
    }

    // =========================
    // UI HANDLERS
    // =========================
    @FXML
    private void handleRegisterUser() {

        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleBox.getValue();
        String age = ageField.getText();
        String gender = genderField.getText();
        String phone = phoneField.getText();
        String dni = dniField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            registerStatus.setText("❌ Missing fields");
            return;
        }

        if (userService.existsEmail(email)) {
            registerStatus.setText("❌ Email exists");
            return;
        }

        User newUser = new User();
        newUser.setId(String.valueOf(System.currentTimeMillis()));
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(role);
        newUser.setProfile(role);
        newUser.setAge(age);
        newUser.setGender(gender);
        newUser.setPhone(phone);
        newUser.setDni(dni);

        userService.addUser(newUser);

        registerStatus.setText("✔ User created");

        nameField.clear();
        emailField.clear();
        passwordField.clear();
        ageField.clear();
        genderField.clear();
        phoneField.clear();
        dniField.clear();
        passwordField.clear();
        roleBox.setValue("user");

        filteredUsers = new javafx.collections.transformation.FilteredList<>(userService.getAllUsers());
        usersTable.setItems(filteredUsers);
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        usersTable.getSelectionModel().clearSelection();
        filteredUsers.setPredicate(null);
        filteredRecords.setPredicate(null);
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/stockmaster/login-view.fxml"
            ));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setMainController(mainController);
            mainController.setContent(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToUser(User selected, TabPane tabPane) {
        try {
            for (Tab tab : tabPane.getTabs()) {
                if (tab.getText().equals("👤 " + selected.getName())) {
                    tabPane.getSelectionModel().select(tab);
                    return;
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/loginpaneladmin/user-view.fxml"
            ));
            Parent root = loader.load();
            UserController userController = loader.getController();
            userController.setUser(selected);
            userController.setMainController(mainController);

            Tab userTab = new Tab("👤 " + selected.getName());
            userTab.setContent(root);
            userTab.setClosable(true);

            tabPane.getTabs().add(userTab);
            tabPane.getSelectionModel().select(userTab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}