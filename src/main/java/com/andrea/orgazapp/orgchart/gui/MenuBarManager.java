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

        Menu infoMenu = new Menu("Info");
        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setOnAction(e -> showInfoDialog());
        infoMenu.getItems().add(aboutMenuItem);
        menuBar.getMenus().addAll(fileMenu, infoMenu);
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

    private void showInfoDialog() {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Informazioni");
        infoAlert.setHeaderText("Informazioni sull'applicazione");
        infoAlert.setContentText(
                "Questa è un'applicazione per gestire organigrammi aziendali." + "\n" +
                        "Funzionalità principali includono:" + "\n" +
                        "- Aggiungere nuove unità, ruoli o dipendenti." + "\n" +
                        "- Modificare e rimuovere unità, ruoli e dipendenti." + "\n" +
                        "- Salvare e caricare organigrammi tramite il menu File." + "\n" +
                        "Il nodo selezionato è indicato dal colore verde, se vuoi selezionarne un altro basta cliccarci sopra." + "\n" +
                        "Puoi inoltre interagire con i nodi cliccando con il tasto destro:" + "\n"+
                        "- Appariranno tre funzionalità: modifica nome del nodo," + "\n" + "visualizzazione dei ruoli o dei dipendenti dell'unità." + "\n"+
                        "- Tramite il tasto Mostra ruoli o Mostra dipendenti potrai gestirli, eliminandoli o modificandoli."
        );
        infoAlert.showAndWait();
    }

}