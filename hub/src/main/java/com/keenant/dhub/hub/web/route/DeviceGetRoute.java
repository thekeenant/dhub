package com.keenant.dhub.hub.web.route;

import com.google.gson.JsonElement;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.web.NetworkProvider;
import com.keenant.dhub.hub.web.exception.DeviceNotFoundException;
import spark.Request;
import spark.Response;

public abstract class DeviceGetRoute extends NetworkGetRoute {
    private final String deviceParam;

    public DeviceGetRoute(NetworkProvider network, String deviceParam) {
        super(network);
        this.deviceParam = deviceParam;
    }

    public Device getDevice(Network network, Request req) {
        return network.getDevices().getById(req.params(deviceParam)).orElseThrow(DeviceNotFoundException::new);
    }

    public abstract JsonElement handle(Network network, Device device, Request req, Response res);

    @Override
    public JsonElement handle(Network network, Request req, Response res) throws Exception {
        return handle(network, getDevice(network, req), req, res);
    }
}
