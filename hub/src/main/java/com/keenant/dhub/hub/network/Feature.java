package com.keenant.dhub.hub.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keenant.dhub.hub.util.Transformer;
import lombok.ToString;

@ToString(callSuper = true)
public abstract class Feature<D extends Device, T> extends Provider<D, T> {
    public Feature(D device) {
        super(device);
    }

    protected abstract void send(T data);

    protected abstract Transformer<JsonElement, T> fromJson();

    public void set(T data) {
        send(data);
        update();
    }

    public void jsonSet(JsonObject input) {
        if (input.has("data")) {
            JsonElement data = input.get("data");

            T transformed = fromJson().transform(data);
            set(transformed);
        }
    }
}
