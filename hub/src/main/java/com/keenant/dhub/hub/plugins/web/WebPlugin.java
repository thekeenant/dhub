package com.keenant.dhub.hub.plugins.web;

import com.keenant.dhub.hub.Plugin;
import io.airlift.airline.Cli.CliBuilder;
import spark.Service;

public class WebPlugin extends Plugin {
    private Service http;

    public WebPlugin() {

    }

    @Override
    public void init(CliBuilder<Runnable> cli) {
        http = Service.ignite();
        http.threadPool(8, 2, -1);
        http.port(4567);
    }

    @Override
    public void start() {
        http.before((req, res) -> {
            String path = req.pathInfo();
            if (path.length() > 1 && path.endsWith("/")) {
                res.redirect(path.substring(0, path.length() - 1));
            }
        });

        // Register routes
        Routes routes = new Routes(getHub());
        routes.setup(http);

        http.init();
    }

    public void api(String path, ServiceRouteGroup routeGroup) {
        http.path("/api/v1", () -> {
            http.path(path, () -> routeGroup.addRoutes(http));
        });
    }

    @Override
    public void stop() {

    }
}
