package com.mertyilmaz.lab4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The rental: owns the vehicle list (ArrayList) and the garages, and
 * implements the user operations (park, add, remove, sort). Every operation
 * returns a clear user message with the result and the reason.
 */
public class Rental {

    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private final ArrayList<Garage> garages = new ArrayList<>();

    public Rental(int garageCount) {
        for (int i = 1; i <= garageCount; i++) {
            garages.add(new Garage(i));
        }
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public List<Garage> getGarages() {
        return garages;
    }

    public Garage getGarage(int number) {
        for (Garage g : garages) {
            if (g.getNumber() == number) {
                return g;
            }
        }
        return null;
    }

    public Vehicle findById(int id) {
        for (Vehicle v : vehicles) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    /** Operation 1: park a vehicle (by ID) in a garage (by number). */
    public String parkVehicle(int vehicleId, int garageNumber) {
        Vehicle vehicle = findById(vehicleId);
        if (vehicle == null) {
            return "Failure: no vehicle with ID " + vehicleId + ".";
        }
        Garage garage = getGarage(garageNumber);
        if (garage == null) {
            return "Failure: no garage number " + garageNumber + ".";
        }
        if (!(vehicle instanceof Parkable)) {
            return "Failure: vehicle [" + vehicleId + "] is not parkable.";
        }
        Parkable parkable = (Parkable) vehicle;
        if (parkable.isParked()) {
            return "Failure: vehicle [" + vehicleId + "] is already parked in garage "
                    + parkable.getGarage().getNumber() + ".";
        }
        if (!garage.isEmpty()) {
            return "Failure: garage " + garageNumber + " is occupied.";
        }
        boolean ok = parkable.park(garage);
        return ok
                ? "Success: vehicle [" + vehicleId + "] parked in garage " + garageNumber + "."
                : "Failure: could not park vehicle [" + vehicleId + "].";
    }

    /** Unparks a vehicle (by ID). */
    public String unparkVehicle(int vehicleId) {
        Vehicle vehicle = findById(vehicleId);
        if (vehicle == null) {
            return "Failure: no vehicle with ID " + vehicleId + ".";
        }
        if (!(vehicle instanceof Parkable)) {
            return "Failure: vehicle [" + vehicleId + "] is not parkable.";
        }
        Parkable parkable = (Parkable) vehicle;
        if (!parkable.isParked()) {
            return "Failure: vehicle [" + vehicleId + "] is not parked.";
        }
        int number = parkable.getGarage().getNumber();
        parkable.unpark();
        return "Success: vehicle [" + vehicleId + "] left garage " + number + ".";
    }

    /**
     * Operation 3: remove a vehicle by ID. Documented behavior: if the vehicle
     * is parked it is unparked automatically first, and the message says so.
     */
    public String removeVehicle(int vehicleId) {
        Vehicle vehicle = findById(vehicleId);
        if (vehicle == null) {
            return "Failure: no vehicle with ID " + vehicleId + ".";
        }
        String note = "";
        if (vehicle instanceof Parkable && ((Parkable) vehicle).isParked()) {
            int number = ((Parkable) vehicle).getGarage().getNumber();
            ((Parkable) vehicle).unpark();
            note = " (it was unparked from garage " + number + " automatically)";
        }
        vehicles.remove(vehicle);
        return "Success: vehicle [" + vehicleId + "] removed" + note + ".";
    }

    /** Sorting with the mandatory multi-criteria order (see VehicleComparator). */
    public void sortVehicles() {
        Collections.sort(vehicles, new VehicleComparator());
    }
}
