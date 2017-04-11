package com.keenant.dhub.hub.web.route;

import com.google.gson.JsonElement;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Feature;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.web.NetworkProvider;
import com.keenant.dhub.hub.web.exception.UnsupportedFeatureException;
import spark.Request;
import spark.Response;

public abstract class FeatureGetRoute extends DeviceGetRoute {
    private final String featureParam;

    public FeatureGetRoute(NetworkProvider network, String deviceParam, String featureParam) {
        super(network, deviceParam);
        this.featureParam = featureParam;
    }

    public Feature getFeature(Network network, Device device, Request req) {
        return device.getFeature(req.params(featureParam)).orElseThrow(UnsupportedFeatureException::new);
    }

    public abstract JsonElement handle(Network network, Device device, Feature feature, Request req, Response res);

    @Override
    public JsonElement handle(Network network, Device device, Request req, Response res) {
        return handle(network, device, getFeature(network, device, req), req, res);
    }
}
