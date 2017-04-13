package com.keenant.dhub.hub.network.provider;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.ProviderOptional;
import lombok.ToString;

import java.util.Optional;
import java.util.function.Supplier;

@ToString(callSuper = true)
public class BooleanProvider extends ProviderOptional<Boolean> {
    public BooleanProvider(Device device, Supplier<Optional<Boolean>> supplier) {
        super(device, supplier);
    }

    @Override
    public boolean isEqual(Optional<Boolean> before, Optional<Boolean> after) {
        return before.equals(after);
    }
}
