package com.keenant.dhub.hub.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keenant.dhub.core.Lifecycle;

import java.util.Collection;
import java.util.Optional;

/**
 * A network is a collection of devices that communicate similarly.
 */
public interface Network extends Lifecycle, Data {
    String getUniqueId();

    Collection<? extends Device> getDevices();

    default Optional<? extends Device> getDevice(String id) {
        for (Device device : getDevices()) {
            if (device.getUniqueId().equals(id)) {
                return Optional.of(device);
            }
        }
        return Optional.empty();
    }

    @Override
    default JsonElement toJson() {
        JsonObject json = new JsonObject();
        for (Device device : getDevices()) {
            json.add(device.getUniqueId(), device.toJson());
        }
        return json;
    }
}
