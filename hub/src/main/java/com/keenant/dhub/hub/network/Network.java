package com.keenant.dhub.hub.network;

import com.keenant.dhub.hub.network.event.NetworkEvent;
import net.engio.mbassy.bus.MBassador;

public abstract class Network {
    private final MBassador<NetworkEvent> bus = new MBassador<>();

    public Network() {

    }

    public void subscribe(NetworkListener listener) {
        bus.subscribe(listener);
    }

    public void unsubscribe(NetworkListener listener) {
        bus.unsubscribe(listener);
    }

    public abstract String getUniqueId();

    public abstract void start();

    public abstract void stop();
}
