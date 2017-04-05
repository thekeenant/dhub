package com.keenant.dhub.hub.plugins.zwave;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.Feature;
import lombok.ToString;

@ToString(exclude = "node")
public class ZFeature implements Feature {
    private final ZNode node;

    public ZFeature(ZNode node) {
        this.node = node;
    }

    public ZNode getNode() {
        return node;
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException("getId() not implemented");
    }

    @Override
    public JsonObject toJson() {
        throw new UnsupportedOperationException("toJson() not implemented");
    }

    @Override
    public void acceptData(JsonObject json) {
        throw new UnsupportedOperationException("acceptData(JsonObject) not implemented");
    }
}
