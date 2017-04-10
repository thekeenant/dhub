package com.keenant.dhub.core.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map.Entry;

public class JsonUtil {
    public static JsonObject merge(JsonObject json1, JsonObject json2) {
        JsonObject json = new JsonObject();

        for (Entry<String, JsonElement> entry : json1.entrySet()) {
            json.add(entry.getKey(), entry.getValue());
        }

        for (Entry<String, JsonElement> entry : json2.entrySet()) {
            json.add(entry.getKey(), entry.getValue());
        }

        return json;
    }
}
