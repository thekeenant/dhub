package com.keenant.dhub.hub.event;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.DataFeature;

public class FeatureChangeEvent extends DeviceEvent {
    private final DataFeature feature;

    public FeatureChangeEvent(DataFeature feature) {
        super(feature.getDevice());
        this.feature = feature;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("feature", feature.getUniqueId());
        json.add("data", feature.toJson());
        return json;
    }
}
