package com.keenant.dhub.hub.web;

import com.keenant.dhub.hub.plugins.Plugin;
import io.airlift.airline.Cli.CliBuilder;
import spark.Request;
import spark.Response;
import spark.Service;
import spark.Spark;
import spark.route.RouteOverview;

public class WebPlugin extends Plugin {
    public WebPlugin() {
    }

    @Override
    public void init(CliBuilder<Runnable> cli) {
    }

    @Override
    public void start() {
        Spark.ipAddress("localhost");
        Spark.port(8080);

        RouteOverview.enableRouteOverview("/__routes__");

        Spark.get("/api/v1", this::test);
        Spark.get("/api/v1/:name", this::test);



        Spark.init();
    }

    private Object test(Request req, Response res) {
        return "test";
    }

    @Override
    public void stop() {

    }
}
