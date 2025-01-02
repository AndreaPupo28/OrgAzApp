package com.andrea.orgazapp.orgchart.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HistoryCommandHandlerTest {

    private HistoryCommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        commandHandler = new HistoryCommandHandler(5);
    }

    static class TestCommand implements Command {
        private boolean executed = false;
        private boolean undone = false;

        @Override
        public boolean doIt() {
            if (executed) return false;
            executed = true;
            undone = false;
            return true;
        }

        @Override
        public boolean undoIt() {
            if (!executed || undone) return false;
            executed = false;
            undone = true;
            return true;
        }

        public boolean isExecuted() {
            return executed;
        }

        public boolean isUndone() {
            return undone;
        }
    }

    @Test
    void testHandleSuccess() {
        TestCommand command = new TestCommand();
        commandHandler.handle(command);

        assertTrue(command.isExecuted(), "Il comando dovrebbe essere eseguito correttamente.");
        assertEquals(1, commandHandler.getHistory().size(), "La cronologia dovrebbe contenere un comando.");
    }

    @Test
    void testUndoRemovesLastCommand() {
        TestCommand command1 = new TestCommand();
        TestCommand command2 = new TestCommand();

        commandHandler.handle(command1);
        commandHandler.handle(command2);

        assertEquals(2, commandHandler.getHistory().size(), "La cronologia dovrebbe contenere due comandi.");

        commandHandler.undo();

        assertEquals(1, commandHandler.getHistory().size(), "Dopo un undo, la cronologia dovrebbe contenere un comando.");
        assertTrue(commandHandler.getRedoList().contains(command2), "Il comando annullato dovrebbe essere nella lista redo.");
    }

    @Test
    void testRedoRestoresLastUndoneCommand() {
        TestCommand command = new TestCommand();

        commandHandler.handle(command);
        commandHandler.undo();

        assertTrue(command.isUndone(), "Il comando dovrebbe essere annullato.");
        assertTrue(commandHandler.getRedoList().contains(command), "Il comando dovrebbe essere nella lista redo.");

        commandHandler.redo();

        assertTrue(command.isExecuted(), "Il comando dovrebbe essere rieseguito.");
        assertTrue(commandHandler.getHistory().contains(command), "Il comando rieseguito dovrebbe essere nella cronologia.");
    }

    @Test
    void testHistoryLimit() {
        for (int i = 0; i < 6; i++) {
            commandHandler.handle(new TestCommand());
        }
        assertEquals(5, commandHandler.getHistory().size(), "La cronologia dovrebbe contenere solo gli ultimi 5 comandi.");
    }

    @Test
    void testInvalidMaxHistoryLength() {
        assertThrows(IllegalArgumentException.class, () -> new HistoryCommandHandler(-1),
                "Un valore negativo per la lunghezza massima dovrebbe lanciare un'eccezione.");
    }

    @Test
    void testRedoWithEmptyRedoList() {
        assertDoesNotThrow(() -> commandHandler.redo(), "Non dovrebbe accadere nulla quando si prova a rifare senza comandi.");
    }

    @Test
    void testUndoWithEmptyHistory() {
        assertDoesNotThrow(() -> commandHandler.undo(), "Non dovrebbe accadere nulla quando si prova ad annullare senza comandi.");
    }
}
