package com.keenant.dhub.hub.network;

import com.google.gson.JsonObject;

/**
 * A single {@link Device} may have many features, i.e. things like an ability to report the temperature,
 * the ability to set a multilevel switch...
 */
public interface Feature {
    String getId();

    JsonObject toJson();

    void acceptData(JsonObject json);
}
