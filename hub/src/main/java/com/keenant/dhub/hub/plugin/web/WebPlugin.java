package com.keenant.dhub.hub.plugin.web;

import com.google.gson.*;
import com.keenant.dhub.hub.network.*;
import com.keenant.dhub.hub.plugin.Plugin;
import com.keenant.dhub.hub.plugin.web.socket.NetworksWebSocket;
import io.airlift.airline.Cli.CliBuilder;
import spark.Request;
import spark.Response;
import spark.Service;

public class WebPlugin extends Plugin {
    private Service http;

    @Override
    public void load(CliBuilder<Runnable> cli) {
        http = Service.ignite();
    }

    @Override
    public void enable() {
        http.webSocket("/ws/networks/*", new NetworksWebSocket());

        http.get("/api/networks/:network", new NetworkRoute() {
            @Override
            protected JsonElement handle(Network<?> network, Request req, Response res) {
                JsonArray deviceArray = new JsonArray();
                for (Device device : network.getDevices()) {
                    deviceArray.add(new JsonPrimitive(device.getUniqueId()));
                }

                JsonObject json = new JsonObject();
                json.addProperty("id", network.getUniqueId());
                json.add("devices", deviceArray);
                return json;
            }
        });

        http.get("/api/networks/:network/devices/:device", new DeviceRoute() {
            @Override
            protected JsonElement handle(Device<?> device, Request req, Response res) {
                JsonObject features = new JsonObject();
                for (Feature<?, ?> feature : device.getFeatures()) {
                    features.add(feature.getUniqueId(), feature.jsonGet());
                }

                JsonObject providers = new JsonObject();
                for (Provider<?, ?> provider : device.getProviders()) {
                    providers.add(provider.getUniqueId(), provider.jsonGet());
                }

                JsonObject json = new JsonObject();
                json.addProperty("id", device.getUniqueId());
                json.add("features", features);
                json.add("providers", providers);
                return json;
            }
        });

        http.put("/api/networks/:network/devices/:device/features/:feature", new FeatureRoute() {
            @Override
            protected JsonElement handle(Feature<?, ?> feature, Request req, Response res) {
                JsonParser parser = new JsonParser();
                JsonObject element = parser.parse(req.body()).getAsJsonObject();
                feature.jsonSet(element);
                return feature.jsonGet();
            }
        });

        http.init();
    }

    @Override
    public void disable() {
        http.stop();
    }
}
