package com.keenant.dhub.hub.network;

import com.keenant.dhub.hub.network.event.NetworkEvent;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler.ConsoleLogger;

import java.util.ArrayList;
import java.util.List;

public abstract class Network<T extends Device> {
    private boolean started;
    private final List<T> devices = new ArrayList<>();
    private final MBassador<NetworkEvent> bus = new MBassador<>(new ConsoleLogger(true));

    public void publish(NetworkEvent event) {
        bus.publish(event);
    }

    public void subscribe(NetworkListener listener) {
        bus.subscribe(listener);
    }

    public void unsubscribe(NetworkListener listener) {
        bus.unsubscribe(listener);
    }

    public List<T> getDevices() {
        return devices;
    }

    public void addDevice(T device) {
        if (!started) {
            throw new UnsupportedOperationException("Network not started.");
        }
        devices.add(device);
        device.start();
    }

    public void removeDevice(T device) {
        if (!started) {
            throw new UnsupportedOperationException("Network not started.");
        }
        devices.remove(device);
        device.stop();
    }

    public abstract String getUniqueId();

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }
}
