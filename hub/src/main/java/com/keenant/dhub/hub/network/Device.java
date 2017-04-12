package com.keenant.dhub.hub.network;

import java.util.ArrayList;
import java.util.List;

public abstract class Device {
    private final Network network;
    private final List<Ability> abilities = new ArrayList<>();
    private final List<Provider> providers = new ArrayList<>();

    public Device(Network network) {
        this.network = network;
    }

    public Network getNetwork() {
        return network;
    }

    protected void addAbility(Ability ability) {
        this.abilities.add(ability);
    }

    protected void addProvider(Provider provider) {
        this.providers.add(provider);
    }
}
