package com.keenant.dhub.hub.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keenant.dhub.core.Lifecycle;
import com.keenant.dhub.hub.event.NetworkEvent;

/**
 * A network is a collection of devices that communicate similarly.
 */
public interface Network extends Lifecycle, Data {
    String getUniqueId();

    DeviceCollection<?> getDevices();

    void subscribe(NetworkListener listener);

    void unsubscribe(NetworkListener listener);

    void publish(NetworkEvent event);

    @Override
    default JsonElement toJson() {
        JsonObject json = new JsonObject();

        JsonArray devicesJson = new JsonArray();
        for (Device device : getDevices()) {
            JsonObject deviceJson = new JsonObject();
            deviceJson.addProperty("id", device.getUniqueId());
            devicesJson.add(deviceJson);
        }
        json.add("devices", devicesJson);

        return json;
    }
}
