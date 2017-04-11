package com.keenant.dhub.hub.web;

import com.google.gson.*;
import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.network.*;
import com.keenant.dhub.hub.web.exception.NetworkNotFoundException;
import com.keenant.dhub.hub.web.route.*;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Routes {
    private static final JsonParser parser = new JsonParser();

    private static Hub hub;
    private static NetworkProvider network;

    public static void setup(Hub hub, Service http) {
        // Web Socket
        EventsSocket socket = new EventsSocket("/events/v1");
        http.webSocket("/events/v1/*", socket);
        http.webSocket("/events/v1", socket);

        Routes.hub = hub;
        Routes.network = req -> {
            String param = req.params("network");
            return hub.getNetwork(param).orElseThrow(NetworkNotFoundException::new);
        };

        http.exception(Exception.class, (e, req, res) -> {
            String trace = Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.joining("<br/>"));
            res.body("<h1>" + e.getClass().getSimpleName() + ": " + e.getMessage() + "</h1><p>" + trace + "</p>");
        });

        http.path("/api/v1", () -> {
            http.get("/networks", networksGet());
            http.get("/networks/:network", networkGet());

            http.path("/networks/:network", () -> {
                http.get("/devices", networkDevicesGet(network));
                http.get("/devices/:device", deviceGet(network, "device"));
                http.get("/devices/:device/features/:feature", featureGet(network, "device", "feature"));
                http.put("/devices/:device/features/:feature", featurePut(network, "device", "feature"));

                http.path("/devices/:device/features/:feature", () -> {
                    FeatureGetRoute baseRoute = featureGet(network, "device", "feature");
                    ChildrenGetRoute route = new ChildrenGetRoute(baseRoute);

                    http.get("/devices", networkDevicesGet(route));
                    http.get("/devices/:device2", deviceGet(route, "device2"));
                    http.get("/devices/:device2/features/:feature2", featureGet(route, "device2", "feature2"));
                    http.put("/devices/:device2/features/:feature2", featurePut(route, "device2", "feature2"));
                });
            });

        });
    }

    private static Route networksGet() {
        return new ApiGetRoute() {
            @Override
            public JsonElement jsonHandle(Request req, Response res) throws Exception {
                JsonArray networkJson = new JsonArray();
                for (Network network : hub.getNetworks()) {
                    networkJson.add(new JsonPrimitive(network.getUniqueId()));
                }

                JsonObject json = new JsonObject();
                json.add("networks", networkJson);
                return json;
            }
        };
    }

    private static NetworkGetRoute networkGet() {
        return new NetworkGetRoute(network) {
            @Override
            public JsonElement handle(Network network, Request req, Response res) throws Exception {
                return network.toJson();
            }
        };
    }

    private static NetworkGetRoute networkDevicesGet(NetworkProvider network) {
        return new NetworkGetRoute(network) {
            @Override
            public JsonElement handle(Network network, Request req, Response res) throws Exception {
                return network.getDevices().toJson();
            }
        };
    }

    private static DeviceGetRoute deviceGet(NetworkProvider network, String param) {
        return new DeviceGetRoute(network, param) {
            @Override
            public JsonElement handle(Network network, Device device, Request req, Response res) {
                return device.toJson();
            }
        };
    }

    private static FeatureGetRoute featureGet(NetworkProvider network, String deviceParam, String featureParam) {
        return new FeatureGetRoute(network, deviceParam, featureParam) {
            @Override
            public JsonElement handle(Network network, Device device, Feature feature, Request req, Response res) {
                if (feature instanceof DataFeature) {
                    return ((DataFeature) feature).toJson();
                }
                return new JsonPrimitive(true);
            }
        };
    }

    private static FeatureGetRoute featurePut(NetworkProvider network, String deviceParam, String featureParam) {
        return new FeatureGetRoute(network, deviceParam, featureParam) {
            @Override
            public JsonElement handle(Network network, Device device, Feature feature, Request req, Response res) {
                JsonElement json = parser.parse(req.body());

                if (feature instanceof ResponsiveFeature) {
                    ((ResponsiveFeature) feature).respondTo(json);
                    return new JsonPrimitive(true);
                }

                // Todo
                throw new RuntimeException("Feature doesn't support data.");
            }
        };
    }

}
