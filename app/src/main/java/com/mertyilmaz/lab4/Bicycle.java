package com.mertyilmaz.lab4;

import java.util.Locale;

/**
 * A bicycle: no fuel, but it can be parked in a garage.
 */
public class Bicycle extends Vehicle implements Parkable {

    private Garage garage; // null = not parked

    public Bicycle(String name) {
        super(name);
    }

    @Override
    public boolean park(Garage garage) {
        if (garage == null || !garage.isEmpty() || isParked()) {
            return false;
        }
        this.garage = garage;
        garage.setParkedVehicle(this);
        return true;
    }

    @Override
    public boolean unpark() {
        if (!isParked()) {
            return false;
        }
        garage.setParkedVehicle(null);
        garage = null;
        return true;
    }

    @Override
    public boolean isParked() {
        return garage != null;
    }

    @Override
    public Garage getGarage() {
        return garage;
    }

    @Override
    public String toString() {
        return String.format(Locale.US,
                "[%d] Bicycle %s | parked: %s",
                getId(), getName(),
                isParked() ? "yes, garage " + garage.getNumber() : "no, garage -");
    }
}
