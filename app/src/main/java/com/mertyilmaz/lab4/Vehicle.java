package com.mertyilmaz.lab4;

/**
 * Abstract base class of all vehicles.
 *
 * Every vehicle (loaded from XML or created by the user) automatically gets
 * a unique ID in this constructor from the static counter nextId, so no
 * duplicate IDs can exist during program runtime.
 */
public abstract class Vehicle {

    private static int nextId = 1; // global counter for unique IDs

    private final int id;
    private String name;

    protected Vehicle(String name) {
        this.id = nextId++;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public abstract String toString();
}
