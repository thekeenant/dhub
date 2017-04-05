package com.keenant.dhub.hub.plugins.web;

import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.plugins.web.exception.NetworkNotFoundException;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class NetworkRoute implements Route {
    private final Hub hub;

    public NetworkRoute(Hub hub) {
        this.hub = hub;
    }

    public abstract Object handle(Network<?> network, Request req, Response res);

    public Hub getHub() {
        return hub;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Network<?> network = hub.getNetwork(req.params("network")).orElseThrow(NetworkNotFoundException::new);
        return handle(network, req, res);
    }
}
