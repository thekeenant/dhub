package com.keenant.dhub.hub.network.level;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.Feature;

public interface LevelSetFeature extends Feature {
    @Override
    default String getId() {
        return "level.set";
    }

    @Override
    default JsonObject toJson() {
        return new JsonObject();
    }

    @Override
    default void acceptData(JsonObject json) {
        int value = json.get("value").getAsInt();
        setLevel(value);
    }

    void setLevel(int value);
}
