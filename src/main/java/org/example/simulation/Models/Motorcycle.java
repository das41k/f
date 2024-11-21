package org.example.simulation.Models;
import java.time.LocalTime;

public class Motorcycle extends Transport {

    public Motorcycle(String id, LocalTime birthTime) {
        super(id, birthTime);
    }

    @Override
    public void move() {
        System.out.println("Motorcycle is moving");
    }

    @Override
    public void display() {
        System.out.println("Motorcycle displayed");
    }
}
