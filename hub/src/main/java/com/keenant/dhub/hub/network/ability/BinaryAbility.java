package com.keenant.dhub.hub.network.ability;

import com.keenant.dhub.hub.network.Ability;

import java.util.function.Consumer;

public class BinaryAbility implements Ability {
    private final Consumer<Boolean> consumer;

    public BinaryAbility(Consumer<Boolean> consumer) {
        this.consumer = consumer;
    }

    public void setBinary(boolean binary) {
        this.consumer.accept(binary);
    }
}
