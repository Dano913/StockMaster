module org.example.paneljavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires static lombok;
    requires com.google.gson;
    requires jdk.compiler;

    opens org.example.paneljavafx to javafx.fxml;
    exports org.example.paneljavafx;
    exports org.example.paneljavafx.controller;
    opens org.example.paneljavafx.controller to javafx.fxml;
    opens org.example.paneljavafx.model to com.fasterxml.jackson.databind, com.google.gson;
}