package com.mertyilmaz.lab4;

import java.util.Comparator;

/**
 * Multi-criteria ordering used with Collections.sort(list, comparator):
 *  1. parked vehicles first,
 *  2. vehicle type in the fixed order Car < Motorboat < Bicycle < Scooter,
 *  3. name ascending (alphabetical, case-insensitive),
 *  4. supported fuel mask ascending (0 for non-combustion vehicles),
 *  5. fuel amount ascending (0.0 for non-combustion vehicles).
 */
public class VehicleComparator implements Comparator<Vehicle> {

    @Override
    public int compare(Vehicle a, Vehicle b) {
        int byParked = Boolean.compare(!isParked(a), !isParked(b)); // parked first
        if (byParked != 0) {
            return byParked;
        }
        int byType = Integer.compare(typeRank(a), typeRank(b));
        if (byType != 0) {
            return byType;
        }
        int byName = a.getName().compareToIgnoreCase(b.getName());
        if (byName != 0) {
            return byName;
        }
        int byFuelMask = Integer.compare(fuelMask(a), fuelMask(b));
        if (byFuelMask != 0) {
            return byFuelMask;
        }
        return Double.compare(fuelAmount(a), fuelAmount(b));
    }

    private static boolean isParked(Vehicle v) {
        return v instanceof Parkable && ((Parkable) v).isParked();
    }

    /** Fixed mandatory type order: Car < Motorboat < Bicycle < Scooter. */
    private static int typeRank(Vehicle v) {
        if (v instanceof Car) return 0;
        if (v instanceof Motorboat) return 1;
        if (v instanceof Bicycle) return 2;
        return 3; // Scooter
    }

    private static int fuelMask(Vehicle v) {
        return v instanceof CombustionVehicle
                ? ((CombustionVehicle) v).getSupportedFuelMask() : 0;
    }

    private static double fuelAmount(Vehicle v) {
        return v instanceof CombustionVehicle
                ? ((CombustionVehicle) v).getFuelAmount() : 0.0;
    }
}
