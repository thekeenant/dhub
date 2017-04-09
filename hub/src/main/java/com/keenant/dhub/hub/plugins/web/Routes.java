package com.keenant.dhub.hub.plugins.web;

import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Network;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

import java.util.Arrays;
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
            http.get("/networks", (req, res) -> hub.getNetworks());
            http.get("/networks/:network", networkGet());
            http.get("/networks/:network/:device", deviceGet());
//            http.get("/networks/:network/:device/:feature", featureGet());
//            http.put("/networks/:network/:device/:feature", featurePut());
        });
    }

    private Route networkGet() {
        return new NetworkRoute(hub) {
            @Override
            public Object handle(Network<?> network, Request req, Response res) {
                return network;
            }
        };
    }

    private Route deviceGet() {
        return new DeviceRoute(hub) {
            @Override
            public Object handle(Network<?> network, Device device, Request req, Response res) {
                return device;
            }
        };
    }
}
