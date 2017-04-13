package com.keenant.dhub.hub.network.ability;

import com.keenant.dhub.hub.network.Ability;
import lombok.ToString;

import java.util.function.Consumer;

@ToString
public class BooleanAbility extends Ability<Boolean> {
    public BooleanAbility(Consumer<Boolean> consumer) {
        super(consumer);
    }
}
