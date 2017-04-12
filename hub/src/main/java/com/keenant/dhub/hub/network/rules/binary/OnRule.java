package com.keenant.dhub.hub.network.rules.binary;

import com.keenant.dhub.hub.network.ProviderRule;

import java.util.Optional;

public class OnRule implements ProviderRule<Optional<Boolean>> {
    @Override
    public boolean evaluate(Optional<Boolean> current) {
        return current.orElse(false);
    }
}
