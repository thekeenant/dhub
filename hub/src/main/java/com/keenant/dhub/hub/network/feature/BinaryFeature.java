package com.keenant.dhub.hub.network.feature;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.keenant.dhub.hub.network.DataFeature;
import com.keenant.dhub.hub.network.ResponsiveFeature;

import java.util.Optional;

/**
 * Set a binary state.
 */
public abstract class BinaryFeature implements ResponsiveFeature, DataFeature {
    public abstract void setState(boolean state);

    public abstract Optional<Boolean> getState();

    public abstract void updateState();

    @Override
    public String getUniqueId() {
        return "binary";
    }

    @Override
    public void respondTo(JsonElement json) {
        boolean state = json.getAsJsonObject().get("state").getAsBoolean();
        setState(state);
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("state", getState().orElse(null));
        return json;
    }
}
