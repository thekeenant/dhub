package com.keenant.dhub.hub.plugin.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Feature;
import spark.Request;
import spark.Response;

public abstract class FeatureRoute extends DeviceRoute {
    protected abstract JsonElement handle(Feature<?, ?> feature, Request req, Response res);

    @Override
    protected JsonElement handle(Device<?> device, Request req, Response res) {
        Feature<?, ?> feature = device.getFeature(req.params("feature")).orElseThrow(RuntimeException::new);
        return handle(feature, req, res);
    }
}
