package com.keenant.dhub.hub.network;

public interface ProviderRule<T> {
    boolean evaluate(T current);
}
