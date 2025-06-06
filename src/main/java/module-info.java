module com.example.nostalgiaapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires opencv;

    opens com.example.nostalgiaapp to javafx.fxml;
    exports com.example.nostalgiaapp;
}