package com.mertyilmaz.lab4;

/**
 * Interface for vehicles that can be parked in a Garage (Car, Bicycle).
 * A garage holds at most one parkable vehicle; the vehicle keeps a reference
 * to its garage and both sides are always updated together.
 */
public interface Parkable {

    /**
     * Parks the vehicle. Returns true only if the garage is empty AND this
     * vehicle is not parked anywhere else; both references (garage <-> vehicle)
     * are then updated consistently. Returns false otherwise.
     */
    boolean park(Garage garage);

    /** Frees the garage. Returns true only if the vehicle was parked. */
    boolean unpark();

    boolean isParked();

    /** The garage this vehicle is parked in, or null. */
    Garage getGarage();
}
