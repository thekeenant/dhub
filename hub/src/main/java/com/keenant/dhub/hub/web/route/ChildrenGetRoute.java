package com.keenant.dhub.hub.web.route;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Feature;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.network.feature.ChildrenFeature;
import com.keenant.dhub.hub.web.NetworkProvider;
import com.keenant.dhub.hub.web.exception.UnsupportedFeatureException;
import spark.Request;

public class ChildrenGetRoute implements NetworkProvider {
    private final FeatureGetRoute baseRoute;

    public ChildrenGetRoute(FeatureGetRoute baseRoute) {
        this.baseRoute = baseRoute;
    }

    @Override
    public ChildrenFeature<?> getNetwork(Request req) {
        Network network = baseRoute.getNetwork(req);
        Device device = baseRoute.getDevice(network, req);
        Feature feature = baseRoute.getFeature(network, device, req);
        if (feature instanceof ChildrenFeature) {
            return (ChildrenFeature<?>) feature;
        }

        throw new UnsupportedFeatureException();
    }
}
