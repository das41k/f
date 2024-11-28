package org.example.simulation.Models;

import org.example.simulation.Models.IBehaviour;

import java.time.LocalTime;

public abstract class Transport implements IBehaviour {
    private String id;
    private LocalTime birthTime;
    public Transport(String id, LocalTime birthTime) {
        this.id = id;
        this.birthTime = birthTime;
    }

    public String getId() {
        return id;
    }

    public void setBirthTime(LocalTime birthTime) {
        this.birthTime = birthTime;
    }
    public LocalTime getBirthTime() {
        return birthTime;
    }

    @Override
    public abstract void move();

    @Override
    public abstract void display();
}
