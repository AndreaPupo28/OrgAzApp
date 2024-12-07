package com.andrea.orgazapp.orgchart.gui;

import com.andrea.orgazapp.orgchart.command.ModifyNodeNameCommand;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


import java.util.HashMap;
import java.util.Map;

public class GraphicalTreeManager {


    private final MainGUI mainGUI;
    private final Pane graphicalTree;
    private final ScrollPane scrollPane;
    private final Map<OrgNode, Rectangle> nodeToRectangleMap = new HashMap<>();

    private static final double NODE_WIDTH = 200;
    private static final double NODE_HEIGHT = 50;
    private static final double HORIZONTAL_SPACING = 50;
    private static final double VERTICAL_SPACING = 100;

    public GraphicalTreeManager(MainGUI mainGUI, Pane graphicalTree, ScrollPane scrollPane) {
        this.mainGUI = mainGUI;
        this.graphicalTree = graphicalTree;
        this.scrollPane = scrollPane;
    }

    public void updateGraphicalTree() {
        graphicalTree.getChildren().clear();

        double contentWidth = scrollPane.getViewportBounds().getWidth();

        double startX = (contentWidth - NODE_WIDTH) / 2;
        double startY = 20;

        BoundingBox bounds = buildGraphicalTree(mainGUI.getRoot(), startX, startY, graphicalTree);

        double margin = 50; // Extra margin
        graphicalTree.setMinWidth(bounds.getWidth() + margin * 2);
        graphicalTree.setMinHeight(bounds.getHeight() + margin * 2);

        if (bounds.getMinX() < 0) {
            graphicalTree.setTranslateX(-bounds.getMinX() + margin);
        } else {
            graphicalTree.setTranslateX(margin);
        }
        graphicalTree.setTranslateY(margin);
    }

    private BoundingBox buildGraphicalTree(OrgNode node, double x, double y, Pane pane) {
        double minX = x, minY = y, maxX = x + NODE_WIDTH, maxY = y + NODE_HEIGHT;

        Pane subTreePane = buildGraphicalSubTree(node, x, y, pane);
        for (Node child : subTreePane.getChildren()) {
            Bounds childBounds = child.getBoundsInParent();
            minX = Math.min(minX, childBounds.getMinX());
            minY = Math.min(minY, childBounds.getMinY());
            maxX = Math.max(maxX, childBounds.getMaxX());
            maxY = Math.max(maxY, childBounds.getMaxY());
        }

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
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

        ContextMenu contextMenu = new ContextMenu();

        MenuItem showEmployeesItem = new MenuItem("Mostra Dipendenti");
        showEmployeesItem.setOnAction(e -> {
            mainGUI.setSelectedNode(node);
            mainGUI.setSelectedRectangle(rectangle);
            updateGraphicalTreeHighlight();
        });

        MenuItem showRolesItem = new MenuItem("Mostra Ruoli");
        showRolesItem.setOnAction(e -> {
            mainGUI.setSelectedNode(node);
            mainGUI.setSelectedRectangle(rectangle);
            updateGraphicalTreeHighlight();
        });

        MenuItem renameNodeItem = new MenuItem("Modifica Nome");
        renameNodeItem.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(node.getName());
            dialog.setTitle("Modifica Nome Nodo");
            dialog.setHeaderText("Modifica il nome dell'unità");
            dialog.setContentText("Nome:");

            dialog.showAndWait().ifPresent(newName -> {
                if (newName == null || newName.trim().isEmpty()) {
                    mainGUI.showAlert("Errore", "Il nome del nodo non può essere vuoto.");
                } else {
                    // Use the ModifyNodeNameCommand for renaming
                    ModifyNodeNameCommand command = new ModifyNodeNameCommand(node, node.getName(), newName);
                    try {
                        mainGUI.getCommandHandler().handle(command);
                        name.setText(newName); // Update the graphical representation
                        mainGUI.setSelectedNode(node);
                        mainGUI.setSelectedRectangle(rectangle);
                        updateGraphicalTreeHighlight();
                    } catch (Exception ex) {
                        mainGUI.showAlert("Errore", "Impossibile modificare il nome del nodo.");
                    }
                }
            });
        });

        contextMenu.getItems().addAll(showEmployeesItem, showRolesItem, renameNodeItem);

        rectangle.setOnContextMenuRequested(event ->
                contextMenu.show(rectangle, event.getScreenX(), event.getScreenY()));

        rectangle.setOnMouseClicked(event -> {
            mainGUI.setSelectedNode(node);
            mainGUI.setSelectedRectangle(rectangle);
            updateGraphicalTreeHighlight();
        });

        int numberOfChildren = node.children.size();
        if (numberOfChildren > 0) {
            double totalWidth = numberOfChildren * (NODE_WIDTH + HORIZONTAL_SPACING) - HORIZONTAL_SPACING;
            double childStartX = x - totalWidth / 2 + NODE_WIDTH / 2;

            for (OrgNode child : node.children) {
                Line line = new Line(
                        x + NODE_WIDTH / 2, y + NODE_HEIGHT,
                        childStartX + NODE_WIDTH / 2, y + NODE_HEIGHT + VERTICAL_SPACING
                );
                pane.getChildren().add(line);
                buildGraphicalSubTree(child, childStartX, y + NODE_HEIGHT + VERTICAL_SPACING, pane);
                childStartX += NODE_WIDTH + HORIZONTAL_SPACING;
            }
        }

        if (mainGUI.getSelectedNode() == node) {
            mainGUI.setSelectedRectangle(rectangle);
            updateGraphicalTreeHighlight();
        }

        return pane;
    }

    protected void updateGraphicalTreeHighlight() {
        for (Map.Entry<OrgNode, Rectangle> entry : nodeToRectangleMap.entrySet()) {
            Rectangle rect = entry.getValue();
            if (entry.getKey().equals(mainGUI.getSelectedNode())) {
                rect.setFill(Color.LIGHTGREEN);
            } else {
                rect.setFill(Color.LIGHTBLUE);
            }
        }
    }

    protected void centerGraphicalTree() {
        if (graphicalTree == null || graphicalTree.getChildren().isEmpty()) {
            return; // Non centra se il contenitore grafico è vuoto o non inizializzato
        }

        double contentWidth = graphicalTree.getBoundsInLocal().getWidth();
        double contentHeight = graphicalTree.getBoundsInLocal().getHeight();
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();

        // Calcolo centratura orizzontale e verticale
        double horizontalOffset = (contentWidth > viewportWidth) ? 0.5 : 0.0;
        double verticalOffset = (contentHeight > viewportHeight) ? 0.5 : 0.0;

        // Applica i valori di centratura
        scrollPane.setHvalue(horizontalOffset);
        scrollPane.setVvalue(verticalOffset);
    }

}
