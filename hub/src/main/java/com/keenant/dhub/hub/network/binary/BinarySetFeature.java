package com.keenant.dhub.hub.network.binary;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.Feature;

public interface BinarySetFeature extends Feature {
    @Override
    default String getId() {
        return "binary.set";
    }

    @Override
    default JsonObject toJson() {
        return new JsonObject();
    }

    @Override
    default void acceptData(JsonObject json) {
        boolean value = json.get("value").getAsBoolean();
        setBinary(value);
    }

    void setBinary(boolean value);
}
