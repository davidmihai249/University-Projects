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

    exports com.example.socialnetworkgui;
    exports com.example.socialnetworkgui.domain;
    opens com.example.socialnetworkgui.controller;
    exports com.example.socialnetworkgui.controller;
    opens com.example.socialnetworkgui;
    exports com.example.socialnetworkgui.service;
    exports com.example.socialnetworkgui.utils.events;
    exports com.example.socialnetworkgui.utils.observer;
    exports com.example.socialnetworkgui.repository;
}