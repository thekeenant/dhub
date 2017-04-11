package com.keenant.dhub.hub.event;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.Data;
import com.keenant.dhub.hub.network.Network;

public abstract class NetworkEvent implements Data {
    private final Network network;

    public NetworkEvent(Network network) {
        this.network = network;
    }

    public Network getNetwork() {
        return network;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("network", network.getUniqueId());
        return json;
    }
}
