package com.keenant.dhub.hub.network.ability;

import com.keenant.dhub.hub.network.Ability;
import lombok.ToString;

import java.util.function.Consumer;

@ToString
public class IntegerAbility extends Ability<Integer> {
    private final int min;
    private final int max;

    public IntegerAbility(Consumer<Integer> consumer, int min, int max) {
        super(consumer);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public void perform(Integer data) {
        if (data < min || data > max) {
            throw new IllegalArgumentException("Integer out bounds for ability.");
        }
        super.perform(data);
    }
}
