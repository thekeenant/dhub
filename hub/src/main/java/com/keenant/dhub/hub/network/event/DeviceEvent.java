package com.keenant.dhub.hub.network.event;

import com.keenant.dhub.hub.network.Device;

public abstract class DeviceEvent extends NetworkEvent {
    private final Device device;

    public DeviceEvent(Device device) {
        super(device.getNetwork());
        this.device = device;
    }

    public Device getDevice() {
        return this.device;
    }
}
