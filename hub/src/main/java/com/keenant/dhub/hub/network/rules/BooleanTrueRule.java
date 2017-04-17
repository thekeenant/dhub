package com.keenant.dhub.hub.network.rules;

import com.keenant.dhub.hub.network.ProviderRule;
import com.keenant.dhub.hub.network.provider.BooleanProvider;

import java.util.Optional;

public class BooleanTrueRule extends ProviderRule<BooleanProvider<?>, Boolean> {
    public BooleanTrueRule(BooleanProvider provider) {
        super(provider);
    }

    @Override
    protected boolean evaluate(Boolean current) {
        return current;
    }
}
