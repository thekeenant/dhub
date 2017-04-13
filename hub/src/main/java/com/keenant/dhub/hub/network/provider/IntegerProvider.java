package com.keenant.dhub.hub.network.provider;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.ProviderOptional;
import lombok.ToString;

import java.util.Optional;
import java.util.function.Supplier;

@ToString(callSuper = true)
public class IntegerProvider extends ProviderOptional<Integer> {
    public IntegerProvider(Device device, Supplier<Optional<Integer>> supplier) {
        super(device, supplier);
    }

    @Override
    public boolean isEqual(Optional<Integer> before, Optional<Integer> after) {
        return before.equals(after);
    }
}
