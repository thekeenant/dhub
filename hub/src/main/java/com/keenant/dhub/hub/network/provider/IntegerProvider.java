package com.keenant.dhub.hub.network.provider;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Provider;
import lombok.ToString;

@ToString(callSuper = true)
public abstract class IntegerProvider<D extends Device> extends Provider<D, Integer> {
    public IntegerProvider(D device) {
        super(device);
    }

    @Override
    public boolean isEqual(Integer before, Integer after) {
        return before.equals(after);
    }
}
