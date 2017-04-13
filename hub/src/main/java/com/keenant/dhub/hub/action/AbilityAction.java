package com.keenant.dhub.hub.action;

import com.keenant.dhub.hub.network.Ability;

import java.util.function.Supplier;

public class AbilityAction<T> implements Action {
    private final Ability<T> ability;
    private final Supplier<T> data;

    public AbilityAction(Ability<T> ability, Supplier<T> supplier) {
        this.ability = ability;
        this.data = supplier;
    }

    public AbilityAction(Ability<T> ability, T data) {
        this(ability, () -> data);
    }

    @Override
    public void execute() {
        ability.perform(data.get());
    }

    @Override
    public void stop() {
        // Nothing to stop.
    }
}
