module org.example.paneljavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires static lombok;
    requires com.google.gson;
    requires jdk.compiler;
    requires java.sql;

    // ── Abiertos para reflexión ──────────────────────────────────────────
    opens org.example.paneljavafx to javafx.fxml;
    opens org.example.paneljavafx.controller to javafx.fxml;
    opens org.example.paneljavafx.model to com.fasterxml.jackson.databind, com.google.gson;

    // ── Exportados para uso entre paquetes ───────────────────────────────
    exports org.example.paneljavafx;
    exports org.example.paneljavafx.model;
    exports org.example.paneljavafx.controller;
    exports org.example.paneljavafx.service;
    exports org.example.paneljavafx.service.dto;
    exports org.example.paneljavafx.dao;
    exports org.example.paneljavafx.dao.impl;
    exports org.example.paneljavafx.database;
    exports org.example.paneljavafx.simulation;
    exports org.example.paneljavafx.common;
}