package com.keenant.dhub.hub.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.event.ProviderChangeEvent;
import com.keenant.dhub.hub.util.Transformer;
import lombok.ToString;

import java.util.Optional;

@ToString(exclude = {"device"})
public abstract class Provider<D extends Device, T> {
    private final D device;

    private T lastValue;

    public Provider(D device) {
        this.device = device;
    }

    public abstract String getUniqueId();

    public abstract String getDataType();

    protected abstract Optional<T> fetch();

    protected abstract boolean isEqual(T before, T after);

    protected abstract Transformer<Optional<T>, JsonElement> toJson();

    public void update() {
        T prevValue = lastValue;
        T newValue = fetch().orElse(null);
        lastValue = newValue;

        boolean changed;

        if (prevValue == null && newValue == null) {
            changed = true;
        }
        else if ((prevValue == null) != (newValue == null)) {
            changed = true;
        }
        else {
            changed = !isEqual(prevValue, newValue);
        }

        System.out.println("Changed? " + changed + " (" + this + ")");

        if (changed) {
            ProviderChangeEvent event = new ProviderChangeEvent(this);
            device.getNetwork().publish(event);
        }
    }

    public Optional<T> get() {
        return Optional.ofNullable(lastValue);
    }

    public JsonElement jsonGet() {
        JsonObject json = new JsonObject();
        json.add("data", toJson().transform(get()));
        json.addProperty("data-type", getDataType());
        return json;
    }

    public D getDevice() {
        return device;
    }
}
