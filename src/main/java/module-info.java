module com.example.nemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires zip4j;

    opens com.example.nemo to javafx.fxml;
    exports com.example.nemo;
}