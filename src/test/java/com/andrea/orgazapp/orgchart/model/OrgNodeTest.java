package com.andrea.orgazapp.orgchart.model;

import com.andrea.orgazapp.orgchart.observer.OrgChartObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class OrgNodeTest {

    private OrgNode rootNode;

    @BeforeEach
    public void setup() {
        rootNode = new Management("Root");
    }

    @Test
    public void testAddChild() {
        OrgNode child = new Department("Dipartimento vendite");
        rootNode.addChild(child);
        assertTrue(rootNode.getChildren().contains(child));
    }

    @Test
    public void testRemoveNode() {
        OrgNode child = new Department("Dipartimento vendite");
        rootNode.addChild(child);
        boolean removed = rootNode.removeNode("Dipartimento vendite");
        assertTrue(removed);
        assertFalse(rootNode.getChildren().contains(child));
    }

    @Test
    public void testObserverNotification() {
        TestObserver observer = new TestObserver();
        rootNode.addObserver(observer);
        rootNode.setName("Nuovo nome");
        assertTrue(observer.isNotified());
    }

    @Test
    public void testAssignRole() {
        Employee employee = new Employee("Mario Rossi");
        Set<String> applicableUnits = Set.of("Department");
        Role role = new Role("Direttore", applicableUnits);

        rootNode.addEmployee(employee);
        rootNode.addRole(role);
        rootNode.assignRole(employee, role);

        assertEquals(role, rootNode.getRoles().get("Mario Rossi"));
        assertEquals("Direttore", role.getName());
        assertTrue(role.isValidForUnit("Department"));
    }


    @Test
    public void testSaveAndRestoreMemento() {
        rootNode.setName("Nome originario");
        OrgNodeMemento memento = (OrgNodeMemento) rootNode.saveToMemento();
        rootNode.setName("Nome cambiato");
        rootNode.restoreFromMemento(memento);
        assertEquals("Nome originario", rootNode.getName());
    }

    private static class TestObserver implements OrgChartObserver {
        private boolean notified = false;

        @Override
        public void onOrgChartUpdated() {
            notified = true;
        }

        public boolean isNotified() {
            return notified;
        }
    }
}
