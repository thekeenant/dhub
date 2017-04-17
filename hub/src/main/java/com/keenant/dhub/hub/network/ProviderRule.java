package com.keenant.dhub.hub.network;

public abstract class ProviderRule<P extends Provider<?, T>, T> {
    private final P provider;

    public ProviderRule(P provider) {
        this.provider = provider;
    }

    public P getProvider() {
        return provider;
    }

    public boolean evaluate() {
        T value = provider.get().orElse(null);
        return value != null && evaluate(value);
    }

    protected abstract boolean evaluate(T current);
}