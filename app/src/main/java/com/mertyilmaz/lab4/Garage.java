package com.mertyilmaz.lab4;

/**
 * A garage of the rental. Holds at most one parkable vehicle at a time
 * (parkedVehicle == null means the garage is empty).
 */
public class Garage {

    private final int number;
    private Parkable parkedVehicle; // null = empty

    public Garage(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public boolean isEmpty() {
        return parkedVehicle == null;
    }

    public Parkable getParkedVehicle() {
        return parkedVehicle;
    }

    /**
     * Package-private on purpose: only park()/unpark() of the vehicles may
     * change this, so the garage <-> vehicle references always stay in sync.
     */
    void setParkedVehicle(Parkable vehicle) {
        this.parkedVehicle = vehicle;
    }
}
