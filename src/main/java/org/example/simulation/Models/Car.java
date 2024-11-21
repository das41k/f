package org.example.simulation.Models;
import java.time.LocalTime;

public class Car extends Transport {

    public Car(String id, LocalTime birthTime) {
        super(id, birthTime);
    }

    @Override
    public void move() {
        System.out.println("Car is moving");
    }

    @Override
    public void display() {
        System.out.println("Car displayed");
    }
}
