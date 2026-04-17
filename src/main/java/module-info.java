module org.example.stockmaster {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.stockmaster to javafx.fxml;
    exports org.example.stockmaster;
}