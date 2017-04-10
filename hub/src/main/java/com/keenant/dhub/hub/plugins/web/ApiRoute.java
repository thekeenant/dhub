package com.keenant.dhub.hub.plugins.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class ApiRoute implements Route {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public abstract JsonElement jsonHandle(Request request, Response response) throws Exception;

    @Override
    public Object handle(Request req, Response res) throws Exception {
        // may throw exceptions
        String body = gson.toJson(jsonHandle(req, res));

        res.type("application/json");

        return body;
    }
}
