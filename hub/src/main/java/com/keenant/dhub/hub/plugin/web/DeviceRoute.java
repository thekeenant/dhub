package com.keenant.dhub.hub.plugin.web;

import com.google.gson.JsonElement;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Network;
import spark.Request;
import spark.Response;

public abstract class DeviceRoute extends NetworkRoute {
    protected abstract JsonElement handle(Device<?> device, Request req, Response res);

    @Override
    protected JsonElement handle(Network<?> network, Request req, Response res) {
        Device<?> device = network.getDevice(req.params("device")).orElseThrow(RuntimeException::new);
        return handle(device, req, res);
    }
}
