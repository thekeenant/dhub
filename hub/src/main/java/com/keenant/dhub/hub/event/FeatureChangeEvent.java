package com.keenant.dhub.hub.event;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.DataFeature;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Network;

public class FeatureChangeEvent extends DeviceEvent {
    private final DataFeature feature;

    public FeatureChangeEvent(Network network, Device device, DataFeature feature) {
        super(network, device);
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
