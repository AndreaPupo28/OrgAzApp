package com.andrea.orgazapp.orgchart.command;


import java.util.LinkedList;

public class HistoryCommandHandler implements CommandHandler {
    private int maxHistoryLength = 100;
    private final LinkedList<Command> history = new LinkedList<>();
    private final LinkedList<Command> redoList = new LinkedList<>();

    public HistoryCommandHandler(int maxHistoryLength) {
        if (maxHistoryLength < 0)
            throw new IllegalArgumentException();
        this.maxHistoryLength = maxHistoryLength;
    }

    public void handle(Command cmd) {
        if (cmd.doIt()) {
            addToHistory(cmd);
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
            }
        }
    }

    public void redo() {
        if (!redoList.isEmpty()) {
            Command redoCmd = redoList.removeFirst();
            if (redoCmd.doIt()) {
                history.addFirst(redoCmd);
            }
        }
    }

    private void addToHistory(Command cmd) {
        history.addFirst(cmd);
        if (history.size() > maxHistoryLength) {
            history.removeLast();
        }
    }
}
