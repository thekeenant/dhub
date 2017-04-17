package com.keenant.dhub.hub.network.provider;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Provider;
import lombok.ToString;

@ToString(callSuper = true)
public abstract class BooleanProvider<D extends Device> extends Provider<D, Boolean> {
    public BooleanProvider(D device) {
        super(device);
    }

    @Override
    public boolean isEqual(Boolean before, Boolean after) {
        return before.equals(after);
    }
}
