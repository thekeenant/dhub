package com.keenant.dhub.hub.network.level;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.Feature;

public interface LevelGetFeature extends Feature {
    @Override
    default String getId() {
        return "level.get";
    }

    @Override
    default JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("value", getLevel());
        return json;
    }

    @Override
    default void acceptData(JsonObject json) {

    }

    int getLevel();
}
