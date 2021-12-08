module com.example.socialnetworkgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.jetbrains.annotations;
    requires org.junit.jupiter.api;

    opens com.example.socialnetworkgui to javafx.fxml;
    exports com.example.socialnetworkgui;
}