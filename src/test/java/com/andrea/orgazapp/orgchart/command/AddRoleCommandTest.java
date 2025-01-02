package com.andrea.orgazapp.orgchart.command;

import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;
import com.andrea.orgazapp.orgchart.model.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AddRoleCommandTest {

    private OrgNode targetNode;
    private Role role;
    private AddRoleCommand addRoleCommand;

    @BeforeEach
    public void setup() {
        targetNode = new Department("Dipartimento Test");
        Set<String> applicableUnits = Set.of("Department");
        role = new Role("Amministratore", applicableUnits);
        addRoleCommand = new AddRoleCommand(targetNode, role);
    }

    @Test
    public void testDoItSuccess() {
        boolean result = addRoleCommand.doIt();
        assertTrue(result, "doIt dovrebbe restituire true se l'operazione ha successo");
        assertTrue(targetNode.getRolesList().contains(role), "Il ruolo dovrebbe essere aggiunto al nodo");
    }

    @Test
    public void testDoItFailure() {
        targetNode.addRole(role);
        boolean result = addRoleCommand.doIt();
        assertFalse(result, "doIt dovrebbe restituire false se il ruolo è già presente");
    }

    @Test
    public void testUndoItSuccess() {
        addRoleCommand.doIt();
        boolean result = addRoleCommand.undoIt();
        assertTrue(result, "undoIt dovrebbe restituire true se l'operazione ha successo");
        assertFalse(targetNode.getRolesList().contains(role), "Il ruolo dovrebbe essere rimosso dal nodo");
    }

    @Test
    public void testUndoItFailure() {
        boolean result = addRoleCommand.undoIt();
        assertFalse(result, "undoIt dovrebbe restituire false se il ruolo non è presente");
    }
}
