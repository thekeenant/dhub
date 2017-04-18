package com.keenant.dhub.hub.plugin.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class JsonRoute implements Route {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected abstract JsonElement handleJson(Request req, Response res);

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String json = gson.toJson(handleJson(req, res));
        res.type("application/json");
        return json;
    }
}
