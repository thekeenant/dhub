package com.keenant.dhub.hub.network.feature;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Feature;
import com.keenant.dhub.hub.util.Transformer;

import java.util.Optional;

public abstract class IntegerFeature<D extends Device> extends Feature<D, Integer> {
    public IntegerFeature(D device) {
        super(device);
    }

    @Override
    public String getDataType() {
        return "integer";
    }

    @Override
    public boolean isEqual(Integer before, Integer after) {
        return before.equals(after);
    }

    @Override
    protected Transformer<Optional<Integer>, JsonElement> toJson() {
        return (opt) -> opt.isPresent() ? new JsonPrimitive(opt.get()) : JsonNull.INSTANCE;
    }

    @Override
    protected Transformer<JsonElement, Integer> fromJson() {
        return JsonElement::getAsInt;
    }
}
