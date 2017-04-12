package com.keenant.dhub.hub.network.provider;

import com.keenant.dhub.hub.network.ProviderOptional;

import java.util.Optional;
import java.util.function.Supplier;

public class BinaryProvider extends ProviderOptional<Boolean> {
    public BinaryProvider(Supplier<Optional<Boolean>> supplier) {
        super(supplier);
    }
}
