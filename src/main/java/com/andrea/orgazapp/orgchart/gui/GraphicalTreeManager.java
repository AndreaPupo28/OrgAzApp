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

    private final OrgChartApp app;
    private final Pane graphicalTree;
    private final ScrollPane scrollPane;
    private final Map<OrgNode, Rectangle> nodeToRectangleMap = new HashMap<>();

    private static final double NODE_WIDTH = 200;
    private static final double NODE_HEIGHT = 50;
    private static final double HORIZONTAL_SPACING = 50;
    private static final double VERTICAL_SPACING = 100;

    public GraphicalTreeManager(OrgChartApp app, Pane graphicalTree, ScrollPane scrollPane) {
        this.app = app;
        this.graphicalTree = graphicalTree;
        this.scrollPane = scrollPane;
    }

    protected void updateGraphicalTree() {
        graphicalTree.getChildren().clear();

        double contentWidth = scrollPane.getViewportBounds().getWidth();

        double startX = (contentWidth - NODE_WIDTH) / 2;
        double startY = 20;

        BoundingBox bounds = buildGraphicalTree(app.getRoot(), startX, startY, graphicalTree);

        double margin = 50;
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
            app.setSelectedNode(node);
            app.setSelectedRectangle(rectangle);
            updateGraphicalTreeHighlight();
            app.getTableManager().showEmployeeTable();
        });

        MenuItem showRolesItem = new MenuItem("Mostra Ruoli");
        showRolesItem.setOnAction(e -> {
            app.setSelectedNode(node);
            app.setSelectedRectangle(rectangle);
            updateGraphicalTreeHighlight();
            app.getTableManager().showRoleTable();
        });

        MenuItem renameNodeItem = new MenuItem("Modifica Nome");
        renameNodeItem.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(node.getName());
            dialog.setTitle("Modifica Nome Nodo");
            dialog.setHeaderText("Modifica il nome dell'unità");
            dialog.setContentText("Nome:");

            dialog.showAndWait().ifPresent(newName -> {
                if (newName == null || newName.trim().isEmpty()) {
                    app.showAlert("Errore", "Il nome del nodo non può essere vuoto.");
                } else if (newName.length() > 24) {
                    app.showAlert("Errore", "Il nome del nodo non può superare i 24 caratteri (spazi inclusi).");
                } else if (isNodeNameDuplicate(newName, app.getRoot(), node)) {
                    app.showAlert("Errore", "Un nodo con questo nome esiste già.");
                }
                else {
                    ModifyNodeNameCommand command = new ModifyNodeNameCommand(node, node.getName(), newName);
                    try {
                        app.getCommandHandler().handle(command);
                        name.setText(newName);
                        app.setSelectedNode(node);
                        app.setSelectedRectangle(rectangle);
                        updateGraphicalTreeHighlight();
                    } catch (Exception ex) {
                        app.showAlert("Errore", "Impossibile modificare il nome del nodo.");
                    }
                }
            });
        });

        contextMenu.getItems().addAll(showEmployeesItem, showRolesItem, renameNodeItem);

        rectangle.setOnContextMenuRequested(event ->
                contextMenu.show(rectangle, event.getScreenX(), event.getScreenY()));

        rectangle.setOnMouseClicked(event -> {
            app.setSelectedNode(node);
            app.setSelectedRectangle(rectangle);
            updateGraphicalTreeHighlight();
            app.updateTables();
        });

        int numberOfChildren = node.getChildren().size();
        if (numberOfChildren > 0) {
            double totalWidth = numberOfChildren * (NODE_WIDTH + HORIZONTAL_SPACING) - HORIZONTAL_SPACING;
            double childStartX = x - totalWidth / 2 + NODE_WIDTH / 2;

            for (OrgNode child : node.getChildren()) {
                Line line = new Line(
                        x + NODE_WIDTH / 2, y + NODE_HEIGHT,
                        childStartX + NODE_WIDTH / 2, y + NODE_HEIGHT + VERTICAL_SPACING
                );
                pane.getChildren().add(line);
                buildGraphicalSubTree(child, childStartX, y + NODE_HEIGHT + VERTICAL_SPACING, pane);
                childStartX += NODE_WIDTH + HORIZONTAL_SPACING;
            }
        }

        if (app.getSelectedNode() == node) {
            app.setSelectedRectangle(rectangle);
            updateGraphicalTreeHighlight();
        }

        return pane;
    }

    private boolean isNodeNameDuplicate(String name, OrgNode rootNode, OrgNode currentNode) {
        if (rootNode.getName().equalsIgnoreCase(name) && !rootNode.equals(currentNode)) {
            return true;
        }
        for (OrgNode child : rootNode.getChildren()) {
            if (isNodeNameDuplicate(name, child, currentNode)) {
                return true;
            }
        }
        return false;
    }


    protected void updateGraphicalTreeHighlight() {
        for (Map.Entry<OrgNode, Rectangle> entry : nodeToRectangleMap.entrySet()) {
            Rectangle rect = entry.getValue();
            if (entry.getKey().equals(app.getSelectedNode())) {
                rect.setFill(Color.LIGHTGREEN);
            } else {
                rect.setFill(Color.LIGHTBLUE);
            }
        }
    }

    protected void centerGraphicalTree() {
        if (graphicalTree == null || graphicalTree.getChildren().isEmpty()) {
            return;
        }

        double contentWidth = graphicalTree.getBoundsInLocal().getWidth();
        double contentHeight = graphicalTree.getBoundsInLocal().getHeight();
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();

        double horizontalOffset = (contentWidth > viewportWidth) ? 0.5 : 0.0;
        double verticalOffset = (contentHeight > viewportHeight) ? 0.5 : 0.0;

        scrollPane.setHvalue(horizontalOffset);
        scrollPane.setVvalue(verticalOffset);
    }


}
