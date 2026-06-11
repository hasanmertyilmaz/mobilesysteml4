package com.mertyilmaz.lab4;

import java.util.Locale;

/**
 * A scooter: not a combustion vehicle and not parkable.
 */
public class Scooter extends Vehicle {

    public Scooter(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[%d] Scooter %s", getId(), getName());
    }
}
