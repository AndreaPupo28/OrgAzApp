package com.andrea.orgazapp.orgchart.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainGUI extends Application {


    @Override
    public void start(Stage primaryStage) {
        new OrgChartApp(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}

