package com.keenant.dhub.hub.plugins.zwave;

import com.keenant.dhub.hub.network.Feature;
import lombok.ToString;

@ToString(exclude = "node")
public abstract class ZFeature implements Feature {
    private final ZNode node;

    public ZFeature(ZNode node) {
        this.node = node;
    }

    public ZNode getNode() {
        return node;
    }
}
