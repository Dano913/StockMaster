module org.example.stockmaster {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.logging;
    requires com.google.gson;
    requires java.desktop;
    requires javafx.graphics;


    opens org.example.stockmaster to javafx.fxml;
    exports org.example.stockmaster.javafx;
    exports org.example.stockmaster.javafx.controller;
    opens org.example.stockmaster.javafx.controller to javafx.fxml;
    opens org.example.stockmaster.core.model to com.google.gson;
}