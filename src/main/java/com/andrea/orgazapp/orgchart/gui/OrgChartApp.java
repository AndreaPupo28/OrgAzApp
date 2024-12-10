package com.andrea.orgazapp.orgchart.gui;

import com.andrea.orgazapp.orgchart.command.*;
import com.andrea.orgazapp.orgchart.factory.OrgNodeFactory;
import com.andrea.orgazapp.orgchart.model.*;
import com.andrea.orgazapp.orgchart.memento.OrgChartCaretaker;
import com.andrea.orgazapp.orgchart.observer.OrgChartObserver;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class OrgChartApp implements OrgChartObserver {

    private OrgNode root;
    private OrgNode selectedNode;
    private HistoryCommandHandler commandHandler = new HistoryCommandHandler(100);
    private GraphicalTreeManager graphicalTreeManager;
    private TableManager tableManager;
    private Rectangle selectedRectangle;
    private final Map<String, String> roleTypeMap = new HashMap<>();
    private ScrollPane scrollPane; // ScrollPane per il contenitore dell'organigramma
    private Stage primaryStage;
    private Pane graphicalTreePane;
    private final OrgChartCaretaker caretaker;


    public OrgChartApp(Stage primaryStage) {
        root = new Manager("Organo di gestione");
        selectedNode = root;
        commandHandler = new HistoryCommandHandler(100);
        tableManager = new TableManager(this);
        graphicalTreeManager = new GraphicalTreeManager(this, new VBox(), scrollPane);
        caretaker = new OrgChartCaretaker();
        setupUI(primaryStage);
    }

    private void setupUI(Stage primaryStage) {
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

        MenuBarManager menuBarManager = new MenuBarManager(this);
        layout.setTop(menuBarManager.createMenuBar());

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

        String unitType = selectedNode.getType();

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
                if (roleTypeMap.containsKey(roleName)) {
                    String associatedType = roleTypeMap.get(roleName);
                    if (!associatedType.equals(unitType)) {
                        showAlert("Errore", "Il ruolo \"" + roleName + "\" è già associato a un'unità di tipo " + associatedType + ".");
                        return null;
                    }
                } else {
                    roleTypeMap.put(roleName, unitType);
                }
                Role role = new Role(roleName, Set.of(unitType));
                AddRoleCommand command = new AddRoleCommand(selectedNode, role);
                commandHandler.handle(command);

                return null;
            }
            return null;
        });

        dialog.showAndWait();

        graphicalTreeManager.updateGraphicalTreeHighlight();
    }


    protected void handleAddEmployee() {
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

                AddEmployeeCommand command = new AddEmployeeCommand(selectedNode, newEmployee, selectedRole);
                commandHandler.handle(command);
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }



    protected void handleAddUnit() {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi Unità");
        dialog.setHeaderText("Inserisci il nome per la nuova unità:");

        TextField unitNameField = new TextField();
        unitNameField.setPromptText("Nome dell'unità");

        String availableType;
        if (selectedNode instanceof Manager) {
            availableType = "Department";
        } else if (selectedNode instanceof Department) {
            availableType = "Workgroup";
        } else {
            showAlert("Errore", "I gruppi di lavoro non possono avere figli.");
            return;
        }

        Label typeLabel = new Label("Tipo di unità: " + availableType);

        VBox content = new VBox(10, new Label("Nome dell'unità:"), unitNameField, typeLabel);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String name = unitNameField.getText();
                if (name == null || name.trim().isEmpty()) {
                    showAlert("Errore", "Il nome dell'unità non può essere vuoto.");
                    return null;
                }
                return name;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(name -> {
            try {
                OrgNode newUnit = OrgNodeFactory.createNode(availableType, name);
                AddNodeCommand command = new AddNodeCommand(selectedNode, newUnit);
                commandHandler.handle(command);

                graphicalTreeManager.updateGraphicalTree();
                graphicalTreeManager.updateGraphicalTreeHighlight();
            } catch (IllegalArgumentException e) {
                showAlert("Errore", e.getMessage());
            }
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

            selectedNode = null;
            graphicalTreeManager.updateGraphicalTree();
        }
    }



    protected void handleUndo() {
        commandHandler.undo();
        graphicalTreeManager.updateGraphicalTree();
    }

    protected void handleRedo() {
        commandHandler.redo();
        graphicalTreeManager.updateGraphicalTree();
    }

    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void updateTables() {
        if (tableManager == null || selectedNode == null) {
            return;
        }

        tableManager.updateTables(selectedNode);
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

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setSelectedRectangle(Rectangle rectangle) {
        selectedRectangle = rectangle;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public OrgChartCaretaker getCaretaker() {
        return caretaker;
    }

    @Override
    public void onOrgChartUpdated() {
        graphicalTreeManager.updateGraphicalTree();
        updateTables();
    }

    public TableManager getTableManager() {
        return tableManager;
    }
}
