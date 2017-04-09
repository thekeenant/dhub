package com.keenant.dhub.hub.network;

import com.keenant.dhub.core.Lifecycle;

import java.util.Collection;
import java.util.Optional;

/**
 * A network is a collection of devices that communicate similarly.
 *
 * @param <T> The broad type of device that this network contains.
 */
public interface Network<T extends Device> extends Lifecycle {
    String getId();

    Collection<T> getDevices();

    default Optional<T> getDevice(String id) {
        for (T device : getDevices()) {
            if (device.getUniqueId().equals(id)) {
                return Optional.of(device);
            }
        }
        return Optional.empty();
    }
}
