package com.mertyilmaz.lab4;

import java.util.Locale;

/**
 * A car: combustion vehicle (bitmask of supported fuels + fuel amount)
 * that can also be parked in a garage.
 */
public class Car extends Vehicle implements CombustionVehicle, Parkable {

    private final int supportedFuelMask;
    private double fuelAmount;
    private Garage garage; // null = not parked

    public Car(String name, int supportedFuelMask) {
        super(name);
        this.supportedFuelMask = supportedFuelMask;
    }

    // --- CombustionVehicle ---

    @Override
    public boolean refuel(int fuelMask, double liters) {
        if (liters <= 0) {
            return false;
        }
        if ((supportedFuelMask & fuelMask) == 0) {
            return false; // unsupported fuel type, no change
        }
        fuelAmount += liters;
        return true;
    }

    @Override
    public int getSupportedFuelMask() {
        return supportedFuelMask;
    }

    @Override
    public double getFuelAmount() {
        return fuelAmount;
    }

    /** Used by the XML loader to restore the saved fuel amount. */
    void setFuelAmount(double fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    // --- Parkable ---

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

    // --- presentation ---

    @Override
    public String toString() {
        return String.format(Locale.US,
                "[%d] Car %s | fuel: %s (mask %d), %.1f l | parked: %s",
                getId(), getName(),
                CombustionVehicle.describeFuelMask(supportedFuelMask),
                supportedFuelMask, fuelAmount,
                isParked() ? "yes, garage " + garage.getNumber() : "no, garage -");
    }
}
