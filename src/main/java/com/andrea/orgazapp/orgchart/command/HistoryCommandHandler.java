package com.andrea.orgazapp.orgchart.command;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class HistoryCommandHandler implements CommandHandler {
    private int maxHistoryLength;
    private final LinkedList<Command> history = new LinkedList<>();
    private final LinkedList<Command> redoList = new LinkedList<>();
    private final Set<Command> executedCommands = new HashSet<>();

    public HistoryCommandHandler(int maxHistoryLength) {
        if (maxHistoryLength < 0)
            throw new IllegalArgumentException();
        this.maxHistoryLength = maxHistoryLength;
    }

    public void handle(Command cmd) {
        if (executedCommands.contains(cmd)) {
            return;
        }
        if (cmd.doIt()) {
            addToHistory(cmd);
            executedCommands.add(cmd);
        } else {
            history.clear();
        }
        redoList.clear();
    }

    public void undo() {
        if (!history.isEmpty()) {
            Command undoCmd = history.removeFirst();
            if (undoCmd.undoIt()) {
                redoList.addFirst(undoCmd);
                executedCommands.remove(undoCmd);
            } else {
                history.addFirst(undoCmd);
            }
        }
    }


    public void redo() {
        if (!redoList.isEmpty()) {
            Command redoCmd = redoList.removeFirst();
            if (redoCmd.doIt()) {
                history.addFirst(redoCmd);
                executedCommands.add(redoCmd);
            }
        }
    }

    private void addToHistory(Command cmd) {
        history.addFirst(cmd);
        if (history.size() > maxHistoryLength) {
            Command removed = history.removeLast();
            executedCommands.remove(removed);
        }
    }

    LinkedList<Command> getHistory() {
        return new LinkedList<>(history);
    }

    LinkedList<Command> getRedoList() {
        return new LinkedList<>(redoList);
    }

}
