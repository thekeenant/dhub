package com.keenant.dhub.hub.plugin.web;

import com.google.gson.JsonElement;
import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.network.Network;
import spark.Request;
import spark.Response;

public abstract class NetworkRoute extends JsonRoute {
    protected abstract JsonElement handle(Network<?> network, Request req, Response res);

    @Override
    protected JsonElement handleJson(Request req, Response res) {
        Network<?> network = Hub.getHub().getNetworkManager()
                .getNetwork(req.params("network"))
                .orElseThrow(RuntimeException::new);
        return handle(network, req, res);
    }
}
