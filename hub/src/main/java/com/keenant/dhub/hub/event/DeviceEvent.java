package com.keenant.dhub.hub.event;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Network;

public abstract class DeviceEvent extends NetworkEvent {
    private Device device;

    public DeviceEvent(Device device) {
        super(device.getNetwork());
        this.device = device;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("device", device.getUniqueId());
        return json;
    }
}
