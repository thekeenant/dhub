package com.keenant.dhub.hub.network;

public interface SubNetwork extends Network {
    @Override
    default String getUniqueId() {
        throw new UnsupportedOperationException();
    }

    Device getDevice();
}
