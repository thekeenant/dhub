package com.keenant.dhub.hub.network;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class ProviderOptional<T> extends Provider<Optional<T>> {
    public ProviderOptional(Device device, Supplier<Optional<T>> supplier) {
        super(device, supplier, Optional.empty());
    }
}
