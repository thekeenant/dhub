package com.keenant.dhub.hub.plugins.web;

import com.google.gson.JsonElement;
import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.plugins.web.exception.NetworkNotFoundException;
import spark.Request;
import spark.Response;

public abstract class NetworkRoute extends ApiRoute {
    private final Hub hub;

    public NetworkRoute(Hub hub) {
        this.hub = hub;
    }

    public abstract JsonElement handle(Network network, Request req, Response res);

    public Hub getHub() {
        return hub;
    }

    @Override
    public JsonElement jsonHandle(Request req, Response res) throws Exception {
        Network network = hub.getNetwork(req.params("network")).orElseThrow(NetworkNotFoundException::new);
        return handle(network, req, res);
    }
}
