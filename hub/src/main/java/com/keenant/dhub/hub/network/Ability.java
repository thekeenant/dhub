package com.keenant.dhub.hub.network;

import java.util.function.Consumer;

public abstract class Ability<T> {
    private final Consumer<T> consumer;

    public Ability(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public void perform(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data provided to ability must not be null.");
        }
        consumer.accept(data);
    }
}
