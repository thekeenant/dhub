package com.keenant.dhub.hub.plugins.web;

import com.google.gson.JsonElement;
import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Feature;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.plugins.web.exception.UnsupportedFeatureException;
import spark.Request;
import spark.Response;

public abstract class FeatureRoute extends DeviceRoute {
    public FeatureRoute(Hub hub) {
        super(hub);
    }

    public abstract JsonElement handle(Network network, Device device, Feature feature, Request req, Response res);

    @Override
    public JsonElement handle(Network network, Device device, Request req, Response res) {
        String name = req.params("feature");
        Feature feature = null;

        for (Feature test : device.getFeatures()) {
            if (test.getUniqueId().equals(name)) {
                feature = test;
                break;
            }
        }

        if (feature == null) {
            throw new UnsupportedFeatureException();
        }

        return handle(network, device, feature, req, res);
    }
}
