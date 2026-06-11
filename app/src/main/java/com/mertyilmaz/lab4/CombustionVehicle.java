package com.mertyilmaz.lab4;

/**
 * Interface for vehicles with a combustion engine (Car, Motorboat).
 *
 * Supported fuels are stored as an integer BITMASK built from the constants
 * below, so one vehicle can support several fuels at once,
 * e.g. PETROL | LPG == 6.
 *
 * Fuel mapping (also used 1:1 for the fuelType value in the XML files):
 *   DIESEL = 1, PETROL = 2, LPG = 4, CNG = 8,
 *   3 = DIESEL|PETROL, 5 = DIESEL|LPG, 6 = PETROL|LPG, ...
 */
public interface CombustionVehicle {

    int DIESEL = 1 << 0; // 1
    int PETROL = 1 << 1; // 2
    int LPG    = 1 << 2; // 4
    int CNG    = 1 << 3; // 8

    /**
     * Adds fuel. Returns true only if liters > 0 and the given fuelMask
     * overlaps the supported mask ((supported & fuelMask) != 0);
     * otherwise the state does not change and false is returned.
     */
    boolean refuel(int fuelMask, double liters);

    int getSupportedFuelMask();

    double getFuelAmount();

    /** Readable fuel list for a mask, e.g. 3 -> "DIESEL|PETROL", 0 -> "NONE". */
    static String describeFuelMask(int mask) {
        if (mask == 0) {
            return "NONE";
        }
        StringBuilder sb = new StringBuilder();
        if ((mask & DIESEL) != 0) sb.append("DIESEL|");
        if ((mask & PETROL) != 0) sb.append("PETROL|");
        if ((mask & LPG) != 0) sb.append("LPG|");
        if ((mask & CNG) != 0) sb.append("CNG|");
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
