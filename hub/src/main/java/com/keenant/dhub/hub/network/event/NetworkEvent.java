package com.keenant.dhub.hub.network.event;

import com.keenant.dhub.hub.network.Network;

public abstract class NetworkEvent {
    private final Network network;

    public NetworkEvent(Network network) {
        this.network = network;
    }
}
