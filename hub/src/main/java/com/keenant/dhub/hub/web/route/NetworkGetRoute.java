package com.keenant.dhub.hub.web.route;

import com.google.gson.JsonElement;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.web.NetworkProvider;
import spark.Request;
import spark.Response;

public abstract class NetworkGetRoute extends ApiGetRoute implements NetworkProvider {
    private final NetworkProvider provider;

    public NetworkGetRoute(NetworkProvider provider) {
        this.provider = provider;
    }

    public Network getNetwork(Request req) {
        return provider.getNetwork(req);
    }

    public abstract JsonElement handle(Network network, Request req, Response res) throws Exception;

    @Override
    public JsonElement jsonHandle(Request req, Response res) throws Exception {
        return handle(getNetwork(req), req, res);
    }
}
