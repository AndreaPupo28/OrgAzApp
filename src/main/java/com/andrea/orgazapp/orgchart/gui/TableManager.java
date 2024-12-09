package com.andrea.orgazapp.orgchart.gui;

import com.andrea.orgazapp.orgchart.command.*;
import com.andrea.orgazapp.orgchart.model.Employee;
import com.andrea.orgazapp.orgchart.model.Role;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TableManager {
    private final OrgChartApp app;
    private TableView<Employee> employeeTable = new TableView<>();
    private TableView<Role> roleTable = new TableView<>();

    public TableManager(OrgChartApp app) {
        this.app = app;
        setupEmployeeTable();
        setupRoleTable();
    }

    public void updateTables(OrgNode selectedNode) {
        updateEmployeeTable(selectedNode);
        updateRoleTable(selectedNode);
    }

    protected void setupRoleTable() {
        if (roleTable == null) {
            roleTable = new TableView<>();
        }

        // Colonna Nome del Ruolo
        TableColumn<Role, String> roleNameCol = new TableColumn<>("Nome del Ruolo");
        roleNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        roleNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        roleNameCol.setOnEditCommit(event -> {
            Role role = event.getRowValue();
            String newName = event.getNewValue();

            if (newName == null || newName.trim().isEmpty()) {
                app.showAlert("Errore", "Il nome del ruolo non può essere vuoto.");
            } else {
                Command command = new ModifyRoleCommand(app.getSelectedNode(), role, newName);
                app.getCommandHandler().handle(command);
                updateRoleTable(app.getSelectedNode());
            }
        });

        // Colonna Azione: Eliminazione
        TableColumn<Role, String> roleActionCol = new TableColumn<>("Azione");
        roleActionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Elimina");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                    deleteButton.setOnAction(event -> {
                        Role role = getTableView().getItems().get(getIndex());
                        Command command = new RemoveRoleCommand(app.getSelectedNode(), role);
                        app.getCommandHandler().handle(command);
                        updateTables(app.getSelectedNode());
                    });
                }
            }
        });

        // Colonna Azione: Modifica
        TableColumn<Role, String> roleEditCol = new TableColumn<>("Modifica");
        roleEditCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifica");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                    editButton.setOnAction(event -> {
                        Role role = getTableView().getItems().get(getIndex());
                        TextInputDialog dialog = new TextInputDialog(role.getName());
                        dialog.setTitle("Modifica Ruolo");
                        dialog.setHeaderText("Modifica il nome del ruolo");
                        dialog.setContentText("Nuovo nome:");

                        dialog.showAndWait().ifPresent(newName -> {
                            if (newName == null || newName.trim().isEmpty()) {
                                app.showAlert("Errore", "Il nome del ruolo non può essere vuoto.");
                            } else {
                                Command command = new ModifyRoleCommand(app.getSelectedNode(), role, newName);
                                app.getCommandHandler().handle(command);
                                updateRoleTable(app.getSelectedNode());
                            }
                        });
                    });
                }
            }
        });

        roleTable.getColumns().clear();
        roleTable.getColumns().addAll(roleNameCol, roleEditCol, roleActionCol);

        roleTable.setEditable(true);
    }


    protected void setupEmployeeTable() {
        employeeTable = new TableView<>();

        // Colonna Nome
        TableColumn<Employee, String> empNameCol = new TableColumn<>("Nome");
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Abilita l'editing inline per il nome
        empNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        empNameCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            String newName = event.getNewValue();

            if (newName == null || newName.trim().isEmpty()) {
                app.showAlert("Errore", "Il nome del dipendente non può essere vuoto.");
            } else {
                Role oldRole = app.getSelectedNode().getRoles().get(employee.getName());
                Role newRole = app.getSelectedNode().getRoles().getOrDefault(newName, oldRole);

                Command command = new ModifyEmployeeCommand(
                        app.getSelectedNode(),
                        employee,
                        oldRole,
                        newRole,
                        employee.getName(),
                        newName
                );
                app.getCommandHandler().handle(command);
                updateTables(app.getSelectedNode());
            }
        });

        // Colonna Ruolo
        TableColumn<Employee, String> empRoleCol = new TableColumn<>("Ruolo");
        empRoleCol.setCellValueFactory(cellData -> {
            Role role = app.getSelectedNode().getRoles().get(cellData.getValue().getName());
            return new ReadOnlyStringWrapper((role != null) ? role.getName() : "Nessun ruolo");
        });

        // Colonna Azione (Eliminazione)
        TableColumn<Employee, String> empActionCol = new TableColumn<>("Azione");
        empActionCol.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button("Elimina");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Employee emp = getTableView().getItems().get(getIndex());
                    btn.setOnAction(event -> {
                        Command command = new RemoveEmployeeCommand(app.getSelectedNode(), emp);
                        app.getCommandHandler().handle(command);
                        updateTables(app.getSelectedNode());

                    });
                    setGraphic(btn);
                }
            }
        });


        // Colonna Modifica
        TableColumn<Employee, String> empEditCol = new TableColumn<>("Modifica");
        empEditCol.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button("Modifica");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Employee employee = getTableView().getItems().get(getIndex());
                    btn.setOnAction(event -> {
                        Dialog<Void> dialog = new Dialog<>();
                        dialog.setTitle("Modifica Dipendente");

                        TextField nameField = new TextField(employee.getName());
                        nameField.setPromptText("Nome del dipendente");

                        ComboBox<Role> roleComboBox = new ComboBox<>(
                                FXCollections.observableArrayList(app.getSelectedNode().getRolesList())
                        );
                        roleComboBox.setValue(app.getSelectedNode().getRoles().get(employee.getName()));
                        roleComboBox.setPromptText("Seleziona un ruolo");

                        VBox content = new VBox(10,
                                new Label("Nome:"), nameField,
                                new Label("Ruolo:"), roleComboBox
                        );
                        dialog.getDialogPane().setContent(content);
                        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                        dialog.setResultConverter(button -> {
                            if (button == ButtonType.OK) {
                                String newName = nameField.getText();
                                Role newRole = roleComboBox.getValue();

                                if (newName == null || newName.trim().isEmpty()) {
                                    app.showAlert("Errore", "Il nome del dipendente non può essere vuoto.");
                                    return null;
                                }
                                if (newRole == null) {
                                    app.showAlert("Errore", "Devi selezionare un ruolo.");
                                    return null;
                                }

                                Role oldRole = app.getSelectedNode().getRoles().get(employee.getName());

                                Command command = new ModifyEmployeeCommand(
                                        app.getSelectedNode(),
                                        employee,
                                        oldRole,
                                        newRole,
                                        employee.getName(),
                                        newName
                                );
                                app.getCommandHandler().handle(command);
                                updateTables(app.getSelectedNode());
                            }
                            return null;
                        });

                        dialog.showAndWait();
                    });
                    setGraphic(btn);
                }
            }
        });

        employeeTable.getColumns().addAll(empNameCol, empRoleCol, empActionCol, empEditCol);
        employeeTable.setEditable(true); // Abilita l'editing della tabella
    }


    public void updateEmployeeTable(OrgNode selectedNode) {
        if (selectedNode != null) {
            employeeTable.getItems().setAll(selectedNode.getEmployees());
        }
    }

    public void updateRoleTable(OrgNode selectedNode) {
        if (selectedNode != null) {
            roleTable.getItems().setAll(selectedNode.getRolesList());
        }
    }

    public void showEmployeeTable() {
        showTableInModal("Dipendenti", employeeTable);
    }

    public void showRoleTable() {
        showTableInModal("Ruoli", roleTable);
    }

    private void showTableInModal(String title, TableView<?> table) {
        Stage stage = new Stage();
        stage.setTitle(title);

        BorderPane layout = new BorderPane();
        layout.setCenter(table);
        Scene scene = new Scene(layout, 300, 400);

        stage.setScene(scene);
        stage.initOwner(app.getPrimaryStage());
        stage.show();
    }
}
