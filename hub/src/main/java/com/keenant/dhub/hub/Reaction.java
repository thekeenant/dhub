package com.keenant.dhub.hub;

import com.keenant.dhub.hub.action.Action;
import com.keenant.dhub.hub.action.ProviderChangeAction;
import com.keenant.dhub.hub.network.Provider;

public class Reaction extends ProviderChangeAction {
    private boolean stopped;

    public Reaction(Provider provider, Action action) {
        super(provider, action);
    }

    @Override
    public void execute() {
        stopped = false;
        while (!stopped) {
            super.execute();
        }
    }

    @Override
    public void stop() {
        stopped = true;
        super.stop();
    }
}
