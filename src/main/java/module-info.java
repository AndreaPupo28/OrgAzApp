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

    opens com.andrea.orgazapp to javafx.fxml;
    exports com.andrea.orgazapp;
    exports com.andrea.orgazapp.orgchart.gui;
    opens com.andrea.orgazapp.orgchart.model to javafx.base;

}