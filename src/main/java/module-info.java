module com.example.nostalgiaapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires opencv;
    requires java.desktop;
    requires java.logging;

    opens com.example.nostalgiaapp to javafx.fxml;
    exports com.example.nostalgiaapp;
}