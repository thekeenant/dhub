package com.keenant.dhub.hub.network;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToString(exclude = "network")
public abstract class Device<T extends Network> {
    private final T network;
    private final List<Ability> abilities = new ArrayList<>();
    private final List<Provider> providers = new ArrayList<>();

    public Device(T network) {
        this.network = network;
    }

    public abstract void start();

    public abstract void stop();

    public T getNetwork() {
        return network;
    }

    protected <A extends Ability> A addAbility(A ability) {
        this.abilities.add(ability);
        return ability;
    }

    protected <P extends Provider> P addProvider(P provider) {
        this.providers.add(provider);
        return provider;
    }

    @SuppressWarnings("unchecked")
    public <A extends Ability> Optional<A> getAbility(Class<A> type) {
        for (Ability ability : abilities) {
            if (type.isInstance(ability)) {
                return Optional.of((A) ability);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <P extends Provider> Optional<P> getProvider(Class<P> type) {
        for (Provider provider : providers) {
            if (type.isInstance(provider)) {
                return Optional.of((P) provider);
            }
        }
        return Optional.empty();
    }
}
