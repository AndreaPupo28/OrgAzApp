package com.andrea.orgazapp.orgchart.model;


import com.andrea.orgazapp.orgchart.memento.Memento;

public record OrgNodeMemento(String state) implements Memento {
}