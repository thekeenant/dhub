package com.keenant.dhub.hub.plugins.web;

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

    public abstract Object handle(Network<?> network, Device<?> device, Feature feature, Request req, Response res);

    @SuppressWarnings("unchecked")
    @Override
    public Object handle(Network<?> network, Device<?> device, Request req, Response res) {
        Feature feature = device.getFeature(req.params("feature")).orElseThrow(UnsupportedFeatureException::new);
        return handle(network, device, feature, req, res);
    }
}
