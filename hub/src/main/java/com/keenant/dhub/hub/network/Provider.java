package com.keenant.dhub.hub.network;

import com.keenant.dhub.hub.network.event.ProviderChangeEvent;
import lombok.ToString;

import java.util.Optional;

@ToString(exclude = {"device"})
public abstract class Provider<D extends Device, T> {
    private final D device;

    private T lastValue;

    public Provider(D device) {
        this.device = device;
    }

    public abstract Optional<T> fetch();

    public abstract boolean isEqual(T before, T after);

    private T getNewValue() {
        T value = fetch().orElse(null);
        if (value == null) {
            throw new RuntimeException(getClass().getName() + " supplied a null value.");
        }
        return value;
    }

    public void update() {
        T newValue = getNewValue();
        T prevValue = lastValue;
        lastValue = newValue;

        if (prevValue != null && !isEqual(prevValue, newValue)) {
            ProviderChangeEvent event = new ProviderChangeEvent(this);
            device.getNetwork().publish(event);
        }
    }

    public Optional<T> get() {
        return Optional.ofNullable(lastValue);
    }

    public D getDevice() {
        return device;
    }
}
