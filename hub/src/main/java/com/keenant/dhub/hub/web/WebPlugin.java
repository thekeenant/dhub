package com.keenant.dhub.hub.web;

import com.keenant.dhub.hub.plugin.Plugin;
import io.airlift.airline.Cli.CliBuilder;
import spark.Service;
import spark.route.RouteOverview;

public class WebPlugin extends Plugin {
    private Service service;

    public WebPlugin() {

    }

    @Override
    public void init(CliBuilder<Runnable> cli) {
        service = Service.ignite();
    }

    @Override
    public void start() {
        service.ipAddress("localhost");
        service.port(8080);
        RouteOverview.enableRouteOverview("/__routes__");
        service.init();

        service.path("/test", () -> {
            service.get("/derp", (req, res) -> this);
        });
    }

    @Override
    public void stop() {

    }
}
