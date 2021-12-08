module main {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.jetbrains.annotations;
    requires org.junit.jupiter.api;

    opens com.example.socialnetworkgui to javafx.fxml;
    opens com.example.socialnetworkgui.controller to javafx.fxml;
    exports com.example.socialnetworkgui;
    exports com.example.socialnetworkgui.controller;
}