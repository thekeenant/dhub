package com.keenant.dhub.hub.network;

import java.util.function.Supplier;

public abstract class Provider<T> {
    private final Supplier<T> supplier;
    private T latestValue;

    public Provider(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public abstract boolean isEqual(T before, T after);

    public void init() {
        update();
    }

    public void update() {
        T newValue = supplier.get();

        if (newValue == null) {
            throw new RuntimeException();
        }

        if (latestValue != null && !isEqual(latestValue, newValue)) {
            // Todo
            System.out.println("Changed from: " + latestValue + " to " + newValue);
        }

        latestValue = newValue;
    }

    public T get() {
        if (latestValue == null) {
            // Todo
            throw new RuntimeException();
        }
        return latestValue;
    }
}
