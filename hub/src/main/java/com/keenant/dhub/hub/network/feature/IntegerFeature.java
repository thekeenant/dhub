package com.keenant.dhub.hub.network.feature;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Feature;

public abstract class IntegerFeature<D extends Device> extends Feature<D, Integer> {
    public IntegerFeature(D device) {
        super(device);
    }

    @Override
    public boolean isEqual(Integer before, Integer after) {
        return before.equals(after);
    }
}
