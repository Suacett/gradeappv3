module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens org.example.demo3 to javafx.fxml;
    exports org.example.demo3;

    opens com.gradeapp.util to javafx.fxml;
    opens com.gradeapp.controller to javafx.fxml;
    opens com.gradeapp.view to javafx.fxml;

    exports com.gradeapp.controller;
    exports com.gradeapp.model;
    exports com.gradeapp.util;
    exports com.gradeapp.view;
}

