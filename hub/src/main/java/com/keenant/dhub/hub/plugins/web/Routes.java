package com.keenant.dhub.hub.plugins.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.network.*;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class Routes {
    private final Hub hub;

    public Routes(Hub hub) {
        this.hub = hub;
    }

    public void setup(Service http) {
        http.exception(Exception.class, (e, req, res) -> {
            String trace = Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.joining("<br/>"));
            res.body("<h1>" + e.getClass().getSimpleName() + ": " + e.getMessage() + "</h1><p>" + trace + "</p>");
        });

        http.path("/api/v1", () -> {
            http.get("/networks", new ApiRoute() {
                @Override
                public JsonElement jsonHandle(Request request, Response response) throws Exception {
                    JsonObject json = new JsonObject();
                    for (Network network : hub.getNetworks()) {
                        json.add(network.getUniqueId(), network.toJson());
                    }
                    return json;
                }
            });
            http.get("/networks/:network", networkGet());
            http.get("/networks/:network/:device", deviceGet());

            http.get("/networks/:network/:device/:feature", featureGet());
            http.put("/networks/:network/:device/:feature", featurePut());

            http.get("/networks/:network/:device/:feature/*", featureGetPath());
            http.put("/networks/:network/:device/:feature/*", featurePutPath());

        });
    }

    private Route networkGet() {
        return new NetworkRoute(hub) {
            @Override
            public JsonElement handle(Network network, Request req, Response res) {
                return network.toJson();
            }
        };
    }

    private Route deviceGet() {
        return new DeviceRoute(hub) {
            @Override
            public JsonElement handle(Network network, Device device, Request req, Response res) {
                return device.toJson();
            }
        };
    }

    private Route featureGetPath() {
        return new FeatureRoute(hub) {
            @Override
            public JsonElement handle(Network network, Device device, Feature feature, Request req, Response res) {
                String[] splat = req.splat()[0].split("/");


                System.out.println(Arrays.toString(splat));

                if (feature instanceof DataFeature) {
                    DataFeature data = (DataFeature) feature;
                    if (splat.length > 0) {
                        JsonElement json = data.toJson();
                        for (String curr : splat) {
                            if (json instanceof JsonObject) {
                                json = json.getAsJsonObject().get(curr);
                            }
                        }
                        return json;
                    }

                    return data.toJson();
                }

                // todo
                return new JsonPrimitive(false);
            }
        };
    }

    private Route featurePutPath() {
        JsonParser parser = new JsonParser();

        return new FeatureRoute(hub) {
            @Override
            public JsonElement handle(Network network, Device device, Feature feature, Request req, Response res) {
                String[] splat = req.splat()[0].split("/");

                if (feature instanceof ResponsiveFeature) {
                    ResponsiveFeature responsive = (ResponsiveFeature) feature;

                    JsonElement json = parser.parse(req.body());
                    if (splat.length > 0) {
                        Arrays.sort(splat, Collections.reverseOrder());

                        for (String curr : splat) {
                            JsonObject wrap = new JsonObject();
                            wrap.add(curr, json);
                            json = wrap;
                        }
                    }

                    responsive.respondTo(json);

                    // todo
                    return new JsonPrimitive(true);
                }

                // todo
                return new JsonPrimitive(false);
            }
        };
    }

    private Route featureGet() {
        return new FeatureRoute(hub) {
            @Override
            public JsonElement handle(Network network, Device device, Feature feature, Request req, Response res) {
                if (feature instanceof DataFeature) {
                    DataFeature data = (DataFeature) feature;
                    return data.toJson();
                }

                // todo
                return new JsonPrimitive(false);
            }
        };
    }

    private Route featurePut() {
        JsonParser parser = new JsonParser();

        return new FeatureRoute(hub) {
            @Override
            public JsonElement handle(Network network, Device device, Feature feature, Request req, Response res) {
                if (feature instanceof ResponsiveFeature) {
                    ResponsiveFeature responsive = (ResponsiveFeature) feature;
                    responsive.respondTo(parser.parse(req.body()));

                    if (feature instanceof DataFeature) {
                        DataFeature data = (DataFeature) feature;
                        return data.toJson();
                    }

                    // todo
                    return new JsonPrimitive(true);
                }

                // todo
                return new JsonPrimitive(false);
            }
        };
    }
}
