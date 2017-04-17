package com.keenant.dhub.hub.action;

import com.keenant.dhub.hub.network.Feature;

import java.util.function.Supplier;

public class FeatureAction<T> implements Action {
    private final Feature<?, T> feature;
    private final Supplier<T> data;

    public FeatureAction(Feature<?, T> feature, Supplier<T> supplier) {
        this.feature = feature;
        this.data = supplier;
    }

    public FeatureAction(Feature<?, T> feature, T data) {
        this(feature, () -> data);
    }

    @Override
    public void execute() {
        feature.set(data.get());
    }

    @Override
    public void stop() {
        // Nothing to stop.
    }
}
