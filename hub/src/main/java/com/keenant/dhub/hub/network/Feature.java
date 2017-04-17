package com.keenant.dhub.hub.network;

import lombok.ToString;

@ToString(callSuper = true)
public abstract class Feature<D extends Device, T> extends Provider<D, T> {
    public Feature(D device) {
        super(device);
    }

    protected abstract void send(T data);

    public void set(T data) {
        set(data);
        update();
    }
}
