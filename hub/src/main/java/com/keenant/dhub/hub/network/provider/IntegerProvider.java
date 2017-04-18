package com.keenant.dhub.hub.network.provider;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Provider;
import com.keenant.dhub.hub.util.Transformer;
import lombok.ToString;

import java.util.Optional;

@ToString(callSuper = true)
public abstract class IntegerProvider<D extends Device> extends Provider<D, Integer> {
    public IntegerProvider(D device) {
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
}
