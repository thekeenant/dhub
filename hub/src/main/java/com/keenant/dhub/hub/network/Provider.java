package com.keenant.dhub.hub.network;

import com.keenant.dhub.hub.network.event.ProviderChangeEvent;
import lombok.ToString;

import java.util.function.Supplier;

@ToString(exclude = {"device", "supplier"})
public abstract class Provider<T> {
    private final Device<?> device;
    private final Supplier<T> supplier;

    private T lastValue;

    public Provider(Device<?> device, Supplier<T> supplier, T defValue) {
        this.device = device;
        this.supplier = supplier;
        this.lastValue = defValue;
    }

    public abstract boolean isEqual(T before, T after);

    private T getNewValue() {
        T value = supplier.get();
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

    public T get() {
        return lastValue;
    }

    public Device getDevice() {
        return device;
    }
}
