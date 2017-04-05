package com.keenant.dhub.hub.network.binary;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.Feature;

public interface BinaryGetFeature extends Feature {
    @Override
    default String getId() {
        return "binary.get";
    }

    @Override
    default JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("value", getBinary());
        return json;
    }

    @Override
    default void acceptData(JsonObject json) {
        // Todo: Exception
    }

    boolean getBinary();
}
