package com.keenant.dhub.hub.network;

public abstract class Device {
    private final Network network;

    public Device(Network network) {
        this.network = network;
    }

    public Network getNetwork() {
        return network;
    }
}
