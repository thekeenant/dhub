package com.keenant.dhub.hub.network.feature;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.DataFeature;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.ResponsiveFeature;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Has many child devices/channels/endpoints, which have features of their own.
 */
public abstract class ChildrenFeature implements DataFeature, ResponsiveFeature {
    public abstract List<? extends Device> getChildren();

    public Optional<? extends Device> getChild(String id) {
        for (Device device : getChildren()) {
            if (device.getUniqueId().equals(id)) {
                return Optional.of(device);
            }
        }
        return Optional.empty();
    }

    @Override
    public String getUniqueId() {
        return "children";
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        for (Device child : getChildren()) {
            json.add(child.getUniqueId(), child.toJson());
        }
        return json;
    }

    @Override
    public void respondTo(JsonElement el) {
        JsonObject json = el.getAsJsonObject();
        for (Entry<String, JsonElement> entry : json.entrySet()) {
            getChild(entry.getKey()).ifPresent(child -> {
                child.respondTo(entry.getValue());
            });
        }
    }
}
