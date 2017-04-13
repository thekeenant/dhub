package com.keenant.dhub.hub.network;

public abstract class ProviderRule<P extends Provider<T>, T> {
    private final P provider;

    public ProviderRule(P provider) {
        this.provider = provider;
    }

    public P getProvider() {
        return provider;
    }

    public boolean evaluate() {
        return evaluate(provider.get());
    }

    protected abstract boolean evaluate(T current);
}