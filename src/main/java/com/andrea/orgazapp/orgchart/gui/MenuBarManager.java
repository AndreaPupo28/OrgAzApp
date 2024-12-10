package com.andrea.orgazapp.orgchart.gui;


import com.andrea.orgazapp.orgchart.model.OrgNode;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;

public class MenuBarManager {

    private final OrgChartApp app;

    public MenuBarManager(OrgChartApp app) {
        this.app = app;
    }

    public MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem saveMenuItem = new MenuItem("Salva");
        MenuItem loadMenuItem = new MenuItem("Carica");
        fileMenu.getItems().addAll(saveMenuItem, loadMenuItem);

        saveMenuItem.setOnAction(e -> handleSave());
        loadMenuItem.setOnAction(e -> handleLoad());

        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }

    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva Organigramma");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON", "*.json"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                app.getCaretaker().saveToFile(app.getRoot(), file.getAbsolutePath());
                app.showAlert("Successo", "Organigramma salvato correttamente.");
            } catch (Exception e) {
                app.showAlert("Errore", "Errore durante il salvataggio: " + e.getMessage());
            }
        }
    }


    private void handleLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Carica Organigramma");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON", "*.json"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                app.setRoot(app.getCaretaker().loadFromFile(file.getAbsolutePath(), OrgNode.class));
                app.getGraphicalTreeManager().updateGraphicalTree();
            } catch (Exception e) {
                app.showAlert("Errore", "Errore durante il caricamento: " + e.getMessage());
            }
        }
    }
}