package com.keenant.dhub.hub.network.event;

import com.keenant.dhub.hub.network.Network;

public class NetworkEvent {
    private final Network network;

    public NetworkEvent(Network network) {
        this.network = network;
    }

    public Network getNetwork() {
        return network;
    }
}
