module org.example.paneljavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.paneljavafx to javafx.fxml;
    exports org.example.paneljavafx;
}