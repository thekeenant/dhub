package com.keenant.dhub.hub.network.feature;

import com.google.gson.JsonElement;
import com.keenant.dhub.hub.network.DataFeature;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.DeviceCollection;
import com.keenant.dhub.hub.network.Network;

/**
 * Has many child devices/channels/endpoints, which have features of their own.
 */
public abstract class ChildrenFeature<T extends Device> implements Network, DataFeature {
    private final DeviceCollection<T> devices;

    public ChildrenFeature() {
        this.devices = new DeviceCollection<>();
    }

    @Override
    public DeviceCollection<T> getDevices() {
        return devices;
    }

    @Override
    public String getUniqueId() {
        return "children";
    }

    @Override
    public JsonElement toJson() {
        return Network.super.toJson();
    }
}
