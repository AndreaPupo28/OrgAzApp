package com.andrea.orgazapp.orgchart.memento;

public interface Originator {
    Memento saveToMemento();
    void restoreFromMemento(Memento memento);
}
