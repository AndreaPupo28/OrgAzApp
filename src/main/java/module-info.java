module com.andrea.orgazapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires java.management;

    exports com.andrea.orgazapp.orgchart.model to com.fasterxml.jackson.databind;
    opens com.andrea.orgazapp.orgchart.model to com.fasterxml.jackson.databind, javafx.base;
    exports com.andrea.orgazapp.orgchart.gui;

}