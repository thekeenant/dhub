package com.keenant.dhub.hub.plugins.web;

import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.plugins.web.exception.DeviceNotFoundException;
import spark.Request;
import spark.Response;

public abstract class DeviceRoute extends NetworkRoute {
    public DeviceRoute(Hub hub) {
        super(hub);
    }

    public abstract Object handle(Network<?> network, Device device, Request req, Response res);

    @Override
    public Object handle(Network<?> network, Request req, Response res) {
        Device device = network.getDevice(req.params("device")).orElseThrow(DeviceNotFoundException::new);
        return handle(network, device, req, res);
    }
}
