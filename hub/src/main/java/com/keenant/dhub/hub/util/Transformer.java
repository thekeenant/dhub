package com.keenant.dhub.hub.util;

@FunctionalInterface
public interface Transformer<I, O> {
    O transform(I input);
}
