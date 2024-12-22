package com.andrea.orgazapp.orgchart.gui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlBarManager {

    private final OrgChartApp app;

    public ControlBarManager(OrgChartApp app) {
        this.app = app;
    }

    protected HBox createControlBar() {
        HBox controls = new HBox(10);

        Button addUnitButton = new Button("Aggiungi Unità");
        Button addRoleButton = new Button("Aggiungi Ruolo");
        Button addEmployeeButton = new Button("Aggiungi Dipendente");
        Button deleteUnitButton = new Button("Elimina Unità");
        Button undoButton = new Button("Undo");
        Button redoButton = new Button("Redo");

        addUnitButton.setOnAction(e -> app.handleAddUnit());
        addRoleButton.setOnAction(e -> app.handleAddRole());
        addEmployeeButton.setOnAction(e -> app.handleAddEmployee());
        deleteUnitButton.setOnAction(e -> app.handleDeleteUnit());
        undoButton.setOnAction(e -> app.handleUndo());
        redoButton.setOnAction(e -> app.handleRedo());

        controls.getChildren().addAll(
                addUnitButton, addRoleButton, addEmployeeButton, deleteUnitButton, undoButton, redoButton
        );

        return controls;
    }
}
