package com.andrea.orgazapp.orgchart;

import com.andrea.orgazapp.orgchart.command.*;
import com.andrea.orgazapp.orgchart.model.Employee;
import com.andrea.orgazapp.orgchart.model.Manager;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;
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

public class MainGUI extends Application {
    ;
    private OrgNode root;
    private OrgNode selectedNode;
    private Pane graphicalTree;
    OrgNodeFactory factory;
    private HistoryCommandHandler commandHandler = new HistoryCommandHandler(100);
    private ScrollPane scrollPane;
    private Map<OrgNode, Rectangle> nodeToRectangleMap = new HashMap<>();
    private static final double NODE_HEIGHT = 50;
    private static final double NODE_WIDTH = 150;
    private static final double HORIZONTAL_SPACING = 50;
    private static final double VERTICAL_SPACING = 100;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new Manager("Nodo 1");
        selectedNode = root;
        graphicalTree = new Pane();
        factory = new OrgNodeFactory();

        scrollPane = new ScrollPane(graphicalTree);

        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);

        updateGraphicalTree();

        BorderPane layout = new BorderPane();
        layout.setCenter(scrollPane);

        HBox controls = new HBox(10);
        Button addUnitButton = new Button("Aggiungi Unità");
        Button deleteUnitButton = new Button("Elimina Unità");
        Button addRoleButton = new Button("Aggiungi Ruolo");
        Button addEmployeeButton = new Button("Aggiungi Dipendente");

        controls.getChildren().addAll(
                addUnitButton, deleteUnitButton, addRoleButton, addEmployeeButton
        );
        layout.setBottom(controls);

        // Eventi pulsanti
        addUnitButton.setOnAction(e -> handleAddUnit());
        deleteUnitButton.setOnAction(e -> handleDeleteUnit());
        addRoleButton.setOnAction(e -> handleAddRole());
        addEmployeeButton.setOnAction(e -> handleAddEmployee());

        primaryStage.setScene(new Scene(layout, 800, 600));
        primaryStage.setTitle("Organigramma Aziendale");
        
        primaryStage.setOnShown(event -> {
            updateGraphicalTree();
            centerGraphicalTree();
        });

        primaryStage.show();
    }

    private void handleAddRole() {
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
        updateGraphicalTree();
    }


    private void handleAddEmployee() {
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


    private void updateGraphicalTree() {
        graphicalTree.getChildren().clear();

        double contentWidth = scrollPane.getViewportBounds().getWidth();
        double contentHeight = scrollPane.getViewportBounds().getHeight();

        double startX = (contentWidth - NODE_WIDTH) / 2;
        double startY = Math.max((contentHeight - NODE_HEIGHT) / 2, 20);

        buildGraphicalTree(root, startX, startY, graphicalTree);
    }


    private void centerGraphicalTree() {
        if (graphicalTree.getChildren().isEmpty()) {
            return;
        }

        double contentWidth = graphicalTree.getBoundsInLocal().getWidth();
        double contentHeight = graphicalTree.getBoundsInLocal().getHeight();
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();

        if (contentWidth > viewportWidth) {
            scrollPane.setHvalue(0.5); // Centra orizzontalmente
        }

        if (contentHeight > viewportHeight) {
            scrollPane.setVvalue(0.5); // Centra verticalmente
        }
    }


    private Pane buildGraphicalTree(OrgNode node, double x, double y, Pane pane) {
        double startX = scrollPane.getWidth() / 2 - NODE_WIDTH / 2;
        double startY = 50;
        return buildGraphicalSubTree(node, startX, startY, pane);
    }

    private Pane buildGraphicalSubTree(OrgNode node, double x, double y, Pane pane) {
        Rectangle rectangle = new Rectangle(x, y, NODE_WIDTH, NODE_HEIGHT);
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);
        rectangle.setFill(Color.LIGHTBLUE);
        rectangle.setStroke(Color.BLACK);

        nodeToRectangleMap.put(node, rectangle);

        Text name = new Text(node.getName());
        name.setX(x + 10);
        name.setY(y + 25);
        name.setFont(Font.font("Arial", 14));

        pane.getChildren().addAll(rectangle, name);

        int numberOfChildren = node.children.size();
        if (numberOfChildren > 0) {
            double totalWidth = numberOfChildren * (NODE_WIDTH + HORIZONTAL_SPACING) - HORIZONTAL_SPACING;
            double childStartX = x - totalWidth / 2 + NODE_WIDTH / 2;

            for (OrgNode child : node.children) {
                Line line = new Line(x + NODE_WIDTH / 2, y + NODE_HEIGHT, childStartX + NODE_WIDTH / 2, y + NODE_HEIGHT + VERTICAL_SPACING);
                pane.getChildren().add(line);
                buildGraphicalSubTree(child, childStartX, y + NODE_HEIGHT + VERTICAL_SPACING, pane);
                childStartX += NODE_WIDTH + HORIZONTAL_SPACING;
            }
        }

        return pane;
    }

    private void handleAddUnit() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Aggiungi Unità");
        dialog.setHeaderText(null);
        dialog.setContentText("Nome della nuova unità:");

        dialog.showAndWait().ifPresent(name -> {

            OrgNode newUnit = factory.createNode("Manager", name);
            AddNodeCommand command = new AddNodeCommand(selectedNode, newUnit);
            commandHandler.handle(command);

            selectedNode = newUnit;
            updateGraphicalTree();
        });
    }

    private void handleDeleteUnit() {
        if(selectedNode == root){
            showAlert("Errore", "Non è possibile eliminare il nodo radice");
        }

        OrgNode parentNode = root.findNodeParent(selectedNode.getName());
        if (parentNode != null) {
            RemoveNodeCommand command = new RemoveNodeCommand(parentNode, selectedNode);
            commandHandler.handle(command);

            selectedNode = null; // Deseleziona dopo l'eliminazione
            updateGraphicalTree();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

