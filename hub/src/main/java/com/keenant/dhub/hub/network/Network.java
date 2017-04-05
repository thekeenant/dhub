package com.keenant.dhub.hub.network;

import java.util.Collection;
import java.util.Optional;

/**
 * A network is a collection of devices that communicate similarly.
 *
 * @param <T> The broad type of device that this network contains.
 */
public interface Network<T extends Device> {
    String getId();

    void loadDevices();

    Collection<T> getDevices();

    default Optional<T> getDevice(String id) {
        for (T device : getDevices()) {
            if (device.getId().equals(id)) {
                return Optional.of(device);
            }
        }
        return Optional.empty();
    }
}
