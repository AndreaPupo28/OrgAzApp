package com.andrea.orgazapp.orgchart.gui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlBarManager {

    private final MainGUI mainGUI;

    public ControlBarManager(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    public HBox createControlBar() {
        HBox controls = new HBox(10);

        Button addUnitButton = new Button("Aggiungi Unità");
        Button addRoleButton = new Button("Aggiungi Ruolo");
        Button addEmployeeButton = new Button("Aggiungi Dipendente");
        Button deleteUnitButton = new Button("Elimina Unità");
        Button undoButton = new Button("Undo");
        Button redoButton = new Button("Redo");

        // Aggiungi eventi ai pulsanti
        addUnitButton.setOnAction(e -> mainGUI.handleAddUnit());
        addRoleButton.setOnAction(e -> mainGUI.handleAddRole());
        addEmployeeButton.setOnAction(e -> mainGUI.handleAddEmployee());
        deleteUnitButton.setOnAction(e -> mainGUI.handleDeleteUnit());
        undoButton.setOnAction(e -> mainGUI.handleUndo());
        redoButton.setOnAction(e -> mainGUI.handleRedo());

        controls.getChildren().addAll(
                addUnitButton, addRoleButton, addEmployeeButton, deleteUnitButton, undoButton, redoButton
        );

        return controls;
    }
}
