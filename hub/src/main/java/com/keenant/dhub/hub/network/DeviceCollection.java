package com.keenant.dhub.hub.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Optional;

public class DeviceCollection<T extends Device> extends ArrayList<T> implements Data, Responsive {
    public DeviceCollection() {

    }

    public Optional<T> getById(String id) {
        for (T device : this) {
            if (device.getUniqueId().equals(id)) {
                return Optional.of(device);
            }
        }
        return Optional.empty();
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        for (T device : this) {
            json.add(device.getUniqueId(), device.toJson());
        }
        return json;
    }

    @Override
    public void respondTo(JsonElement el) {
        JsonObject json = el.getAsJsonObject();
        for (Entry<String, JsonElement> entry : json.entrySet()) {
            getById(entry.getKey()).ifPresent((device) -> {
                device.respondTo(entry.getValue());
            });
        }
    }
}
