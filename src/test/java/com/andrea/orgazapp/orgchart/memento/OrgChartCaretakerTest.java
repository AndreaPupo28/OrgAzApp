package com.andrea.orgazapp.orgchart.memento;

import com.andrea.orgazapp.orgchart.model.Management;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class OrgChartCaretakerTest {

    private static final String TEST_FILE = "testOrgChart.json";
    private OrgChartCaretaker caretaker;

    @BeforeEach
    void setUp() {
        caretaker = new OrgChartCaretaker();
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(new File(TEST_FILE).toPath());
    }

    @Test
    void testSaveToFileSuccess() {
        OrgNode root = new Management("Root");
        Set<String> applicableUnits = Set.of("Management");
        Role role = new Role("Manager", applicableUnits);
        root.addRole(role);
        assertDoesNotThrow(() -> caretaker.saveToFile(root, TEST_FILE), "Il salvataggio su file non dovrebbe generare eccezioni.");
        assertTrue(new File(TEST_FILE).exists(), "Il file di output dovrebbe essere creato.");
    }

    @Test
    void testLoadFromFileSuccess() {
        OrgNode root = new Management("Root");
        Set<String> applicableUnits = Set.of("Management");
        Role role = new Role("Manager", applicableUnits);
        root.addRole(role);
        caretaker.saveToFile(root, TEST_FILE);
        OrgNode loadedNode = caretaker.loadFromFile(TEST_FILE, Management.class);
        assertEquals("Root", loadedNode.getName(), "Il nome del nodo caricato dovrebbe essere 'Root'.");
        assertTrue(loadedNode.getRolesList().stream()
                .anyMatch(r -> r.getName().equals("Manager")), "Il ruolo caricato dovrebbe essere presente.");
    }


    @Test
    void testLoadFromFileNonExistent() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> caretaker.loadFromFile("nonexistent.json", Management.class),
                "Dovrebbe lanciare un'eccezione se il file non esiste.");
        assertTrue(exception.getMessage().contains("Errore durante il caricamento dal file"));
    }

    @Test
    void testSaveToFileHandlesIOException() {
        String invalidPath = "/root/nonWritableFile.json";
        OrgNode root = new Management("Root");
        Exception exception = assertThrows(RuntimeException.class,
                () -> caretaker.saveToFile(root, invalidPath),
                "Dovrebbe lanciare un'eccezione se il salvataggio fallisce.");
        assertTrue(exception.getMessage().contains("Errore durante il salvataggio su file"));
    }
}
