package com.andrea.orgazapp.orgchart.gui;

import com.andrea.orgazapp.orgchart.command.*;
import com.andrea.orgazapp.orgchart.model.Employee;
import com.andrea.orgazapp.orgchart.model.Manager;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;
import com.andrea.orgazapp.orgchart.observer.OrgChartObserver;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.andrea.orgazapp.orgchart.factory.OrgNodeFactory;

import java.util.*;

public class MainGUI extends Application implements OrgChartObserver {
    ;
    private OrgNode root;
    private OrgNode selectedNode;
    private Pane graphicalTree;
    private final OrgNodeFactory factory = new OrgNodeFactory();
    private HistoryCommandHandler commandHandler = new HistoryCommandHandler(100);
    private ScrollPane scrollPane;
    private GraphicalTreeManager graphicalTreeManager;
    private Stage primaryStage;
    private Pane graphicalTreePane;
    private Rectangle selectedRectangle; // Rettangolo attualmente selezionato



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new Manager("Nodo 1");
        selectedNode = root;
        graphicalTreePane = new Pane();

        Pane graphicalTree = new Pane();
        scrollPane = new ScrollPane(graphicalTree);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);

        graphicalTreeManager = new GraphicalTreeManager(this, graphicalTree, scrollPane);

        root.addObserver(this);

        BorderPane layout = new BorderPane();
        layout.setCenter(scrollPane);

        ControlBarManager controlBarManager = new ControlBarManager(this);
        layout.setBottom(controlBarManager.createControlBar());

        primaryStage.setScene(new Scene(layout, 800, 600));
        primaryStage.setTitle("Organigramma Aziendale");

        primaryStage.setOnShown(event -> {
            graphicalTreeManager.updateGraphicalTree();
            graphicalTreeManager.updateGraphicalTreeHighlight();
            graphicalTreeManager.centerGraphicalTree();
            ;
        });

        primaryStage.show();

        scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            graphicalTreeManager.updateGraphicalTree();
            graphicalTreeManager.centerGraphicalTree();
        });

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> graphicalTreeManager.centerGraphicalTree());
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> graphicalTreeManager.centerGraphicalTree());

        primaryStage.setOnCloseRequest(event -> root.removeObserver(this));
    }


    protected void handleAddRole() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi Ruolo");

        // Campi di input
        TextField roleNameField = new TextField();
        roleNameField.setPromptText("Nome del ruolo");

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Nome del ruolo:"), roleNameField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String roleName = roleNameField.getText();
                if (roleName == null || roleName.trim().isEmpty()) {
                    showAlert("Errore", "Il nome del ruolo non può essere vuoto.");
                    return null;
                }

                Role role = new Role(roleName, Set.of(selectedNode.getType()));

                // Usa il comando AddRoleCommand per aggiungere il ruolo
                AddRoleCommand command = new AddRoleCommand(selectedNode, role);
                commandHandler.handle(command); // Registra il comando per undo/redo
                return null;
            }
            return null;
        });

        dialog.showAndWait();
        graphicalTreeManager.updateGraphicalTree();
    }


    protected void handleAddEmployee() {
        // Ottieni i ruoli disponibili SOLO per l'unità selezionata
        List<Role> availableRoles = new ArrayList<>(selectedNode.getRolesList());
        if (availableRoles.isEmpty()) {
            showAlert("Errore", "Non ci sono ruoli disponibili per questa unità.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi Dipendente");

        // Campi di input
        TextField employeeNameField = new TextField();
        employeeNameField.setPromptText("Nome del dipendente");

        ComboBox<Role> roleComboBox = new ComboBox<>(FXCollections.observableArrayList(availableRoles));
        roleComboBox.setPromptText("Seleziona un ruolo");

        // Mostra solo i nomi dei ruoli nella tendina
        roleComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Role role, boolean empty) {
                super.updateItem(role, empty);
                setText(empty || role == null ? null : role.getName());
            }
        });
        roleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Role role, boolean empty) {
                super.updateItem(role, empty);
                setText(empty || role == null ? null : role.getName());
            }
        });

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Nome del dipendente:"), employeeNameField,
                new Label("Ruolo:"), roleComboBox
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String employeeName = employeeNameField.getText();
                Role selectedRole = roleComboBox.getValue();

                if (employeeName == null || employeeName.trim().isEmpty() || selectedRole == null) {
                    showAlert("Errore", "Tutti i campi sono obbligatori.");
                    return null;
                }

                Employee newEmployee = new Employee(employeeName);

                // Usa il comando AddEmployeeCommand per aggiungere il dipendente
                AddEmployeeCommand command = new AddEmployeeCommand(selectedNode, newEmployee, selectedRole);
                commandHandler.handle(command); // Registra il comando per undo/redo

                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }



    protected void handleAddUnit() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Aggiungi Unità");
        dialog.setHeaderText(null);
        dialog.setContentText("Nome della nuova unità:");

        dialog.showAndWait().ifPresent(name -> {

            OrgNode newUnit = factory.createNode("Manager", name);
            AddNodeCommand command = new AddNodeCommand(selectedNode, newUnit);
            commandHandler.handle(command);

            selectedNode = newUnit;
            graphicalTreeManager.updateGraphicalTree();
        });
    }

    protected void handleDeleteUnit() {
        if(selectedNode == root){
            showAlert("Errore", "Non è possibile eliminare il nodo radice");
        }

        OrgNode parentNode = root.findNodeParent(selectedNode.getName());
        if (parentNode != null) {
            RemoveNodeCommand command = new RemoveNodeCommand(parentNode, selectedNode);
            commandHandler.handle(command);

            selectedNode = null; // Deseleziona dopo l'eliminazione
            graphicalTreeManager.updateGraphicalTree();
        }
    }



    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void onOrgChartUpdated() {
        graphicalTreeManager.updateGraphicalTree();
    }

    protected void handleUndo() {
        commandHandler.undo();
        graphicalTreeManager.updateGraphicalTree();
    }

    protected void handleRedo() {
        commandHandler.redo();
        graphicalTreeManager.updateGraphicalTree();
    }

    public OrgNode getRoot() {
        return root;
    }

    public void setRoot(OrgNode orgNode) {
        root = orgNode;
    }

    public void setSelectedNode(OrgNode node) {
        selectedNode = node;
    }


    public OrgNode getSelectedNode() {
        return selectedNode;
    }

    public GraphicalTreeManager getGraphicalTreeManager() {
        return graphicalTreeManager;
    }

    public void setSelectedRectangle(Rectangle rectangle) {
        selectedRectangle = rectangle;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }


}

