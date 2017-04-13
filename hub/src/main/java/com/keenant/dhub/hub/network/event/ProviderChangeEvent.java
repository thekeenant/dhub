package com.keenant.dhub.hub.network.event;

import com.keenant.dhub.hub.network.Provider;
import lombok.ToString;

@ToString
public class ProviderChangeEvent extends DeviceEvent {
    private final Provider provider;

    public ProviderChangeEvent(Provider provider) {
        super(provider.getDevice());
        this.provider = provider;
    }

    public Provider getProvider() {
        return provider;
    }
}
