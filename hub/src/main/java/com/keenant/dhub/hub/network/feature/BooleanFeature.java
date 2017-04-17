package com.keenant.dhub.hub.network.feature;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Feature;

import java.util.Objects;
import java.util.Optional;

public abstract class BooleanFeature<D extends Device> extends Feature<D, Boolean> {
    public BooleanFeature(D device) {
        super(device);
    }

    @Override
    public boolean isEqual(Boolean before, Boolean after) {
        return before == after;
    }
}
