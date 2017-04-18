package com.keenant.dhub.hub.network.feature;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Feature;
import com.keenant.dhub.hub.util.Transformer;

import java.util.Optional;

public abstract class BooleanFeature<D extends Device> extends Feature<D, Boolean> {
    public BooleanFeature(D device) {
        super(device);
    }

    @Override
    public String getDataType() {
        return "boolean";
    }

    @Override
    protected boolean isEqual(Boolean before, Boolean after) {
        return before == after;
    }

    @Override
    protected Transformer<Optional<Boolean>, JsonElement> toJson() {
        return (opt) -> opt.isPresent() ? new JsonPrimitive(opt.get()) : JsonNull.INSTANCE;
    }

    @Override
    protected Transformer<JsonElement, Boolean> fromJson() {
        return JsonElement::getAsBoolean;
    }
}
