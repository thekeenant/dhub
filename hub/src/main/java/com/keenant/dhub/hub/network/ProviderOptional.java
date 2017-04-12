package com.keenant.dhub.hub.network;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class ProviderOptional<T> extends Provider<Optional<T>> {
    public ProviderOptional(Supplier<Optional<T>> supplier) {
        super(supplier);
    }
}
