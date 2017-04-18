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
public abstract class BooleanProvider<D extends Device> extends Provider<D, Boolean> {
    public BooleanProvider(D device) {
        super(device);
    }

    @Override
    public String getDataType() {
        return "boolean";
    }

    @Override
    public boolean isEqual(Boolean before, Boolean after) {
        return before.equals(after);
    }

    @Override
    protected Transformer<Optional<Boolean>, JsonElement> toJson() {
        return (opt) -> opt.isPresent() ? new JsonPrimitive(opt.get()) : JsonNull.INSTANCE;
    }
}
