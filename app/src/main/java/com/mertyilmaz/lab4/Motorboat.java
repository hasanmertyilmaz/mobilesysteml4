package com.mertyilmaz.lab4;

import java.util.Locale;

/**
 * A motorboat: combustion vehicle (bitmask of supported fuels + fuel amount).
 * Motorboats are NOT parkable.
 */
public class Motorboat extends Vehicle implements CombustionVehicle {

    private final int supportedFuelMask;
    private double fuelAmount;

    public Motorboat(String name, int supportedFuelMask) {
        super(name);
        this.supportedFuelMask = supportedFuelMask;
    }

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

    @Override
    public String toString() {
        return String.format(Locale.US,
                "[%d] Motorboat %s | fuel: %s (mask %d), %.1f l",
                getId(), getName(),
                CombustionVehicle.describeFuelMask(supportedFuelMask),
                supportedFuelMask, fuelAmount);
    }
}
