package com.keenant.dhub.hub.event;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.DataFeature;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Network;

public class NestedFeatureChangeEvent extends FeatureChangeEvent {
    private final FeatureChangeEvent event;

    public NestedFeatureChangeEvent(Network network, Device device, DataFeature feature, FeatureChangeEvent event) {
        super(network, device, feature);
        this.event = event;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("event", event.toJson());
        return json;
    }
}
